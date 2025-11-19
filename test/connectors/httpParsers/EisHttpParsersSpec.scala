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

import base.SpecBase
import connectors.httpParsers.EisHttpParsers._
import models.EisError
import models.responses.{CreateCaseResponse, UpdateCaseResponse}
import org.scalatest.matchers.must.Matchers.mustBe
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse

import java.time.Instant
import java.util.UUID

class EisHttpParsersSpec extends SpecBase {

  val correlationId: String = UUID.randomUUID().toString

  "Parsing a 200 (OK) response" when {

    "the create case response is valid" should {

      val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(correlationId))
      val body: JsObject                    = Json.obj(
        "CaseID"         -> "C18-101",
        "ProcessingDate" -> Instant.now().toString,
        "Status"         -> "Success",
        "StatusText"     -> "Case created successfully"
      )

      val error: JsObject = Json.obj("correlationId" -> correlationId, "errorMessage" -> "Unknown error")

      val response = HttpResponse(Status.OK, body, headers)

      "return the Case ID" in {
        createCaseHttpParser.read("", "", response) mustBe Right(CreateCaseResponse("C18-101", correlationId))
      }

      "return Eis error SEE_OTHER with unexpected status code and response body" in {
        val resp = HttpResponse(Status.SEE_OTHER, body, headers)
        createCaseHttpParser.read("", "", resp) mustBe Left(
          EisError.UnexpectedError(
            Status.SEE_OTHER,
            "Received an error response with unexpected status code and response body"
          )
        )
      }

      "return Eis error INTERNAL_SERVER_ERROR with unexpected status code and expected response body" in {
        val resp = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj("errorDetail" -> error), headers)
        createCaseHttpParser.read("", "", resp) mustBe Left(
          EisError.UnexpectedError(
            Status.INTERNAL_SERVER_ERROR,
            "Received an error response with unexpected status code and expected response body"
          )
        )
      }

      "return Eis error SERVICE_UNAVAILABLE with empty error response" in {
        val resp = HttpResponse(Status.SERVICE_UNAVAILABLE, "", headers)
        createCaseHttpParser.read("", "", resp) mustBe Left(
          EisError.UnexpectedError(Status.SERVICE_UNAVAILABLE, "Non-success response code with empty response body")
        )
      }

    }

    "the update case response is valid" should {

      val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(correlationId))
      val body: JsObject                    = Json.obj(
        "CaseID"         -> "C18-101",
        "ProcessingDate" -> Instant.now().toString,
        "Status"         -> "Success",
        "StatusText"     -> "Case created successfully"
      )

      val response = HttpResponse(Status.OK, body, headers)

      "return the Case ID" in {
        updateCaseHttpParser.read("", "", response) mustBe Right(UpdateCaseResponse("C18-101", correlationId))
      }

    }

    "the response does not contain a case ID" should {

      val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(correlationId))
      val body: JsObject                    = Json.obj(
        "ProcessingDate" -> Instant.now().toString,
        "Status"         -> "Success",
        "StatusText"     -> "Case created successfully"
      )

      val response = HttpResponse(Status.OK, body, headers)

      "return an error" in {
        createCaseHttpParser.read("", "", response) mustBe Left(
          EisError.UnexpectedError(Status.OK, "Received invalid JSON")
        )
      }

    }
  }

  "Parsing a 400 response" should {

    val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(correlationId))

    "the response is a specific EIS error" in {
      val error =
        Json.obj("correlationId" -> correlationId, "errorMessage" -> "9xx : 03- Invalid Case ID")
      val response = HttpResponse(Status.BAD_REQUEST, Json.obj("errorDetail" -> error), headers)
      createCaseHttpParser.read("", "", response) mustBe Left(
        EisError.BackendError(correlationId, None, Some("9xx : 03- Invalid Case ID"))
      )
    }

    "the response is empty" in {
      val response = HttpResponse(Status.BAD_REQUEST, Json.obj(), headers)
      createCaseHttpParser.read("", "", response) mustBe Left(
        EisError.UnexpectedError(Status.BAD_REQUEST, "Received an unexpected error response")
      )
    }
  }
}
