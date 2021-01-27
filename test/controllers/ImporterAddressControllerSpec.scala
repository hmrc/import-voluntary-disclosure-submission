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

package controllers

import base.SpecBase
import mocks.services.MockImporterAddressService
import models.{ErrorModel, TraderAddress}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.http.Status
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import utils.ReusableValues

class ImporterAddressControllerSpec extends SpecBase with MockImporterAddressService with ReusableValues {

  object Controller extends ImporterAddressController(controllerComponents, mockImporterAddressService)


  "Importer Address Controller" should {
    "return OK and the correct Json" in {
      setupMockRetrieveAddress(Right(TraderAddress("first", "second", Some("third"), "fourth")))
      val result = Controller.onLoad(idOne)(fakeRequest)
      status(result) mustEqual Status.OK
      contentAsJson(result) mustEqual traderAddressJson
    }

    "return error model" in {
      setupMockRetrieveAddress(Left(ErrorModel(400, "Could not retrieve address")))
      val result = Controller.onLoad(idOne)(fakeRequest)
      status(result) mustEqual Status.NOT_FOUND
    }

  }

}
