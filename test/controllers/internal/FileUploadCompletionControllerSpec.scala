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

package controllers.internal

import base.SpecBase
import mocks.services.MockAuditService
import models.audit.FilesUploadedAuditEvent
import org.scalatest.matchers.should.Matchers
import play.api.http.{ContentTypes, Status}
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.mvc.Http.HeaderNames
import utils.ReusableValues

class FileUploadCompletionControllerSpec extends SpecBase with Matchers {

  trait Test extends MockAuditService with ReusableValues {
    lazy val target = new FileUploadCompletionController(controllerComponents, mockAuditService, ec)

    val validRequest: FakeRequest[JsObject] = FakeRequest(controllers.internal.routes.FileUploadCompletionController.onSubmit())
      .withHeaders(
        HeaderNames.CONTENT_TYPE -> ContentTypes.JSON,
        HeaderNames.ACCEPT -> ContentTypes.JSON
      )
      .withBody(Json.toJsObject(multiFileTransferResponse))

    val invalidRequest: FakeRequest[JsObject] = FakeRequest(controllers.internal.routes.FileUploadCompletionController.onSubmit())
      .withHeaders(
        HeaderNames.CONTENT_TYPE -> ContentTypes.JSON,
        HeaderNames.ACCEPT -> ContentTypes.JSON
      )
      .withBody(Json.obj())
  }

  "onSubmit" when {
    "submit a file upload response to be audited" should {
      "return 204 (NO CONTENT) response" in new Test {
        AuditService.audit(FilesUploadedAuditEvent(Seq(fileTransferResponse), multiFileTransferResponse.caseReferenceNumber))

        private val result = target.onSubmit()(validRequest)
        status(result) shouldBe Status.NO_CONTENT
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
