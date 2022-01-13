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
import play.api.libs.json.{JsError, JsSuccess, Reads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object EisHttpParsers {

  implicit val createCaseHttpParser: HttpReads[Either[EisError, CreateCaseResponse]] =
    jsonParser("IVD - Create Case") { correlationId => json =>
      (json \ "CaseID").validate[String].map(CreateCaseResponse(_, correlationId))
    }

  implicit val updateCaseHttpParser: HttpReads[Either[UpdateCaseError, UpdateCaseResponse]] =
    jsonParser("IVD - Update Case") { correlationId => json =>
      (json \ "CaseID").validate[String].map(UpdateCaseResponse(_, correlationId))
    }.map(_.left.map(UpdateCaseError.fromEisError))

  private def jsonParser[A](apiName: String)(reads: String => Reads[A]): HttpReads[Either[EisError, A]] =
    new HttpReads[Either[EisError, A]] with Logging {
      override def read(method: String, url: String, response: HttpResponse): Either[EisError, A] = {
        val correlationId = response.header("x-correlation-id").getOrElse("UNKNOWN")

        response.status match {
          case Status.OK =>
            response.json.validate(reads(correlationId)).fold(
              invalid => {
                val errorMessage =
                  s"""API: $apiName
                     |Correlation ID: $correlationId
                     |Problem: Failed to parse JSON for a successful response.
                     |Details: $invalid""".stripMargin

                logger.error(errorMessage)
                Left(EisError.UnexpectedError(Status.OK, "Received invalid JSON"))
              },
              caseId => Right(caseId)
            )
          case Status.BAD_REQUEST =>
            response.json.validate[EisError] match {
              case JsSuccess(value, _) => Left(value)
              case JsError(errors) =>
                val errorMessage =
                  s"""API: $apiName
                     |Correlation ID: $correlationId
                     |Problem: Failed to parse JSON for an error response.
                     |Details: $errors""".stripMargin

                logger.error(errorMessage)
                Left(EisError.UnexpectedError(Status.BAD_REQUEST, "Received an unexpected error response"))
            }
          case status =>
            val errorMessage =
              s"""API: $apiName
                 |Problem: Non-success response returned when attempting to create a case
                 |Correlation ID: $correlationId
                 |Status: $status
                 |Body: ${response.body}""".stripMargin
            if (response.body.nonEmpty) {
              response.json.validate[EisError]
            }
            logger.error(errorMessage)
            Left(EisError.UnexpectedError(status, "Non-success response code"))
        }

      }
    }

}
