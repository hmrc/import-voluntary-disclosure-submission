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
import models.TraderTypes._
import play.api.libs.json.{JsResultException, JsString, Json}

class TraderTypesSpec extends ModelSpecBase {

  "Reading an trader type from JSON" when {

    "valid data exists" should {

      "deserialize an Importer" in {
        JsString("importer").as[TraderType] shouldBe TraderTypes.Importer
      }

      "deserialize a Representative" in {
        JsString("representative").as[TraderType] shouldBe TraderTypes.Representative
      }

      "deserialize an Additional" in {
        JsString("additional").as[TraderType] shouldBe TraderTypes.Additional
      }
    }

    "invalid data exists" should {

      "fail to parse" in
        intercept[JsResultException] {
          JsString("Unknown").as[TraderType]
        }
    }
  }

  "Writing a TraderType" should {

    "serialise an Importer as 01" in {
      Json.toJson(Importer) shouldBe JsString("01")
    }

    "serialise a Representative as 02" in {
      Json.toJson(Representative) shouldBe JsString("02")
    }

    "serialise as Additional as 03" in {
      Json.toJson(Additional) shouldBe JsString("03")
    }
  }
}
