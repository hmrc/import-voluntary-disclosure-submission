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

import akka.actor.{Actor, ActorRef}
import akka.pattern.pipe
import connectors.FileTransferConnector
import models.SupportingDocument
import models.requests.FileTransferRequest
import models.responses.FileTransferResponse
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

// $COVERAGE-OFF$Code taken from another [Route 1] service
class FileTransferProcessor(caseReferenceNumber: String,
                            fileTransferConnector: FileTransferConnector,
                            conversationId: String,
                            audit: Seq[FileTransferResponse] => Future[Unit],
                            implicit val executionContext: ExecutionContext
                           ) extends Actor with Logging {

  import FileTransferProcessor._

  var results: Seq[FileTransferResponse] = Seq.empty
  var clientRef: ActorRef = ActorRef.noSender
  var startTimestamp: Long = 0

  def transferFileRequest(file: SupportingDocument, index: Int, batchSize: Int): FileTransferRequest =
    FileTransferRequest.fromSupportingDocument(
      caseReferenceNumber,
      conversationId,
      correlationId = UUID.randomUUID().toString,
      applicationName = "C18",
      batchSize = batchSize,
      batchCount = index + 1,
      uploadedFile = file
    )

  def doTransferFile(file: SupportingDocument, index: Int, batchSize: Int)
                    (implicit hc: HeaderCarrier): Future[FileTransferResponse] =
    fileTransferConnector.transferFile(transferFileRequest(file, index, batchSize))

  override def receive: Receive = {
    case TransferMultipleFiles(files, batchSize, headerCarrier) =>
      startTimestamp = System.currentTimeMillis()
      clientRef = sender()
      files.map {
        case (file, index) => TransferSingleFile(file, index, batchSize, headerCarrier)
      }.foreach(request => self ! request)
      self ! CheckComplete(batchSize)

    case TransferSingleFile(file, index, batchSize, headerCarrier) =>
      doTransferFile(file, index, batchSize)(headerCarrier)
        .pipeTo(sender())

    case result: FileTransferResponse =>
      results = results :+ result

    case akka.actor.Status.Failure(error@UpstreamErrorResponse(message, code, _, _)) =>
      logger.error(error.toString)
      results = results :+ FileTransferResponse(
        upscanReference = "<unknown>",
        fileName = "",
        fileMimeType = "",
        fileTransferSuccess = false,
        LocalDateTime.now(),
        0,
        fileTransferError = Some(message)
      )

    case akka.actor.Status.Failure(error) =>
      logger.error(error.toString)
      results = results :+ FileTransferResponse(
        upscanReference = "<unknown>",
        fileName = "",
        fileMimeType = "",
        fileTransferSuccess = false,
        LocalDateTime.now(),
        0,
        fileTransferError = Some(error.toString)
      )

    case CheckComplete(batchSize) =>
      if (results.size == batchSize || System.currentTimeMillis() - startTimestamp > 3600000 /*hour*/ ) {
        clientRef ! results
        audit(results)
        context.stop(self)
        logger.info(completionMessage(results, batchSize, startTimestamp))
      } else {
        context.system.scheduler.scheduleOnce(1.second, self, CheckComplete(batchSize))
      }
  }

  def completionMessage(transferRequests: Seq[FileTransferResponse], batchSize: Int, startTime: Long): String = {
    val totalRequests = transferRequests.size
    val successCount = transferRequests.count(_.fileTransferSuccess)
    val failureCount = transferRequests.count(!_.fileTransferSuccess)
    val timeToCompleteInSeconds = (System.currentTimeMillis() - startTime) / 1000
    s"Transferred $totalRequests out of $batchSize files in $timeToCompleteInSeconds seconds. With $successCount successes and $failureCount failures."
  }
}

object FileTransferProcessor {
  case class TransferMultipleFiles(files: Seq[(SupportingDocument, Int)], batchSize: Int, hc: HeaderCarrier)

  case class TransferSingleFile(file: SupportingDocument, index: Int, batchSize: Int, hc: HeaderCarrier)

  case class CheckComplete(batchSize: Int)
}

// $COVERAGE-ON$
