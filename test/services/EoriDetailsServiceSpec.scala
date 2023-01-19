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
import connectors.MockEoriDetailsConnector
import models.ErrorModel
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.http.Status
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import utils.ReusableValues

class EoriDetailsServiceSpec extends ServiceSpecBase {

  trait Test extends MockEoriDetailsConnector with ReusableValues {
    lazy val service = new EoriDetailsService(mockEoriDetailsConnector)
  }

  "A success response from the connector" should {
    "return successful EoriDetailsResponse" in new Test {
      setupMockGetAddress(Right(eoriDetails))

      private val result = service.retrieveEoriDetails(idOne)(hc, ec)

      await(result) mustBe Right(eoriDetails)
    }
  }

  "retrieveEoriDetails" should {
    "attempt upto 3 calls before returning a failed response" in new Test {
      private val error = Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "some error"))
      setupMockGetAddress(error).repeat(3)
      private val result = service.retrieveEoriDetails(idOne)(hc, ec)

      await(result) mustBe error
      verifyMockGetAddressCalls()
    }
  }
}
