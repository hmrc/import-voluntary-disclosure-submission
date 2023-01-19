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

package services

import base.ServiceSpecBase
import data.SampleData
import mocks.connectors.MockEisConnector
import mocks.services.MockFileTransferService
import models.responses.UpdateCaseResponse
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import java.util.UUID

class UpdateCaseServiceSpec extends ServiceSpecBase with MockFileTransferService with MockEisConnector with SampleData {

  "updateCase" should {

    "return whatever the connector returns" in {
      val expectedResponse = Right(UpdateCaseResponse("some id", UUID.randomUUID().toString))
      MockedEisConnector.updateCase(updateCase, expectedResponse)
      MockedFileTransferService.transferFiles()
      val service = new UpdateCaseService(mockEisConnector, mockFileTransferService)
      await(service.updateCase(updateCase)(hc, ec, fakeRequest)) mustBe expectedResponse
    }

    "proceed without making a file upload call if the document list is empty" in {
      val expectedResponse = Right(UpdateCaseResponse("some id", UUID.randomUUID().toString))
      val request          = updateCase.copy(supportingDocuments = Seq.empty)

      MockedEisConnector.updateCase(request, expectedResponse)
      val service = new UpdateCaseService(mockEisConnector, mockFileTransferService)
      await(service.updateCase(request)(hc, ec, fakeRequest)) mustBe expectedResponse
    }
  }
}
