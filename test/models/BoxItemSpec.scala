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
import play.api.libs.json.{JsObject, JsResultException, JsValue, Json}

class BoxItemSpec extends ModelSpecBase {

  val model: BoxItem = BoxItem(22, 1, "1", "2")

  "Reading a box item from valid JSON" when {

    "the JSON is a valid" should {

      val json: JsObject = Json.obj(
        "boxNumber" -> 22,
        "itemNumber" -> 1,
        "original" -> "1",
        "amended" -> "2"
      )

      lazy val result: BoxItem = json.as[BoxItem]

      "deserialize the box number" in {
        result.boxNumber shouldBe model.boxNumber
      }

      "deserialize the item number" in {
        result.itemNumber shouldBe model.itemNumber
      }

      "deserialize the original value" in {
        result.original shouldBe model.original
      }

      "deserialize the amended value" in {
        result.amended shouldBe model.amended
      }

    }

    "the JSON is invalid" should {

      val json: JsObject = Json.obj(
        "boxNumber" -> 2, // invalid box number
        "itemNumber" -> 1,
        "original" -> "1",
        "amended" -> "2"
      )

      "throw an exception" in {
        intercept[JsResultException](json.as[BoxItem])
      }
    }
  }

  "Writing a box item to JSON" should {

    val json: JsObject = Json.obj(
      "BoxNumber" -> "22",
      "ItemNumber" -> "01",
      "EnteredAs" -> "1",
      "AmendedTo" -> "2"
    )

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
