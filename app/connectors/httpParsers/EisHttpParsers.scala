/*
 * Copyright 2022 HM Revenue & Customs
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

package connectors.httpParsers

import models.responses.{CreateCaseResponse, UpdateCaseResponse}
import models.{EisError, UpdateCaseError}
import play.api.Logging
import play.api.http.Status
import play.api.libs.json.{JsError, JsPath, JsSuccess, JsonValidationError, Reads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object EisHttpParsers {

  implicit val createCaseHttpParser: HttpReads[Either[EisError, CreateCaseResponse]] =
    jsonParser("IVD - Create Case") { correlationId =>
      json =>
        (json \ "CaseID").validate[String].map(CreateCaseResponse(_, correlationId))
    }

  implicit val updateCaseHttpParser: HttpReads[Either[UpdateCaseError, UpdateCaseResponse]] =
    jsonParser("IVD - Update Case") { correlationId =>
      json =>
        (json \ "CaseID").validate[String].map(UpdateCaseResponse(_, correlationId))
    }.map(_.left.map(UpdateCaseError.fromEisError))

  private def errorMessage(apiName: String,
                           correlationId: String,
                           problem: String,
                           status: Int,
                           details: Seq[(JsPath, Seq[JsonValidationError])] = Seq(),
                           body: Option[String] = None
                          ): String = {
    if (body.isDefined) {
      s"""API: $apiName
         |Correlation ID: $correlationId
         |Problem: $problem
         |Details: $details
         |Body: ${body.get}""".stripMargin
    } else {
      s"""API: $apiName
         |Correlation ID: $correlationId
         |Problem: $problem
         |Details: $details
         |Status: $status""".stripMargin
    }
  }

  private def jsonParser[A](apiName: String)(reads: String => Reads[A]): HttpReads[Either[EisError, A]] =
    new HttpReads[Either[EisError, A]] with Logging {
      override def read(method: String, url: String, response: HttpResponse): Either[EisError, A] = {
        val correlationId = response.header("x-correlation-id").getOrElse("UNKNOWN")

        response.status match {
          case Status.OK =>
            response.json.validate(reads(correlationId)).fold(
              invalid => {
                logger.error(errorMessage(apiName, correlationId, "Failed to parse JSON for a successful response.", response.status, invalid))
                Left(EisError.UnexpectedError(Status.OK, "Received invalid JSON"))
              },
              caseId => Right(caseId)
            )
          case Status.BAD_REQUEST =>
            response.json.validate[EisError] match {
              case JsSuccess(value, _) => {
                logger.error(errorMessage(apiName, correlationId, "Received 400 response from backend", response.status, Seq(), Some(response.body)))
                Left(value)
              }
              case JsError(errors) =>
                logger.error(errorMessage(apiName, correlationId, "Failed to parse JSON for an error response.", response.status, errors))
                Left(EisError.UnexpectedError(Status.BAD_REQUEST, "Received an unexpected error response"))
            }
          case status =>
            if (response.body.nonEmpty) {
              response.json.validate[EisError] match {
                case JsSuccess(value, _) => {
                  logger.error(errorMessage(apiName, correlationId, "Non-success response returned when attempting to create a case with expected error json",
                    response.status, Seq(), Some(response.body)))
                  Left(value)
                }
                case JsError(errors) =>
                  logger.error(errorMessage(apiName, correlationId,
                    "Non-success response returned when attempting to create a case with unexpected error json.",
                    response.status, errors))
                  Left(EisError.UnexpectedError(status, "Received an unexpected error response"))
              }
            } else {
              logger.error(errorMessage(apiName, correlationId,
                "Non-success response returned when attempting to create a case with empty response body.",
                response.status, Seq(), Some("")))
              Left(EisError.UnexpectedError(status, "Non-success response code with empty response body"))
            }
        }

      }
    }

}
