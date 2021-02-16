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

class DutyItemSpec extends ModelSpecBase {

  val model: DutyItem = DutyItem(
    DutyTypes.CustomsDuty,
    BigDecimal("1"),
    BigDecimal("2")
  )

  "Reading a duty item from JSON" when {

    val json: JsObject = Json.obj(
      "duty" -> "customsDuty",
      "original" -> BigDecimal(1),
      "amended" -> BigDecimal(2)
    )

    lazy val result: DutyItem = json.as[DutyItem]

    "the JSON is a valid" should {
      "deserialize the duty type" in {
        result.dutyType shouldBe model.dutyType
      }
    }

    "the JSON is a valid" should {
      "deserialize the original amount" in {
        result.originalAmount shouldBe model.originalAmount
      }
    }

    "the JSON is a valid" should {
      "deserialize the amended amount" in {
        result.amendedAmount shouldBe model.amendedAmount
      }

    }
  }

  "Writing Duty item to JSON" should {

    val json: JsObject = Json.obj(
      "Type" -> "A00",
      "PaidAmount" -> "1",
      "DueAmount" -> "2",
      "OutstandingAmount" -> "1"
    )

    "generate the correct JSON" in {
      Json.toJson(model) shouldBe json
    }
  }

}
