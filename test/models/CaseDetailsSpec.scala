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

class CaseDetailsSpec extends ModelSpecBase with SampleData {

  val model: CaseDetails = caseDetails

  "Reading case details from JSON" when {

    val json: JsObject = incomingJson

    lazy val result: CaseDetails = json.validate[CaseDetails] match {
      case JsSuccess(value, _) => value
      case JsError(errors) => fail(s"Failed to read underpayment details from JSON: $errors")
    }

    "the JSON is a valid" should {
      "deserialize the underpayment details" in {
        result.underpaymentDetails shouldBe model.underpaymentDetails
      }

      "deserialize the duties" in {
        result.duties shouldBe model.duties
      }

      "deserialize the documentsSupplied" in {
        result.documentsSupplied ++ result.optionalDocumentsSupplied shouldBe model.documentsSupplied ++ model.optionalDocumentsSupplied
      }

      "deserialize the supportingDocuments" in {
        result.supportingDocuments shouldBe model.supportingDocuments
      }

      "deserialize the box items (underpayment reasons)" in {
        result.amendedItems shouldBe model.amendedItems
      }

      "deserialize the importer details" in {
        result.importer shouldBe model.importer
      }

      "deserialize the representative details" in {
        result.representative shouldBe model.representative
      }

    }
  }

  "Writing underpayment details as JSON" should {

    val json: JsObject = outgoingJson

    implicit val generatedJson: JsObject = Json.toJson(model).as[JsObject]

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
