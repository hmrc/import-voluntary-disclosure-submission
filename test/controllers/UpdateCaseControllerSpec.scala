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

package controllers

import base.SpecBase
import controllers.actions.AuthAction
import data.SampleData
import mocks.connectors.MockAuthConnector
import mocks.services.MockUpdateCaseService
import models._
import models.responses.UpdateCaseResponse
import org.scalatest.matchers.should.Matchers
import play.api.http.{ContentTypes, Status}
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.mvc.Http.HeaderNames

import java.util.UUID
import scala.concurrent.Future

class UpdateCaseControllerSpec extends SpecBase with Matchers {

  trait Test extends MockAuthConnector with MockUpdateCaseService with SampleData {

    MockedAuthConnector.authorise(Future.successful(Some("externalId")))
    val authAction = new AuthAction(mockAuthConnector)

    lazy val target = new UpdateCaseController(controllerComponents, mockUpdateCaseService, authAction, ec)

    val validRequest: FakeRequest[JsObject] = FakeRequest(controllers.routes.UpdateCaseController.onSubmit())
      .withHeaders(
        HeaderNames.CONTENT_TYPE -> ContentTypes.JSON,
        HeaderNames.ACCEPT       -> ContentTypes.JSON
      )
      .withBody(updateCaseJson)

    val invalidRequest: FakeRequest[JsObject] = FakeRequest(controllers.routes.UpdateCaseController.onSubmit())
      .withHeaders(
        HeaderNames.CONTENT_TYPE -> ContentTypes.JSON,
        HeaderNames.ACCEPT       -> ContentTypes.JSON
      )
      .withBody(Json.obj())

  }

  "onSubmit" when {
    "case create in downstream services" should {

      val successResponse = Right(UpdateCaseResponse("some id", UUID.randomUUID().toString))

      "return 200 (OK) response" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, successResponse)
        private val result = target.onSubmit()(validRequest)
        status(result) shouldBe Status.OK
      }

      "return JSON payload" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, successResponse)
        private val result = target.onSubmit()(validRequest)
        contentType(result) shouldBe Some(ContentTypes.JSON)
      }

      "return the correct JSON" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, successResponse)
        private val result = target.onSubmit()(validRequest)
        contentAsJson(result) shouldBe Json.obj("id" -> "some id")
      }

    }

    "case creation fails in downstream services" should {

      val failedResponse = Left(UpdateCaseError.UnexpectedError(Status.BAD_REQUEST, Some("some error")))

      "return 500 (INTERNAL SERVER ERROR) response" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, failedResponse)
        private val result = target.onSubmit()(validRequest)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return JSON payload" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, failedResponse)
        private val result = target.onSubmit()(validRequest)
        contentType(result) shouldBe Some(ContentTypes.JSON)
      }

      "return the correct JSON" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, failedResponse)
        private val result = target.onSubmit()(validRequest)
        contentAsJson(result) shouldBe Json.obj()
      }

    }

    "case ID is not valid" should {
      val failedResponse = Left(UpdateCaseError.InvalidCaseId)

      "return 400 (BAD REQUEST) response" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, failedResponse)
        private val result = target.onSubmit()(validRequest)
        status(result) shouldBe Status.BAD_REQUEST
      }

      "return the correct JSON payload" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, failedResponse)
        private val result = target.onSubmit()(validRequest)
        contentType(result) shouldBe Some(ContentTypes.JSON)
        contentAsJson(result) shouldBe Json.obj("errorCode" -> 1, "errorMessage" -> "Invalid case ID")
      }
    }

    "case is already closed" should {
      val failedResponse = Left(UpdateCaseError.CaseAlreadyClosed)

      "return 400 (BAD REQUEST) response" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, failedResponse)
        private val result = target.onSubmit()(validRequest)
        status(result) shouldBe Status.BAD_REQUEST
      }

      "return the correct JSON payload" in new Test {
        MockedUpdateCaseService.updateCase(updateCase, failedResponse)
        private val result = target.onSubmit()(validRequest)
        contentType(result) shouldBe Some(ContentTypes.JSON)
        contentAsJson(result) shouldBe Json.obj("errorCode" -> 2, "errorMessage" -> "Requested case is already closed")
      }
    }

    "called with invalid payload" should {
      "return (400) Bad Request" in new Test {
        private val result = target.onSubmit()(invalidRequest)
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
  }

}
