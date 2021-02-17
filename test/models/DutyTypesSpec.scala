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
import models.DutyTypes._
import play.api.libs.json.{JsResultException, JsString, Json}

class DutyTypesSpec extends ModelSpecBase {

  "Reading a duty type from JSON" when {

    "valid data exists" should {

      "deserialize a ADD (Definitive)" in {
        JsString("ADD (Definitive)").as[DutyType] shouldBe DutyTypes.A30
      }

      "deserialize a ADD (Provisional)" in {
        JsString("ADD (Provisional)").as[DutyType] shouldBe DutyTypes.A35
      }

      "deserialize a Additional Duty" in {
        JsString("Additional Duty").as[DutyType] shouldBe DutyTypes.A20
      }

      "deserialize a Compensatory Interest" in {
        JsString("Compensatory Interest").as[DutyType] shouldBe DutyTypes.D10
      }

      "deserialize a importVat" in {
        JsString("importVat").as[DutyType] shouldBe DutyTypes.ImportVat
      }

      "deserialize a exciseDuty" in {
        JsString("exciseDuty").as[DutyType] shouldBe DutyTypes.ExciseDuty
      }

      "deserialize a Countervieling Duty (Definitive)" in {
        JsString("Countervieling Duty (Definitive)").as[DutyType] shouldBe DutyTypes.A40
      }

      "deserialize a Countervieling Duty (Provisional)" in {
        JsString("Countervieling Duty (Provisional)").as[DutyType] shouldBe DutyTypes.A45
      }

      "deserialize a Agricultural" in {
        JsString("Agricultural").as[DutyType] shouldBe DutyTypes.A10
      }

      "deserialize a CustomsDuty" in {
        JsString("customsDuty").as[DutyType] shouldBe DutyTypes.CustomsDuty
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

    "serialise ImportVat as B00" in {
      Json.toJson(ImportVat) shouldBe JsString("B00")
    }

    "serialise ExciseDuty as E00" in {
      Json.toJson(ExciseDuty) shouldBe JsString("E00")
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

    "serialise CustomsDuty as A00" in {
      Json.toJson(CustomsDuty) shouldBe JsString("A00")
    }
  }
}
