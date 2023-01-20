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

package models.requests

import base.ModelSpecBase
import data.SampleData
import models.CreateCase
import play.api.libs.json.{JsObject, JsValue, Json}

import java.util.UUID

class EisRequestSpec extends ModelSpecBase with SampleData {

  val acknowledgementReference: UUID = UUID.randomUUID()
  val model: EisRequest[CreateCase]  = EisRequest(acknowledgementReference, caseDetails)

  "Serialising a create case request" should {

    lazy val json = Json.toJson(model).as[JsObject]

    lazy val resultingJson = Json.obj(
      "AcknowledgementReference" -> acknowledgementReference.toString.replace("-", ""),
      "OriginatingSystem"        -> "Digital",
      "ApplicationType"          -> "C18",
      "Content"                  -> outgoingJson
    )

    json.keys.foreach { propertyName =>
      s"generate a property named $propertyName" in {
        resultingJson.keys should contain(propertyName)
      }

      s"have the correct value for $propertyName" in {
        (resultingJson \ propertyName).as[JsValue] shouldBe (json \ propertyName).as[JsValue]
      }
    }

  }

}
