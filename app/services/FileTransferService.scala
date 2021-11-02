/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import akka.actor.ActorSystem
import akka.pattern.after
import config.AppConfig
import connectors.FileTransferConnector
import models.SupportingDocument
import models.audit.FilesUploadedAuditEvent
import models.requests.{FileTransferRequest, MultiFileTransferRequest}
import models.responses.FileTransferResponse
import play.api.Logger
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

@Singleton
class FileTransferService @Inject() (
  actorSystem: ActorSystem,
  connector: FileTransferConnector,
  auditService: AuditService,
  config: AppConfig
) {

  private val logger = Logger("application." + getClass.getCanonicalName)
  val MAX_RETRIES    = 2

  def newCorrelationId(): String =
    UUID.randomUUID().toString

  def transferFiles(caseId: String, conversationId: String, files: Seq[SupportingDocument])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    request: Request[_]
  ): Future[Unit] = {
    if (config.multiFileUploadEnabled) {
      batchTransfer(caseId, conversationId, files)
    } else {
      simpleTransfer(caseId, conversationId, files)
    }
  }

  private def batchTransfer(caseId: String, conversationId: String, files: Seq[SupportingDocument])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    request: Request[_]
  ): Future[Unit] = {
    val correlationId = newCorrelationId()
    val req = MultiFileTransferRequest.fromSupportingDocuments(
      caseReferenceNumber = caseId,
      conversationId = conversationId,
      correlationId = correlationId,
      applicationName = "C18",
      uploadedFiles = files,
      callbackUrl = config.fileUploadCallbackUrl
    )

    def tryTransfer(counter: Int): Future[Unit] =
      connector.transferMultipleFiles(req).flatMap {
        case Left(_) if counter <= MAX_RETRIES =>
          after(1.second * counter, actorSystem.scheduler)(tryTransfer(counter + 1))
        case failure @ Left(err) =>
          logger.error(s"The request to submit file transfer for case '$caseId' has failed: ${err.message}")
          val resps = files.map(file =>
            FileTransferResponse(
              upscanReference = file.reference,
              fileName = file.fileName,
              fileMimeType = file.fileMimeType,
              fileTransferSuccess = false,
              transferredAt = file.uploadTimestamp,
              correlationId = Some(correlationId),
              duration = 0
            )
          )
          auditFileTransfers(resps, caseId)

          Future.successful(failure)
        case Right(res) => Future.successful(res)
      }

    tryTransfer(1)
  }

  private def simpleTransfer(caseId: String, conversationId: String, files: Seq[SupportingDocument])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    request: Request[_]
  ): Future[Unit] = {

    val context: ExecutionContext = actorSystem.dispatchers.lookup("offline-dispatchers")

    val requests: Seq[FileTransferRequest] = files.zipWithIndex.map { case (file, index) =>
      FileTransferRequest.fromSupportingDocument(
        caseReferenceNumber = caseId,
        conversationId = conversationId,
        correlationId = UUID.randomUUID().toString,
        applicationName = "C18",
        batchSize = files.size,
        batchCount = index + 1,
        uploadedFile = file
      )
    }

    actorSystem.scheduler.scheduleOnce(0 milliseconds) {
      val allResponses: Future[List[FileTransferResponse]] =
        requests.foldLeft(Future(List.empty[FileTransferResponse])) { (previousResponses, req) ⇒
          for {
            responses ← previousResponses
            response  ← connector.transferFile(req)(hc, implicitly)
          } yield responses :+ response
        }
      allResponses.map(file => auditFileTransfers(file, caseId))
        .onComplete {
          case Success(success)      => logger.info("Successfully transferred files")
          case Failure(errormessage) => logger.error(errormessage.toString)
        }
    }(context)

    Future.successful {}
  }

  private def auditFileTransfers(results: Seq[FileTransferResponse], caseId: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext,
    request: Request[_]
  ): Unit = {
    val summaryMessage =
      s"""Case ID: $caseId
         |Failed Transfers: ${results.filter(!_.fileTransferSuccess).map(_.correlationId).mkString(", ")}
         |Total Size: ${results.size}
         |Success: ${results.count(_.fileTransferSuccess)}
         |Failed: ${results.count(!_.fileTransferSuccess)}""".stripMargin
    if (results.forall(_.fileTransferSuccess)) {
      logger.info(summaryMessage)
    } else {
      logger.error("File upload has partially failed", new Exception(summaryMessage))
    }
    auditService.audit(FilesUploadedAuditEvent(results, caseId))
  }

}
