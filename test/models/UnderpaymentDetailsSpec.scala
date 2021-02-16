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

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UnderpaymentDetailsSpec extends ModelSpecBase {

  private val date = LocalDate.now()
  private val formattedDate = date.format(DateTimeFormatter.ofPattern("yyMMdd"))

  val model: UnderpaymentDetails = UnderpaymentDetails(
    UserTypes.Importer,
    EntryTypes.Single,
    "123",
    "123456A",
    date,
    "1234567890",
    "John Smith",
    "1234567890"
  )

  "Reading underpayment details from JSON" when {

    val json: JsObject = Json.obj(
      "userType" -> "importer",
      "numEntries" -> "oneEntry",
      "entryDetails" -> Json.obj(
        "epu" -> "123",
        "entryNumber" -> "123456A",
        "entryDate" -> date,
      ),
      "originalCpc" -> "1234567890",
      "traderContactDetails" -> Json.obj(
        "fullName" -> "John Smith",
        "phoneNumber" -> "1234567890"
      )
    )

    lazy val result: UnderpaymentDetails = json.as[UnderpaymentDetails]

    "the JSON is a valid" should {
      "deserialize the user type" in {
        result.userType shouldBe model.userType
      }

      "deserialize the entry type" in {
        result.entryType shouldBe model.entryType
      }

      "deserialize the Entry Processing Unit (EPU)" in {
        result.entryProcessingUnit shouldBe model.entryProcessingUnit
      }

      "deserialize the entry number" in {
        result.entryNumber shouldBe model.entryNumber
      }

      "deserialize the entry date" in {
        result.entryDate shouldBe model.entryDate
      }

      "deserialize the original customs procedure code (CPC)" in {
        result.originalCustomsProcedureCode shouldBe model.originalCustomsProcedureCode
      }

      "deserialize the declarant name" in {
        result.declarantName shouldBe model.declarantName
      }

      "deserialize the declarant phone number" in {
        result.declarantPhoneNumber shouldBe model.declarantPhoneNumber
      }
    }
  }

  "Writing underpayment details JSON" should {

    val json: JsObject = Json.obj(
      "RequestedBy" -> "01",
      "IsBulkEntry" -> "02",
      "EPU" -> "123",
      "EntryNumber" -> "123456A",
      "EntryDate" -> formattedDate,
      "IsEUDuty" -> "01", // TODO: needs to come from frontend
      "ReasonForAmendment" -> "Not Applicable", // TODO: needs to come from frontend
      "OriginalCustomsProcCode" -> "1234567890",
      "DeclarantDate" -> formattedDate,
      "DeclarantPhoneNumber" -> "1234567890",
      "DeclarantName" -> "John Smith"
    )

    "generate the correct JSON" in {
      Json.toJson(model) shouldBe json
    }
  }

}
