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
import data.SampleData
import play.api.libs.json._

class UnderpaymentDetailsSpec extends ModelSpecBase with SampleData {

  val model: UnderpaymentDetails = underpaymentDetails

  "Reading underpayment details from JSON" when {

    val json: JsObject = incomingJson

    lazy val result: UnderpaymentDetails = json.validate[UnderpaymentDetails] match {
      case JsSuccess(value, _) => value
      case JsError(errors) => fail(s"Failed to read underpayment details from JSON: $errors")
    }

    "the JSON is a valid" should {
      "deserialize the user type" in {
        result.userType shouldBe model.userType
      }

      "deserialize the isBulkEntry flag" in {
        result.isBulkEntry shouldBe false
      }

      "deserialize the Entry Processing Unit (EPU)" in {
        result.entryProcessingUnit shouldBe model.entryProcessingUnit
      }

      "deserialize the entry number" in {
        result.entryNumber shouldBe model.entryNumber
      }

      "deserialize the entry date" in {
        result.entryDate shouldBe model.entryDate
      }

      "deserialize the original customs procedure code (CPC)" in {
        result.originalCustomsProcedureCode shouldBe model.originalCustomsProcedureCode
      }

      "deserialize the declarant name" in {
        result.declarantName shouldBe model.declarantName
      }

      "deserialize the declarant phone number" in {
        result.declarantPhoneNumber shouldBe model.declarantPhoneNumber
      }

      "deserialize the deferment type" in {
        result.defermentType shouldBe model.defermentType
      }

      "deserialize the deferment account number" in {
        result.defermentAccountNumber shouldBe model.defermentAccountNumber
      }

      "deserialize the additional deferment number" in {
        result.additionalDefermentNumber shouldBe model.additionalDefermentNumber
      }
    }
  }

  "Writing underpayment details as JSON" should {

    val json: JsObject = (outgoingJson \ "UnderpaymentDetails").as[JsObject]

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
