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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import data.SampleData
import play.api.libs.json.{JsObject, Json}
import play.mvc.Http.Status
import support.WireMockMethods

import java.time.Instant
import java.util.UUID

object UpdateCaseStub extends WireMockMethods with SampleData {

  private val authoriseUri = "/cpr/caserequest/c18/update/v1"

  val headers: Map[String, String] = Map("x-correlation-id" -> UUID.randomUUID().toString)

  def success(): StubMapping = {
    val body: JsObject = Json.obj(
      "CaseID" -> "C18-101",
      "ProcessingDate" -> Instant.now().toString,
      "Status" -> "Success",
      "StatusText" -> "Case updated successfully"
    )
    when(method = POST, uri = authoriseUri)
      .thenReturn(Status.OK, headers, body)
  }

  def invalidCaseError(): StubMapping = {
    val error = Json.obj("correlationId" -> "some id", "errorMessage" -> "9xx : 03- Invalid Case ID")
    when(method = POST, uri = authoriseUri)
      .thenReturn(Status.BAD_REQUEST, headers, Json.obj("errorDetail" -> error))
  }

  def caseClosedError(): StubMapping = {
    val error = Json.obj("correlationId" -> "some id", "errorMessage" -> "9xx : 04 - Requested case already closed")
    when(method = POST, uri = authoriseUri)
      .thenReturn(Status.BAD_REQUEST, headers, Json.obj("errorDetail" -> error))
  }
}
