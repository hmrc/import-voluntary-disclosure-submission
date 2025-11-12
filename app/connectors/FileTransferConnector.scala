/*
 * Copyright 2023 HM Revenue & Customs
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
import models.ErrorModel
import models.requests.MultiFileTransferRequest
import play.api.Logger
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileTransferConnector @Inject() (val appConfig: AppConfig, val http: HttpClientV2) {

  private[connectors] lazy val multiFileUrl = s"${appConfig.fileTransferUrl}/transfer-multiple-files"
  private val logger                        = Logger(getClass)
  def transferMultipleFiles(
    fileTransferRequest: MultiFileTransferRequest
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorModel, Unit]] = {
    http.post(url"$multiFileUrl").withBody(Json.toJson(fileTransferRequest)).execute[HttpResponse].map { response =>
      if (isSuccess(response.status)) {
        logger.info(
          s"[FILE TRANSFER SUCCESS][CORRELATION ID: ${fileTransferRequest.correlationId}][CONVERSATION ID: ${fileTransferRequest.conversationId}]"
        )
        Right(())
      } else {
        logger.error(s"""[FILE TRANSFER FAILURE]
             |[CORRELATION ID: ${fileTransferRequest.correlationId}]
             |[CONVERSATION ID: ${fileTransferRequest.conversationId}]
             |[STATUS: ${response.status}]""".stripMargin)
        Left(ErrorModel(response.status, "Unsuccessful file transfer response"))
      }

    }.recover { case error =>
      logger.error(s"""[FILE TRANSFER FAILURE]
             |[CORRELATION ID: ${fileTransferRequest.correlationId}]
             |[CONVERSATION ID: ${fileTransferRequest.conversationId}]
             |[MESSAGE: ${error.getMessage}]""".stripMargin)
      Left(ErrorModel(-1, error.toString))
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
