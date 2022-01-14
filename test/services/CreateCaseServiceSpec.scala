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

package services

import base.ServiceSpecBase
import data.SampleData
import mocks.connectors.MockEisConnector
import mocks.services.MockFileTransferService
import models.responses.CreateCaseResponse
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import java.util.UUID

class CreateCaseServiceSpec extends ServiceSpecBase with MockFileTransferService with MockEisConnector with SampleData {

  "createCase" should {

    "return whatever the connector returns" in {
      val expectedResponse = Right(CreateCaseResponse("some id", UUID.randomUUID().toString))
      MockedEisConnector.createCase(caseDetails, expectedResponse)
      MockedFileTransferService.transferFiles()
      val service = new CreateCaseService(mockEisConnector, mockFileTransferService)
      await(service.createCase(caseDetails)(hc, ec, fakeRequest)) mustBe expectedResponse
    }
  }
}
