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

package data

import models.DocumentTypes.Other
import models._
import play.api.libs.json.{JsObject, Json}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

trait SampleData {

  private val date = LocalDate.now()
  private val formattedDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
  private val timestamp = LocalDateTime.now()

  val underpaymentDetails: UnderpaymentDetails = UnderpaymentDetails(
    userType = UserTypes.Representative,
    isBulkEntry = false,
    isEuropeanUnionDuty = false,
    reasonForAmendment = "Not Applicable",
    entryProcessingUnit = Some("123"),
    entryNumber = Some("123456A"),
    entryDate = Some(date),
    originalCustomsProcedureCode = "4000C09",
    declarantName = "John Smith",
    declarantPhoneNumber = "1234567890",
    defermentType = Some("D"),
    defermentAccountNumber = Some("1234567"),
    additionalDefermentNumber = Some("C1234567")
  )

  val bulkUnderpaymentDetails: UnderpaymentDetails = underpaymentDetails.copy(
    entryProcessingUnit = None,
    entryNumber = None,
    entryDate = None,
    originalCustomsProcedureCode = "VARIOUS",
    isBulkEntry = true,
    isEuropeanUnionDuty = true
  )

  val duties = Seq(
    DutyItem(DutyTypes.A00, BigDecimal("123"), BigDecimal("233.33")),
    DutyItem(DutyTypes.B00, BigDecimal("111.11"), BigDecimal("1234")),
    DutyItem(DutyTypes.E00, BigDecimal("123.22"), BigDecimal("4409.55"))
  )

  val documentsSupplied = Seq(
    DocumentTypes.OriginalC88,
    DocumentTypes.OriginalC2,
    DocumentTypes.AmendedSubstituteEntryWorksheet,
    DocumentTypes.AmendedC88,
    DocumentTypes.AmendedC2
  )

  val supportingDocuments = Seq(
    SupportingDocument(
      "file-reference",
      "TestDocument.pdf",
      "http://some/location",
      timestamp,
      "the file checksum",
      "application/pdf"
    )
  )

  val amendedItems = Seq(
    BoxItem(62, 0, "GBP1", "GBP2"),
    BoxItem(33, 1, "1", "2")
  )

  val importer: TraderDetails = TraderDetails(
    eori = "GB000000000000001",
    name = "Importer Inc",
    emailAddress = "notsupplied@example.com",
    phoneNumber = "000000000",
    addressLine1 = "99",
    addressLine2 = Some("Avenue Road"),
    city = "Any Old Town",
    county = None,
    countryCode = "GB",
    postalCode = Some("ZZ11ZZ"),
    vatNumber = Some("123456789")
  )

  val representative: TraderDetails = TraderDetails(
    eori = "GB000000000000002",
    name = "Representative Inc",
    emailAddress = "test@test.com",
    phoneNumber = "1234567890",
    addressLine1 = "99",
    addressLine2 = Some("Avenue Road"),
    city = "Any Old Town",
    county = None,
    countryCode = "GB",
    postalCode = Some("ZZ11ZZ"),
    vatNumber = None
  )

  val caseDetails: CreateCase = CreateCase(
    underpaymentDetails = underpaymentDetails,
    duties = duties,
    documentsSupplied = documentsSupplied,
    supportingDocuments = supportingDocuments,
    amendedItems = Some(amendedItems),
    importer = importer,
    representative = Some(representative)
  )

  val bulkCaseDetails: CreateCase = caseDetails.copy(underpaymentDetails = bulkUnderpaymentDetails, documentsSupplied = Seq(Other))

  val updateCase: UpdateCase = UpdateCase(
    caseId = "C18",
    supportingDocuments = supportingDocuments,
    additionalInfo = "Additional Info"
  )

  val incomingJson: JsObject = Json.obj(
    "userType" -> "representative",
    "isBulkEntry" -> false,
    "isEuropeanUnionDuty" -> false,
    "additionalInfo" -> "Not Applicable",
    "entryDetails" -> Json.obj(
      "epu" -> "123",
      "entryNumber" -> "123456A",
      "entryDate" -> date
    ),
    "customsProcessingCode" -> "4000C09",
    "declarantContactDetails" -> Json.obj(
      "fullName" -> "John Smith",
      "email" -> "test@test.com",
      "phoneNumber" -> "1234567890"
    ),
    "underpaymentDetails" -> Json.arr(
      Json.obj(
        "duty" -> "A00",
        "original" -> BigDecimal("123"),
        "amended" -> BigDecimal("233.33")
      ),
      Json.obj(
        "duty" -> "B00",
        "original" -> BigDecimal("111.11"),
        "amended" -> BigDecimal("1234")
      ),
      Json.obj(
        "duty" -> "E00",
        "original" -> BigDecimal("123.22"),
        "amended" -> BigDecimal("4409.55")
      )
    ),
    "supportingDocumentTypes" -> Json.arr(
      "OriginalC88",
      "OriginalC2",
      "AmendedSubstituteEntryWorksheet",
      "AmendedC88",
      "AmendedC2"
    ),
    "defermentType" -> "D",
    "defermentAccountNumber" -> "1234567",
    "additionalDefermentAccountNumber" -> "C1234567",
    "amendedItems" -> Json.arr(
      Json.obj(
        "boxNumber" -> 62,
        "itemNumber" -> 0,
        "original" -> "GBP1",
        "amended" -> "GBP2"
      ),
      Json.obj(
        "boxNumber" -> 33,
        "itemNumber" -> 1,
        "original" -> "1",
        "amended" -> "2"
      )
    ),
    "supportingDocuments" -> Json.arr(
      Json.obj(
        "reference" -> "file-reference",
        "fileName" -> "TestDocument.pdf",
        "downloadUrl" -> "http://some/location",
        "uploadTimestamp" -> timestamp,
        "checksum" -> "the file checksum",
        "fileMimeType" -> "application/pdf"
      )
    ),
    "importer" -> Json.obj(
      "eori" -> "GB000000000000001",
      "contactDetails" -> Json.obj(
        "fullName" -> "Importer Inc",
        "phoneNumber" -> "000000000",
        "email" -> "notsupplied@example.com"
      ),
      "address" -> Json.obj(
        "addressLine1" -> "99",
        "addressLine2" -> "Avenue Road",
        "city" -> "Any Old Town",
        "countryCode" -> "GB",
        "postalCode" -> "ZZ11ZZ"
      ),
      "vatNumber" -> "123456789"
    ),
    "representative" -> Json.obj(
      "eori" -> "GB000000000000002",
      "contactDetails" -> Json.obj(
        "fullName" -> "Representative Inc",
        "email" -> "test@test.com",
        "phoneNumber" -> "1234567890"
      ),
      "address" -> Json.obj(
        "addressLine1" -> "99",
        "addressLine2" -> "Avenue Road",
        "city" -> "Any Old Town",
        "countryCode" -> "GB",
        "postalCode" -> "ZZ11ZZ"
      )
    )
  )

  val updateCaseJson: JsObject = Json.obj(
    "caseId" -> "C18",
    "additionalInfo" -> "Additional Info",
    "supportingDocuments" -> Json.arr(
      Json.obj(
        "reference" -> "file-reference",
        "fileName" -> "TestDocument.pdf",
        "downloadUrl" -> "http://some/location",
        "uploadTimestamp" -> timestamp,
        "checksum" -> "the file checksum",
        "fileMimeType" -> "application/pdf"
      )
    ),
  )

  val bulkIncomingJson: JsObject = Json.obj(
    "userType" -> "representative",
    "isBulkEntry" -> true,
    "isEuropeanUnionDuty" -> true,
    "additionalInfo" -> "Not Applicable",
    "customsProcessingCode" -> "VARIOUS",
    "declarantContactDetails" -> Json.obj(
      "fullName" -> "John Smith",
      "email" -> "test@test.com",
      "phoneNumber" -> "1234567890"
    ),
    "underpaymentDetails" -> Json.arr(
      Json.obj(
        "duty" -> "A00",
        "original" -> BigDecimal("123"),
        "amended" -> BigDecimal("233.33")
      ),
      Json.obj(
        "duty" -> "B00",
        "original" -> BigDecimal("111.11"),
        "amended" -> BigDecimal("1234")
      ),
      Json.obj(
        "duty" -> "E00",
        "original" -> BigDecimal("123.22"),
        "amended" -> BigDecimal("4409.55")
      )
    ),
    "supportingDocumentTypes" -> Json.arr(
      "Other"
    ),
    "defermentType" -> "D",
    "defermentAccountNumber" -> "1234567",
    "additionalDefermentAccountNumber" -> "C1234567",
    "amendedItems" -> Json.arr(
      Json.obj(
        "boxNumber" -> 62,
        "itemNumber" -> 0,
        "original" -> "GBP1",
        "amended" -> "GBP2"
      ),
      Json.obj(
        "boxNumber" -> 33,
        "itemNumber" -> 1,
        "original" -> "1",
        "amended" -> "2"
      )
    ),
    "supportingDocuments" -> Json.arr(
      Json.obj(
        "reference" -> "file-reference",
        "fileName" -> "TestDocument.pdf",
        "downloadUrl" -> "http://some/location",
        "uploadTimestamp" -> timestamp,
        "checksum" -> "the file checksum",
        "fileMimeType" -> "application/pdf"
      )
    ),
    "importer" -> Json.obj(
      "eori" -> "GB000000000000001",
      "contactDetails" -> Json.obj(
        "fullName" -> "Importer Inc",
        "phoneNumber" -> "000000000",
        "email" -> "notsupplied@example.com"
      ),
      "address" -> Json.obj(
        "addressLine1" -> "99",
        "addressLine2" -> "Avenue Road",
        "city" -> "Any Old Town",
        "countryCode" -> "GB",
        "postalCode" -> "ZZ11ZZ"
      ),
      "vatNumber" -> "123456789"
    ),
    "representative" -> Json.obj(
      "eori" -> "GB000000000000002",
      "contactDetails" -> Json.obj(
        "fullName" -> "Representative Inc",
        "email" -> "test@test.com",
        "phoneNumber" -> "1234567890"
      ),
      "address" -> Json.obj(
        "addressLine1" -> "99",
        "addressLine2" -> "Avenue Road",
        "city" -> "Any Old Town",
        "countryCode" -> "GB",
        "postalCode" -> "ZZ11ZZ"
      )
    )
  )

  val outgoingJson: JsObject = Json.obj(
    "UnderpaymentDetails" -> Json.obj(
      "RequestedBy" -> "02",
      "IsBulkEntry" -> "02",
      "IsEUDuty" -> "02",
      "EPU" -> "123",
      "EntryNumber" -> "123456A",
      "EntryDate" -> formattedDate,
      "ReasonForAmendment" -> "Not Applicable",
      "OriginalCustomsProcCode" -> "4000C09",
      "DeclarantDate" -> formattedDate,
      "DeclarantPhoneNumber" -> "1234567890",
      "DeclarantName" -> "John Smith",
      "DefermentType" -> "D",
      "DefermentAccountNumber" -> "1234567",
      "AdditionalDefermentNumber" -> "C1234567"
    ),
    "DutyTypeList" -> Json.arr(
      Json.obj(
        "Type" -> "A00",
        "PaidAmount" -> "123",
        "DueAmount" -> "233.33",
        "OutstandingAmount" -> "110.33"
      ),
      Json.obj(
        "Type" -> "B00",
        "PaidAmount" -> "111.11",
        "DueAmount" -> "1234",
        "OutstandingAmount" -> "1122.89"
      ),
      Json.obj(
        "Type" -> "E00",
        "PaidAmount" -> "123.22",
        "DueAmount" -> "4409.55",
        "OutstandingAmount" -> "4286.33"
      )
    ),
    "DocumentList" -> Json.arr(
      Json.obj("Type" -> "01"),
      Json.obj("Type" -> "02"),
      Json.obj("Type" -> "03"),
      Json.obj("Type" -> "04"),
      Json.obj("Type" -> "05")
    ),
    "TraderList" -> Json.arr(
      Json.obj(
        "Type" -> "02",
        "EORI" -> "GB000000000000002",
        "Name" -> "Representative Inc",
        "EstablishmentAddress" -> Json.obj(
          "AddressLine1" -> "99",
          "AddressLine2" -> "Avenue Road",
          "City" -> "Any Old Town",
          "CountryCode" -> "GB",
          "PostalCode" -> "ZZ11ZZ",
          "TelephoneNumber" -> "1234567890",
          "EmailAddress" -> "test@test.com"
        )
      ),
      Json.obj(
        "Type" -> "01",
        "EORI" -> "GB000000000000001",
        "Name" -> "Importer Inc",
        "EstablishmentAddress" -> Json.obj(
          "AddressLine1" -> "99",
          "AddressLine2" -> "Avenue Road",
          "City" -> "Any Old Town",
          "CountryCode" -> "GB",
          "PostalCode" -> "ZZ11ZZ",
          "TelephoneNumber" -> "000000000",
          "EmailAddress" -> "notsupplied@example.com"
        ),
        "VATNumber" -> "123456789"
      )
    ),
    "ImportInfoList" -> Json.arr(
      Json.obj(
        "BoxNumber" -> "62",
        "ItemNumber" -> "00",
        "EnteredAs" -> "GBP1",
        "AmendedTo" -> "GBP2"
      ),
      Json.obj(
        "BoxNumber" -> "33",
        "ItemNumber" -> "01",
        "EnteredAs" -> "1",
        "AmendedTo" -> "2"
      )

    )
  )

  val bulkOutgoingJson: JsObject = Json.obj(
    "UnderpaymentDetails" -> Json.obj(
      "RequestedBy" -> "02",
      "IsBulkEntry" -> "01",
      "IsEUDuty" -> "01",
      "EPU" -> "VARIOUS",
      "EntryNumber" -> "VARIOUS",
      "EntryDate" -> "VARIOUS",
      "ReasonForAmendment" -> "Not Applicable",
      "OriginalCustomsProcCode" -> "VARIOUS",
      "DeclarantDate" -> formattedDate,
      "DeclarantPhoneNumber" -> "1234567890",
      "DeclarantName" -> "John Smith",
      "DefermentType" -> "D",
      "DefermentAccountNumber" -> "1234567",
      "AdditionalDefermentNumber" -> "C1234567"
    ),
    "DutyTypeList" -> Json.arr(
      Json.obj(
        "Type" -> "A00",
        "PaidAmount" -> "123",
        "DueAmount" -> "233.33",
        "OutstandingAmount" -> "110.33"
      ),
      Json.obj(
        "Type" -> "B00",
        "PaidAmount" -> "111.11",
        "DueAmount" -> "1234",
        "OutstandingAmount" -> "1122.89"
      ),
      Json.obj(
        "Type" -> "E00",
        "PaidAmount" -> "123.22",
        "DueAmount" -> "4409.55",
        "OutstandingAmount" -> "4286.33"
      )
    ),
    "DocumentList" -> Json.arr(
      Json.obj("Type" -> "08")
    ),
    "TraderList" -> Json.arr(
      Json.obj(
        "Type" -> "02",
        "EORI" -> "GB000000000000002",
        "Name" -> "Representative Inc",
        "EstablishmentAddress" -> Json.obj(
          "AddressLine1" -> "99",
          "AddressLine2" -> "Avenue Road",
          "City" -> "Any Old Town",
          "CountryCode" -> "GB",
          "PostalCode" -> "ZZ11ZZ",
          "TelephoneNumber" -> "1234567890",
          "EmailAddress" -> "test@test.com"
        )
      ),
      Json.obj(
        "Type" -> "01",
        "EORI" -> "GB000000000000001",
        "Name" -> "Importer Inc",
        "EstablishmentAddress" -> Json.obj(
          "AddressLine1" -> "99",
          "AddressLine2" -> "Avenue Road",
          "City" -> "Any Old Town",
          "CountryCode" -> "GB",
          "PostalCode" -> "ZZ11ZZ",
          "TelephoneNumber" -> "000000000",
          "EmailAddress" -> "notsupplied@example.com"
        ),
        "VATNumber" -> "123456789"
      )
    )
  )

  val fileUploadResultJson: JsObject =
    Json.obj(
      "upscanReference" -> "XYZ0123456789",
      "fileName" -> "test1.pdf",
      "fileMimeType" -> "application/pdf",
      "checksum" -> "a38d7dd155b1ec9703e5f19f839922ad5a1b0aa4f255c6c2b03e61535997d75",
      "fileSize" -> 1210290,
      "success" -> true,
      "httpStatus" -> 202,
      "transferredAt" -> "2020-12-12T00:00",
      "correlationId" -> "07b8090f-69c8-4708-bfc4-bf1731d4b4a8",
      "durationMillis" -> 1
    )

  val multiFileResponsePayload: JsObject =
    Json.obj(
      "conversationId" -> "074c3823-c941-417e-a08b-e47b08e9a9b7",
      "caseReferenceNumber" -> "C18123",
      "applicationName" -> "C18",
      "results" -> Json.arr(
        fileUploadResultJson,
        Json.obj(
          "upscanReference" -> "XYZ0123456789",
          "fileName" -> "test2.jpeg",
          "fileMimeType" -> "image/jpeg",
          "checksum" -> "a38d7dd155b1ec9703e5f19f839922ad5a1b0aa4f255c6c2b03e61535997d75",
          "fileSize" -> 98989,
          "success" -> false,
          "httpStatus" -> 500,
          "transferredAt" -> "2021-07-11T12:54:01",
          "correlationId" -> "07b8090f-69c8-4708-bfc4-bf1731d4b4a8",
          "error" -> "some error description",
          "durationMillis" -> 18590
        )
      )
    )
}
