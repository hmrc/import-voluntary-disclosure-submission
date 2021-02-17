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
import models.EntryTypes._
import play.api.libs.json.{JsResultException, JsString, Json}

class EntryTypesSpec extends ModelSpecBase {

  "Reading an entry type from JSON" when {

    "valid data exists" should {

      "deserialize a Single entry type" in {
        JsString("oneEntry").as[EntryType] shouldBe EntryTypes.Single
      }

      "deserialize a Multiple entry type" in {
        JsString("moreThanOneEntry").as[EntryType] shouldBe EntryTypes.Multiple
      }
    }

    "invalid data exists" should {

      "fail to parse" in {
        intercept[JsResultException] {
          JsString("Unknown").as[EntryType]
        }
      }
    }
  }

  "Writing a EntryType" should {

    "serialise as Multiple" in {
      Json.toJson(Multiple) shouldBe JsString("01")
    }

    "serialise as Single" in {
      Json.toJson(Single) shouldBe JsString("02")
    }
  }
}
