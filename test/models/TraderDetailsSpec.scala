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

package models

import base.ModelSpecBase
import data.SampleData
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

class TraderDetailsSpec extends ModelSpecBase with SampleData {

  val model: TraderDetails = importer

  "Reading trader details from JSON" when {

    val json: JsObject = (incomingJson \ "importer").as[JsObject]

    lazy val result: TraderDetails = json.as[TraderDetails]

    "the JSON is a valid" should {
      "deserialize the trader name" in {
        result.name shouldBe model.name
      }

      "deserialize the email address" in {
        result.emailAddress shouldBe model.emailAddress
      }

      "deserialize the phone number" in {
        result.phoneNumber shouldBe model.phoneNumber
      }

      "deserialize the first line of the address" in {
        result.addressLine1 shouldBe model.addressLine1
      }

      "deserialize the second line of the address" in {
        result.addressLine2 shouldBe model.addressLine2
      }

      "deserialize the city" in {
        result.city shouldBe model.city
      }

      "deserialize the county" in {
        result.county shouldBe model.county
      }

      "deserialize the country code" in {
        result.countryCode shouldBe model.countryCode
      }

      "deserialize the postcode" in {
        result.postalCode shouldBe model.postalCode
      }

      "deserialize the VAT number" in {
        result.vatNumber shouldBe model.vatNumber
      }
    }
  }

  "Writing trader details with a VAT number JSON" should {

    val json: JsObject = (outgoingJson \ "TraderList")
      .as[JsArray]
      .apply(1) // importer is the last item in the array
      .as[JsObject] - "Type"

    val generatedJson: JsObject = Json.toJson(model).as[JsObject]

    json.keys.foreach { propertyName =>
      s"generate a property named $propertyName" in {
        generatedJson.keys should contain(propertyName)
      }

      s"have the correct value for $propertyName" in {
        (generatedJson \ propertyName).as[JsValue] shouldBe (json \ propertyName).as[JsValue]
      }
    }
  }

  "Writing trader details without a VAT number JSON" should {

    val model: TraderDetails = importer.copy(vatNumber = None)
    val json: JsObject = (outgoingJson \ "TraderList")
      .as[JsArray]
      .apply(1) // importer is the last item in the array
      .as[JsObject] - "Type" - "VATNumber"

    val generatedJson: JsObject = Json.toJson(model).as[JsObject]

    json.keys.foreach { propertyName =>
      s"generate a property named $propertyName" in {
        generatedJson.keys should contain(propertyName)
      }

      s"have the correct value for $propertyName" in {
        (generatedJson \ propertyName).as[JsValue] shouldBe (json \ propertyName).as[JsValue]
      }
    }
  }

}
