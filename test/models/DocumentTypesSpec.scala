/*
 * Copyright 2022 HM Revenue & Customs
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
import models.DocumentTypes._
import play.api.libs.json.{JsResultException, JsString, Json}

class DocumentTypesSpec extends ModelSpecBase {

  "Reading a document type from JSON" when {

    "valid data exists" should {

      "deserialize a OriginalC88" in {
        JsString("OriginalC88").as[DocumentType] shouldBe DocumentTypes.OriginalC88
      }

      "deserialize a OriginalC2" in {
        JsString("OriginalC2").as[DocumentType] shouldBe DocumentTypes.OriginalC2
      }

      "deserialize a AmendedSubstituteEntryWorksheet" in {
        JsString("AmendedSubstituteEntryWorksheet").as[
          DocumentType
        ] shouldBe DocumentTypes.AmendedSubstituteEntryWorksheet
      }

      "deserialize a AmendedC88" in {
        JsString("AmendedC88").as[DocumentType] shouldBe DocumentTypes.AmendedC88
      }

      "deserialize a AmendedC2" in {
        JsString("AmendedC2").as[DocumentType] shouldBe DocumentTypes.AmendedC2
      }

      "deserialize a InvoiceAirwayBillPreferenceCertificate" in {
        JsString("InvoiceAirwayBillPreferenceCertificate").as[
          DocumentType
        ] shouldBe DocumentTypes.InvoiceAirwayBillPreferenceCertificate
      }

      "deserialize a DefermentAuthorisation" in {
        JsString("DefermentAuthorisation").as[DocumentType] shouldBe DocumentTypes.DefermentAuthorisation
      }

      "deserialize a Other" in {
        JsString("Other").as[DocumentType] shouldBe DocumentTypes.Other
      }
    }

    "invalid data exists" should {

      "fail to parse" in {
        intercept[JsResultException] {
          JsString("Unknown").as[DocumentType]
        }
      }
    }
  }

  "Writing a DocumentType" should {

    "serialise OriginalC88 as 01" in {
      Json.toJson(OriginalC88) shouldBe Json.obj("Type" -> "01")
    }
    "serialise OriginalC2 as 02" in {
      Json.toJson(OriginalC2) shouldBe Json.obj("Type" -> "02")
    }
    "serialise AmendedSubstituteEntryWorksheet as 03" in {
      Json.toJson(AmendedSubstituteEntryWorksheet) shouldBe Json.obj("Type" -> "03")
    }
    "serialise AmendedC88 as 04" in {
      Json.toJson(AmendedC88) shouldBe Json.obj("Type" -> "04")
    }
    "serialise AmendedC2 as 05" in {
      Json.toJson(AmendedC2) shouldBe Json.obj("Type" -> "05")
    }
    "serialise InvoiceAirwayBillPreferenceCertificate as 06" in {
      Json.toJson(InvoiceAirwayBillPreferenceCertificate) shouldBe Json.obj("Type" -> "06")
    }
    "serialise DefermentAuthorisation as 07" in {
      Json.toJson(DefermentAuthorisation) shouldBe Json.obj("Type" -> "07")
    }
    "serialise Other as 08" in {
      Json.toJson(Other) shouldBe Json.obj("Type" -> "08")
    }

  }
}
