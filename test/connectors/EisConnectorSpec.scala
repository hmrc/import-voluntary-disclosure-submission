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
import play.mvc.Http.HeaderNames

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

class EisConnectorSpec extends SpecBase {

  trait Test extends MockHttp with SampleData {
    val correlationId: UUID = UUID.randomUUID()
    lazy val target = new EisConnector(mockHttp, appConfig)
  }

  val expectedCreateCaseUrl = "http://localhost:7952/cpr/caserequest/c18/create/v1"

  "createCaseUrl" should {

    "return the correct URL" in new Test {
      target.createCaseUrl shouldBe expectedCreateCaseUrl
    }

  }

  "headers" should {

    "generate the correct CustomProcessesHost header required for EIS" in new Test {
      private val headers = target.headers(correlationId)
      headers should contain("CustomProcessesHost" -> "Digital")
    }

    "generate the correct Accept header required for EIS" in new Test {
      private val headers = target.headers(correlationId)
      headers should contain("accept" -> "application/json")
    }

    "generate the correct Correlation ID header required for EIS" in new Test {
      private val headers = target.headers(correlationId)
      headers should contain("x-correlation-id" -> correlationId.toString)
    }

    "generate the correct Date header required for EIS" in new Test {
      private val headers = target.headers(correlationId)
      headers.toMap.keys should contain("date")
    }

    "generate the correct Authorization header required for EIS" in new Test {
      private val headers = target.headers(correlationId)
      headers should contain(HeaderNames.AUTHORIZATION -> s"Bearer ${appConfig.createCaseToken}")
    }

  }

  "createCase" when {

    "a success response is returned from EIS" should {

      "return a CreateCaseResponse" in new Test {
        val response = Right(CreateCaseResponse("some case ID", UUID.randomUUID().toString))
        MockedHttp.post[CreateCaseRequest, ExternalResponse[CreateCaseResponse]](expectedCreateCaseUrl, response)

        await(target.createCase(caseDetails)) shouldBe response
      }

    }

  }

}
