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
import models.DutyTypes._
import play.api.libs.json.{JsResultException, JsString, Json}

class DutyTypesSpec extends ModelSpecBase {

  "Reading a duty type from JSON" when {

    "valid data exists" should {

      "deserialize a ADD (Definitive)" in {
        JsString("A30").as[DutyType] shouldBe DutyTypes.A30
      }

      "deserialize a ADD (Provisional)" in {
        JsString("A35").as[DutyType] shouldBe DutyTypes.A35
      }

      "deserialize a Additional Duty" in {
        JsString("A20").as[DutyType] shouldBe DutyTypes.A20
      }

      "deserialize a Compensatory Interest" in {
        JsString("D10").as[DutyType] shouldBe DutyTypes.D10
      }

      "deserialize a importVat" in {
        JsString("B00").as[DutyType] shouldBe DutyTypes.B00
      }

      "deserialize a exciseDuty" in {
        JsString("E00").as[DutyType] shouldBe DutyTypes.E00
      }

      "deserialize a Countervieling Duty (Definitive)" in {
        JsString("A40").as[DutyType] shouldBe DutyTypes.A40
      }

      "deserialize a Countervieling Duty (Provisional)" in {
        JsString("A45").as[DutyType] shouldBe DutyTypes.A45
      }

      "deserialize a Agricultural" in {
        JsString("A10").as[DutyType] shouldBe DutyTypes.A10
      }

      "deserialize a CustomsDuty" in {
        JsString("A00").as[DutyType] shouldBe DutyTypes.A00
      }

    }

    "invalid data exists" should {

      "fail to parse" in {
        intercept[JsResultException] {
          JsString("Unknown").as[DutyType]
        }
      }
    }
  }

  "Writing a DutyType" should {

    "serialise as A30" in {
      Json.toJson(A30) shouldBe JsString("A30")
    }

    "serialise as A35" in {
      Json.toJson(A35) shouldBe JsString("A35")
    }

    "serialise as A20" in {
      Json.toJson(A20) shouldBe JsString("A20")
    }

    "serialise as D10" in {
      Json.toJson(D10) shouldBe JsString("D10")
    }

    "serialise B00 as B00" in {
      Json.toJson(B00) shouldBe JsString("B00")
    }

    "serialise E00 as E00" in {
      Json.toJson(E00) shouldBe JsString("E00")
    }

    "serialise as A40" in {
      Json.toJson(A40) shouldBe JsString("A40")
    }

    "serialise as A45" in {
      Json.toJson(A45) shouldBe JsString("A45")
    }

    "serialise as A10" in {
      Json.toJson(A10) shouldBe JsString("A10")
    }

    "serialise A00 as A00" in {
      Json.toJson(A00) shouldBe JsString("A00")
    }

  }
}
