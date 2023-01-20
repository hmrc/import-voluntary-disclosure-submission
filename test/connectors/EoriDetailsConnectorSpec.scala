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

package connectors

import base.SpecBase
import connectors.httpParsers.ResponseHttpParser.ExternalResponse
import data.SampleData
import mocks.MockHttp
import models.EoriDetails
import org.scalatest.matchers.should.Matchers._
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ReusableValues

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.{Locale, UUID}
import scala.util.Try

class EoriDetailsConnectorSpec extends SpecBase with MockHttp with ReusableValues {

  trait Test extends MockHttp with SampleData {
    val expectedCorrelationId               = "effd019b-0d2e-42a2-bb98-1e8e14738b59"
    val correlationId: UUID                 = UUID.fromString(expectedCorrelationId)
    lazy val target                         = new EoriDetailsConnector(mockHttp, appConfig)
    lazy val headers: Seq[(String, String)] = target.headers(correlationId)
  }

  val expectedEoriDetailsUrl = "http://localhost:7952/subscriptions/subscriptiondisplay/v1"

  "getEoriDetailsUrl" should {
    "return the correct URL" in new Test {
      target.getEoriDetailsUrl shouldBe expectedEoriDetailsUrl
    }
  }

  "headers" should {

    "generate the correct Date header format required for sub09" in new Test {
      val dateFormat = DateTimeFormatter
        .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
        .withZone(ZoneId.of("GMT"))

      headers.filter(item => item._1 == "Date").map { item =>
        Try(dateFormat.parse(item._2)).isSuccess shouldBe true
      }
    }
    "generate the correct Correlation ID header required for sub09" in new Test {
      headers should contain("X-Correlation-ID" -> expectedCorrelationId)
    }

    "generate the correct Accept header required for sub09" in new Test {
      headers should contain("Accept" -> "application/json")
    }

    "generate the correct X-Forwarded-Host header required for sub09" in new Test {
      headers should contain("X-Forwarded-Host" -> "MDTP")
    }

  }

  "getEoriDetails" when {

    "a success response is returned from SUB09" should {

      "return a EoriDetails" in new Test {
        val response = Right(
          EoriDetails(
            "GB987654321000",
            "Fast Food ltd",
            "99 Avenue Road",
            "Anyold Town",
            Some("99JZ 1AA"),
            "GB",
            Some("987654321000")
          )
        )
        MockedHttp.get[ExternalResponse[EoriDetails]](expectedEoriDetailsUrl, response)

        await(target.getEoriDetails("GB987654321000")) shouldBe response
      }

    }

  }

}
