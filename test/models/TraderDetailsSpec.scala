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

package models

import base.ModelSpecBase
import play.api.libs.json.{JsObject, Json}

class TraderDetailsSpec extends ModelSpecBase {

  val model: TraderDetails = TraderDetails(
    traderType = TraderTypes.Importer,
    name = "John Smith",
    emailAddress = "email@address.com",
    phoneNumber = "1234567890",
    addressLine1 = "1 Some Street",
    addressLine2 = None,
    city = "Some City",
    county = None,
    countryCode = "GB",
    postalCode = "ZZ11ZZ"
  )

  "Reading underpayment details from JSON" when {

    val json: JsObject = Json.obj(
      "userType" -> "importer",
      "traderContactDetails" -> Json.obj(
        "fullName" -> "John Smith",
        "phoneNumber" -> "1234567890",
        "email" -> "email@address.com"
      ),
      "traderAddress" -> Json.obj(
        "streetAndNumber" -> "1 Some Street",
        "city" -> "Some City",
        "countryCode" -> "GB",
        "postalCode" -> "ZZ11ZZ"
      )
    )

    lazy val result: TraderDetails = json.as[TraderDetails]

    "the JSON is a valid" should {
      "deserialize the trader type" in {
        result.traderType shouldBe model.traderType
      }

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
    }
  }

  "Writing underpayment details JSON" should {

    val json: JsObject = Json.obj(
      "Type" -> "01",
      "EORI" -> "GB000000000000000",
      "Name" -> "John Smith",
      "EstablishmentAddress" -> Json.obj(
        "AddressLine1" -> "1 Some Street",
        "City" -> "Some City",
        "CountryCode" -> "GB",
        "PostalCode" -> "ZZ11ZZ",
        "TelephoneNumber" -> "1234567890",
        "EmailAddress" -> "email@address.com"
      )
    )

    "generate the correct JSON for Importers" in {
      Json.toJson(model) shouldBe json
    }
  }

}
