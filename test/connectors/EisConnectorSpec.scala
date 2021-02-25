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

package connectors

import base.SpecBase
import connectors.httpParsers.ResponseHttpParser.ExternalResponse
import data.SampleData
import mocks.MockHttp
import models.requests.CreateCaseRequest
import models.responses.CreateCaseResponse
import org.scalatest.matchers.should.Matchers._
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.ExecutionContext.Implicits.global

class EisConnectorSpec extends SpecBase {

  trait Test extends MockHttp with SampleData {
    lazy val target = new EisConnector(mockHttp, appConfig)
  }

  val expectedCreateCaseUrl = "http://localhost:7952/cpr/caserequest/c18/create/v1"

  "createCaseUrl" should {

    "return the correct URL" in new Test {
      target.createCaseUrl shouldBe expectedCreateCaseUrl
    }

  }

  "eisHeaderCarrier" should {

    "generate the correct CustomProcessesHost header required for EIS" in new Test {
      private val headerCarrier = target.eisHeaderCarrier()
      headerCarrier.extraHeaders should contain("CustomProcessesHost" -> "Digital")
    }

    "generate the correct Accept header required for EIS" in new Test {
      private val headerCarrier = target.eisHeaderCarrier()
      headerCarrier.extraHeaders should contain("accept" -> "application/json")
    }

    "generate the correct Correlation ID header required for EIS" in new Test {
      private val headerCarrier = target.eisHeaderCarrier()
      headerCarrier.extraHeaders.toMap.keys should contain("x-correlation-id")
    }

    "generate the correct Date header required for EIS" in new Test {
      private val headerCarrier = target.eisHeaderCarrier()
      headerCarrier.extraHeaders.toMap.keys should contain("date")
    }

  }

  "createCase" when {

    "a success response is returned from EIS" should {

      "return a CreateCaseResponse" in new Test {
        val response = Right(CreateCaseResponse("some case ID"))
        MockedHttp.post[CreateCaseRequest, ExternalResponse[CreateCaseResponse]](expectedCreateCaseUrl, response)

        await(target.createCase(caseDetails)) shouldBe response
      }

    }

  }
}
