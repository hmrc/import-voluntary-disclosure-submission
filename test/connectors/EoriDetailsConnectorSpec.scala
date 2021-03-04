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
import connectors.httpParsers.ResponseHttpParser.{ExternalResponse, HttpGetResult}
import data.SampleData
import mocks.MockHttp
import models.EoriDetails
import org.scalatest.matchers.should.Matchers._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ReusableValues

import java.time.ZonedDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EoriDetailsConnectorSpec extends SpecBase with MockHttp with ReusableValues {

  trait Test extends MockHttp with SampleData {
    implicit val correlationId: UUID = UUID.randomUUID()
    lazy val target = new EoriDetailsConnector(mockHttp, appConfig)

    def getEoriDetailsResult(): Future[HttpGetResult[EoriDetails]] = new EoriDetailsConnector(mockHttp, appConfig).getEoriDetails(idOne)
  }


  val expectedEoriDetailsUrl = "http://localhost:7952/subscriptions/subscriptiondisplay/v1"

  "getEoriDetailsUrl" should {
    "return the correct URL" in new Test {
      target.getEoriDetailsUrl(idOne) shouldBe expectedEoriDetailsUrl
    }
  }

  "sub09HeaderCarrier" should {

    "generate the correct Date header required for sub09" in new Test {
      private val headerCarrier = target.sub09HeaderCarrier()
      headerCarrier.extraHeaders should contain("Date" -> target.httpDateFormat.format(ZonedDateTime.now))
    }

    "generate the correct Correlation ID header required for sub09" in new Test {
      private val headerCarrier = target.sub09HeaderCarrier()
      headerCarrier.extraHeaders.toMap.keys should contain("X-Correlation-ID")
    }

    "generate the correct Content-Type header required for sub09" in new Test {
      private val headerCarrier = target.sub09HeaderCarrier()
      headerCarrier.extraHeaders should contain("Content-Type" -> "application/json")
    }

    "generate the correct Accept header required for sub09" in new Test {
      private val headerCarrier = target.sub09HeaderCarrier()
      headerCarrier.extraHeaders should contain("Accept" -> "application/json")
    }

    "generate the correct X-Source-System header required for sub09" in new Test {
      private val headerCarrier = target.sub09HeaderCarrier()
      headerCarrier.extraHeaders should contain("X-Source-System" -> "DIG")
    }

  }

}
