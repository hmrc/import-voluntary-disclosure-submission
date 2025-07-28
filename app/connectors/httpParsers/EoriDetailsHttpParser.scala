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

package connectors.httpParsers

import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import models.responses.Sub09Response
import models.{EoriDetails, ErrorModel}
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object EoriDetailsHttpParser {

  implicit object EoriDetailsReads extends HttpReads[HttpGetResult[EoriDetails]] {

    private val logger = Logger(getClass)

    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[EoriDetails] = {
      response.status match {
        case Status.OK =>
          response.json.validate[Sub09Response](Sub09Response.reads).fold(
            invalid => {
              logger.error("Received 200 response but failed to parse Sub09 response: " + invalid)
              Left(
                ErrorModel(
                  Status.INTERNAL_SERVER_ERROR,
                  "Invalid Json returned from SUB09 API for EoriDetailsHttpParser"
                )
              )
            },
            okResponse => handleOkResponse(okResponse)
          )
        case Status.NOT_FOUND =>
          logger.error(s"Received 400 response for GET Eori Details. Response body: ${response.body}")
          Left(ErrorModel(Status.NOT_FOUND, "Eori Details not found"))
        case status =>
          logger.error(s"Unexpected response received from downstream. Status code: $status, body: ${response.body}")
          Left(ErrorModel(status, "Downstream error returned when retrieving EoriDetails model from Sub09"))
      }
    }

    private def handleOkResponse(response: Sub09Response): HttpGetResult[EoriDetails] = {
      response.eoriDetails match {
        case Some(details) => Right(details)
        case _ =>
          logger.error("Received 200 response but Eori Details not returned. Response body: " + response.eoriStatus)
          Left(ErrorModel(Status.NOT_FOUND, "Eori Details not returned on Ok response"))
      }
    }
  }

}
