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
import connectors.FileTransferConnector
import models.SupportingDocument
import models.requests.FileTransferRequest
import models.responses.FileTransferResponse
import uk.gov.hmrc.http.HeaderCarrier

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

@Singleton
class FileTransferService @Inject()(actorSystem: ActorSystem,
                                    connector: FileTransferConnector) {

  def transferFiles(caseId: String, conversationId: String, files: Seq[SupportingDocument])
                   (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {
    simpleTransfer(caseId, conversationId, files)
  }

  //  private def actorTransfer(caseId: String, conversationId: String, files: Seq[SupportingDocument])
  //                           (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = Future {
  //    val fileTransferActor: ActorRef = actorSystem.actorOf(
  //      Props(classOf[FileTransferProcessor], caseId, connector, conversationId, auditFileTransfers)
  //    )
  //
  //    fileTransferActor ! FileTransferProcessor.TransferMultipleFiles(
  //      files.zipWithIndex,
  //      files.size,
  //      hc
  //    )
  //  }

  private def simpleTransfer(caseId: String, conversationId: String, files: Seq[SupportingDocument])
                            (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] = {

    val context: ExecutionContext = actorSystem.dispatchers.lookup("offline-dispatchers")

    val requests: Seq[FileTransferRequest] = files.zipWithIndex.map {
      case (file, index) =>
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
      val allResponses = requests.foldLeft(Future(List.empty[FileTransferResponse])) {
        (previousResponses, req) ⇒
          for {
            responses ← previousResponses
            response ← connector.transferFile(req)(hc, implicitly)
          } yield responses :+ response
      }

      allResponses.flatMap(auditFileTransfers)
    }(context)

    Future.successful({})
  }

  private val auditFileTransfers: Seq[FileTransferResponse] => Future[Unit] = results => {
    val message = results.map {
      case response if response.success => s"[${response.transferredAt}][SUCCESS] - Reference ${response.upscanReference}"
      case response => s"[${response.transferredAt}][FAILURE] - Reference ${response.upscanReference}\n\t${response.error}"
    }

    val summaryMessage = s"\nTotal Size: ${results.size} | Success: ${results.count(_.success)} | Failed: ${results.count(!_.success)}\n\n"
    println(message.mkString("\n") + summaryMessage)
    Future.successful({})
  }

}
