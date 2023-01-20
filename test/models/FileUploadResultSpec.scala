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
import models.responses.FileUploadResult
import play.api.libs.json._
import utils.ReusableValues

class FileUploadResultSpec extends ModelSpecBase with SampleData with ReusableValues {

  val model: FileUploadResult = uploadResult

  "Reading file upload result from JSON" when {

    val json: JsObject = fileUploadResultJson

    lazy val result: FileUploadResult = json.validate[FileUploadResult] match {
      case JsSuccess(value, _) => value
      case JsError(errors)     => fail(s"Failed to read underpayment details from JSON: $errors")
    }

    "the JSON is valid" should {
      "deserialize the reference" in {
        result.upscanReference shouldBe model.upscanReference
      }

      "deserialize the file name" in {
        result.fileName shouldBe model.fileName
      }

      "deserialize the file mime type" in {
        result.fileMimeType shouldBe model.fileMimeType
      }

      "deserialize the transferredAt field" in {
        result.transferredAt shouldBe model.transferredAt
      }

      "deserialize the durationMillis field" in {
        result.durationMillis shouldBe model.durationMillis
      }

      "deserialize the error field" in {
        result.error shouldBe model.error
      }
    }
  }
}
