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

package controllers

import base.SpecBase
import mocks.services.MockCreateCaseService
import models.responses.CreateCaseResponse
import models.{CaseDetails, ErrorModel}
import org.scalatest.matchers.should.Matchers
import play.api.http.{ContentTypes, Status}
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, contentType, defaultAwaitTimeout, status}
import play.mvc.Http.HeaderNames

class CreateCaseControllerSpec extends SpecBase with Matchers {

  trait Test extends MockCreateCaseService {
    lazy val target = new CreateCaseController(controllerComponents, mockCreateCaseService)
    val validRequest: FakeRequest[JsObject] = FakeRequest(controllers.routes.CreateCaseController.onSubmit())
      .withHeaders(
        HeaderNames.CONTENT_TYPE -> ContentTypes.JSON,
        HeaderNames.ACCEPT -> ContentTypes.JSON
      )
      .withBody(Json.obj())

  }

  "onSubmit" when {
    "case create in downstream services" should {

      val successResponse = Right(CreateCaseResponse("some id"))

      "return 200 (OK) response" in new Test {
        MockedCreateCaseService.createCase(CaseDetails(None), successResponse)
        private val result = target.onSubmit()(validRequest)
        status(result) shouldBe Status.OK
      }

      "return JSON payload" in new Test {
        MockedCreateCaseService.createCase(CaseDetails(None), successResponse)
        private val result = target.onSubmit()(validRequest)
        contentType(result) shouldBe Some(ContentTypes.JSON)
      }


      "return the correct JSON" in new Test {
        MockedCreateCaseService.createCase(CaseDetails(None), successResponse)
        private val result = target.onSubmit()(validRequest)
        contentAsJson(result) shouldBe Json.obj("id" -> "some id")
      }

    }

    "case creation fails in downstream services" should {

      val failedResponse = Left(ErrorModel(Status.BAD_REQUEST, "some error"))

      "return 500 (INTERNAL SERVER ERROR) response" in new Test {
        MockedCreateCaseService.createCase(CaseDetails(None), failedResponse)
        private val result = target.onSubmit()(validRequest)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return JSON payload" in new Test {
        MockedCreateCaseService.createCase(CaseDetails(None), failedResponse)
        private val result = target.onSubmit()(validRequest)
        contentType(result) shouldBe Some(ContentTypes.JSON)
      }


      "return the correct JSON" in new Test {
        MockedCreateCaseService.createCase(CaseDetails(None), failedResponse)
        private val result = target.onSubmit()(validRequest)
        contentAsJson(result) shouldBe Json.obj()
      }

    }
  }

}
