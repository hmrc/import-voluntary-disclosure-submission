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
import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import mocks.MockHttp
import models.EoriDetails
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ReusableValues

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EoriDetailsConnectorSpec extends SpecBase with MockHttp with ReusableValues {

  object Connector extends EoriDetailsConnector(mockHttp, appConfig)

  "Eori Details Connector" should {

    def getEoriDetailsResult(): Future[HttpGetResult[EoriDetails]] = Connector.getEoriDetails(idOne)

    "return the Right response" in {
      setupMockHttpGet(Connector.getEoriDetailsUrl(idOne))(Right(eoriDetails))
      await(getEoriDetailsResult()) mustBe Right(eoriDetails)
    }

    "return the error response" in {
      setupMockHttpGet(Connector.getEoriDetailsUrl(idOne))(Left(errorModel))
      await(getEoriDetailsResult()) mustBe Left(errorModel)
    }

  }


}
