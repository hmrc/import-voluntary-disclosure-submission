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

package controllers.actions

import base.SpecBase
import mocks.connectors.MockAuthConnector
import models.auth.AuthorisedRequest
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.Json
import play.api.mvc.Results.Unauthorized
import play.api.mvc.{Request, Result}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthorisedActionSpec extends SpecBase with Matchers {

  trait Test extends MockAuthConnector {

    def failureResponse(message: String): Result = Unauthorized(Json.obj("error" -> message))

    class Harness() extends AuthAction(mockAuthConnector) {
      def test[A](request: Request[A]): Future[Either[Result, AuthorisedRequest[A]]] = refine(request)
    }

    val target = new Harness()
  }

  "Auth Action" when {

    "user is not logged in" must {

      "return an unauthorised response" in new Test {
        MockedAuthConnector.authorise(Future.failed(SessionRecordNotFound()))
        private val result = await(target.test(fakeRequest))
        result mustBe Left(failureResponse("Session record not found"))
      }
    }

    "user is logged in and has an external ID" must {

      "return a request with user details" in new Test {
        val externalId = "user-external-id"
        MockedAuthConnector.authorise(Future.successful(Some(externalId)))
        private val expectedRequest = AuthorisedRequest(fakeRequest, externalId)
        private val result = await(target.test(fakeRequest))
        result mustBe Right(expectedRequest)
      }
    }

    "user is logged in and has no external ID" must {

      "return an unauthorised response" in new Test {
        MockedAuthConnector.authorise(Future.successful(None))
        private val result = await(target.test(fakeRequest))
        result mustBe Left(failureResponse("Unable to retrieve an external ID from auth"))
      }
    }

    "authorisation exception occurs" must {

      "receive an authorised response" in new Test {
        MockedAuthConnector.authorise(Future.failed(InternalError()))
        private val result = await(target.test(fakeRequest))
        result mustBe Left(failureResponse("Internal error"))
      }
    }
  }

}
