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

package connectors

import config.AppConfig
import models.requests.FileTransferRequest
import models.responses.FileTransferResponse
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import java.time.LocalDateTime
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileTransferConnector @Inject()(val appConfig: AppConfig,
                                      val http: HttpClient) {

  private[connectors] lazy val url = s"${appConfig.fileTransferUrl}/transfer-file"
  private val logger = Logger("application." + getClass.getCanonicalName)

  def transferFile(fileTransferRequest: FileTransferRequest)
                  (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FileTransferResponse] = {
    http.POST[FileTransferRequest, HttpResponse](url, fileTransferRequest).map { response =>

      if (isSuccess(response.status)) {
        logger.info(s"[FILE TRANSFER SUCCESS][REFERENCE: ${fileTransferRequest.upscanReference}][CORRELATION ID: ${fileTransferRequest.correlationId}][CONVERSATION ID: ${fileTransferRequest.conversationId}]")
      } else {
        logger.error(s"[FILE TRANSFER FAILURE][REFERENCE: ${fileTransferRequest.upscanReference}][CORRELATION ID: ${fileTransferRequest.correlationId}][CONVERSATION ID: ${fileTransferRequest.conversationId}]")
      }

      FileTransferResponse(
        fileTransferRequest.upscanReference,
        isSuccess(response.status),
        LocalDateTime.now(),
        errorMessage(response.status)
      )
    }.recover {
      case error =>
        logger.error(
          s"""[FILE TRANSFER FAILURE][REFERENCE: ${fileTransferRequest.upscanReference}][CORRELATION ID: ${fileTransferRequest.correlationId}][CONVERSATION ID: ${fileTransferRequest.conversationId}]
             | [MESSAGE: ${error.getMessage}]""".stripMargin)

        FileTransferResponse(
          fileTransferRequest.upscanReference,
          success = false,
          LocalDateTime.now(),
          Some(error.getMessage)
        )
    }
  }

  private def isSuccess(status: Int): Boolean = status == Status.ACCEPTED

  final def errorMessage(status: Int): Option[String] = {
    if (isSuccess(status)) {
      None
    } else {
      Some(s"HTTP response status $status")
    }
  }
}
