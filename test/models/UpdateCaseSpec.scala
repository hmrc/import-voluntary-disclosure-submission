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

package models

import base.ModelSpecBase
import data.SampleData
import play.api.libs.json._

class UpdateCaseSpec extends ModelSpecBase with SampleData {

  val model: UpdateCase = updateCase

  "Reading case update from JSON" when {

    val json: JsObject = updateCaseJson

    lazy val result: UpdateCase = json.validate[UpdateCase] match {
      case JsSuccess(value, _) => value
      case JsError(errors)     => fail(s"Failed to read underpayment details from JSON: $errors")
    }

    "the JSON is valid" should {
      "deserialize the case ID" in {
        result.caseId shouldBe model.caseId
      }

      "deserialize the supporting documents" in {
        result.supportingDocuments shouldBe model.supportingDocuments
      }

      "deserialize null supporting documents as an empty sequence" in {
        (updateCaseJson + ("supportingDocuments" -> Json.parse("null")))
          .validate[UpdateCase]
          .map(_.supportingDocuments) shouldBe JsSuccess(Seq.empty[SupportingDocument])
      }

      "deserialize the additional info" in {
        result.additionalInfo shouldBe model.additionalInfo
      }
    }
  }

  "Converting update case to JSON" when {
    "the data is valid" in {
      Json.toJson(updateCase) shouldBe
        Json.obj("CaseID" -> updateCase.caseId, "Description" -> updateCase.additionalInfo)
    }
  }
}
