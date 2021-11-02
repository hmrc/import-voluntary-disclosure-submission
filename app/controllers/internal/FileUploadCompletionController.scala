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

package controllers.internal

import models.audit.FilesUploadedAuditEvent
import models.responses.{FileTransferResponse, MultiFileTransferResponse}
import play.api.Logging
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents, Request}
import services.AuditService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class FileUploadCompletionController @Inject() (
  cc: ControllerComponents,
  auditService: AuditService,
  implicit val ec: ExecutionContext
) extends BackendController(cc)
    with Logging {

  def onSubmit(): Action[JsValue] = Action(parse.json).apply { implicit request =>
    request.body.validate[MultiFileTransferResponse] match {
      case JsSuccess(response, _) =>
        val results = response.results.map { res =>
          FileTransferResponse(
            upscanReference = res.upscanReference,
            fileName = res.fileName,
            fileMimeType = res.fileMimeType,
            fileTransferSuccess = res.success,
            transferredAt = res.transferredAt,
            correlationId = Some(res.correlationId),
            duration = res.durationMillis,
            fileTransferError = res.error
          )
        }
        auditFileTransfers(results, response.caseReferenceNumber)

        NoContent
      case JsError(errors) =>
        val pathsWithErrors: Map[String, String] = errors.map { error =>
          val (path, errors) = error
          path.toString().substring(1) -> errors.head.message
        }.toMap
        BadRequest(Json.obj("errors" -> pathsWithErrors))
    }
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
