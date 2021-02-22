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
import play.api.libs.json._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UnderpaymentDetailsSpec extends ModelSpecBase {

  private val date = LocalDate.now()
  private val formattedDate = date.format(DateTimeFormatter.ofPattern("yyMMdd"))

  val model: UnderpaymentDetails = UnderpaymentDetails(
    UserTypes.Importer,
    isBulkEntry = false,
    isEuropeanUnionDuty = false,
    "Not Applicable",
    "123",
    "123456A",
    date,
    "4000C09",
    "John Smith",
    "1234567890"
  )

  "Reading underpayment details from JSON" when {

    val json: JsObject = Json.obj(
      "userType" -> "importer",
      "isBulkEntry" -> false,
      "isEuropeanUnionDuty" -> true,
      "additionalInfo" -> "Not Applicable",
      "entryDetails" -> Json.obj(
        "epu" -> "123",
        "entryNumber" -> "123456A",
        "entryDate" -> date.toString
      ),
      "customsProcessingCode" -> "4000C09",
      "declarantContactDetails" -> Json.obj(
        "fullName" -> "John Smith",
        "email" -> "test@test.com",
        "phoneNumber" -> "1234567890"
      ),
      "declarantAddress" -> Json.obj(
        "streetAndNumber" -> "99 Avenue Road",
        "city" -> "Any Old Town",
        "postalCode" -> "99JZ 1AA",
        "countryCode" -> "United Kingdom"
      ),
      "underpaymentDetails" -> Json.arr(
        Json.obj(
          "duty" -> "customsDuty",
          "original" -> BigDecimal("123"),
          "amended" -> BigDecimal("233.33")
        ),
        Json.obj(
          "duty" -> "importVat",
          "original" -> BigDecimal("111.11"),
          "amended" -> BigDecimal("1234")
        ),
        Json.obj(
          "duty" -> "exciseDuty",
          "original" -> BigDecimal("123.22"),
          "amended" -> BigDecimal("4409.55")
        )
      ),
      "supportingDocumentTypes" -> Json.arr(),
      "amendedItems" -> Json.arr(),
      "supportingDocuments" -> Json.arr(
        Json.obj(
          "fileName" -> "TestDocument.pdf",
          "downloadUrl" -> "http://some/location",
          "uploadTimestamp" -> "2021-02-21T14 ->30 ->18.011",
          "checksum" -> "the file checksum",
          "fileMimeType" -> "application/pdf"
        )
      )
    )

    lazy val result: UnderpaymentDetails = json.validate[UnderpaymentDetails] match {
      case JsSuccess(value, _) => value
      case JsError(errors) => fail(s"Failed to read underpayment details from JSON: $errors")
    }

    "the JSON is a valid" should {
      "deserialize the user type" in {
        result.userType shouldBe model.userType
      }

      "deserialize the isBulkEntry flag" in {
        result.isBulkEntry shouldBe false
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

  "Writing underpayment details as JSON" should {

    val json: JsObject = Json.obj(
      "RequestedBy" -> "01",
      "IsBulkEntry" -> "02",
      "IsEUDuty" -> "02",
      "EPU" -> "123",
      "EntryNumber" -> "123456A",
      "EntryDate" -> formattedDate,
      "ReasonForAmendment" -> "Not Applicable", // TODO: needs to come from frontend
      "OriginalCustomsProcCode" -> "4000C09",
      "DeclarantDate" -> formattedDate,
      "DeclarantPhoneNumber" -> "1234567890",
      "DeclarantName" -> "John Smith"
    )

    implicit val generatedJson: JsObject = Json.toJson(model).as[JsObject]

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
