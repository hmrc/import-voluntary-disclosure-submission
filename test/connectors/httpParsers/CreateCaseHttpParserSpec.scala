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
import connectors.httpParsers.CreateCaseHttpParser.CreateCaseHttpReads
import models.ErrorModel
import models.responses.CreateCaseResponse
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse

import java.time.Instant
import java.util.UUID

class CreateCaseHttpParserSpec extends SpecBase {

  "Parsing a 200 (OK) response" when {

    "the response is valid" should {

      val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(UUID.randomUUID().toString))
      val body: JsObject = Json.obj(
        "CaseID" -> "C18-101",
        "ProcessingDate" -> Instant.now().toString,
        "Status" -> "Success",
        "StatusText" -> "Case created successfully"
      )

      val response = HttpResponse(Status.OK, body, headers)

      "return the Case ID" in {
        CreateCaseHttpReads.read("", "", response) mustBe Right(CreateCaseResponse("C18-101"))
      }

    }

    "the response does not contain a case ID" should {

      val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(UUID.randomUUID().toString))
      val body: JsObject = Json.obj(
        "ProcessingDate" -> Instant.now().toString,
        "Status" -> "Success",
        "StatusText" -> "Case created successfully"
      )

      val response = HttpResponse(Status.OK, body, headers)

      "return an error" in {
        CreateCaseHttpReads.read("", "", response) mustBe Left(ErrorModel(Status.OK, "INVALID JSON"))
      }

    }
  }

  "Parsing a non-200 response" should {

    val headers: Map[String, Seq[String]] = Map("x-correlation-id" -> Seq(UUID.randomUUID().toString))
    val body: JsObject = Json.obj()
    val response = HttpResponse(Status.BAD_REQUEST, body, headers)

    "return an error" in {
      CreateCaseHttpReads.read("", "", response) mustBe Left(ErrorModel(Status.BAD_REQUEST, "Non-success response"))
    }

  }

}
