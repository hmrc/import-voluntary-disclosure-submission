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

package connectors.httpParsers

import base.SpecBase
import connectors.httpParsers.EisHttpParsers._
import models.ErrorModel
import models.responses.{CreateCaseResponse, UpdateCaseResponse}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
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
      val body: JsObject = Json.obj(
        "CaseID" -> "C18-101",
        "ProcessingDate" -> Instant.now().toString,
        "Status" -> "Success",
        "StatusText" -> "Case created successfully"
      )

      val response = HttpResponse(Status.OK, body, headers)

      "return the Case ID" in {
        createCaseHttpParser.read("", "", response) mustBe Right(CreateCaseResponse("C18-101", correlationId))
      }

    }

    "the update case response is valid" should {

      val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(correlationId))
      val body: JsObject = Json.obj(
        "CaseID" -> "C18-101",
        "ProcessingDate" -> Instant.now().toString,
        "Status" -> "Success",
        "StatusText" -> "Case created successfully"
      )

      val response = HttpResponse(Status.OK, body, headers)

      "return the Case ID" in {
        updateCaseHttpParser.read("", "", response) mustBe Right(UpdateCaseResponse("C18-101", correlationId))
      }

    }

    "the response does not contain a case ID" should {

      val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(correlationId))
      val body: JsObject = Json.obj(
        "ProcessingDate" -> Instant.now().toString,
        "Status" -> "Success",
        "StatusText" -> "Case created successfully"
      )

      val response = HttpResponse(Status.OK, body, headers)

      "return an error" in {
        createCaseHttpParser.read("", "", response) mustBe Left(ErrorModel(Status.OK, "INVALID JSON"))
      }

    }
  }

  "Parsing a non-200 response" should {

    val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(correlationId))
    val body: JsObject = Json.obj()
    val response = HttpResponse(Status.BAD_REQUEST, body, headers)

    "return an error" in {
      createCaseHttpParser.read("", "", response) mustBe Left(ErrorModel(Status.BAD_REQUEST, "Non-success response"))
    }

  }

}
