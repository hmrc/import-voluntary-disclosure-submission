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

package utils

import models.responses.{FileTransferResponse, FileUploadResult, MultiFileTransferResponse}
import models.{EoriDetails, SupportingDocument}
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDateTime

trait ReusableValues {

  val idOne: String = "1"

  val eoriDetails: EoriDetails = EoriDetails(
    "GB987654321000",
    "Fast Food ltd",
    "99 Avenue Road",
    "Anyold Town",
    Some("99JZ 1AA"),
    "GB",
    None
  )

  val eoriDetailsWithVatId: EoriDetails = EoriDetails(
    "GB987654321000",
    "Fast Food ltd",
    "99 Avenue Road",
    "Anyold Town",
    Some("99JZ 1AA"),
    "GB",
    Some("987654321000")
  )

  val doc: SupportingDocument =
    SupportingDocument(
      reference = "XYZ0123456789",
      fileName = "test1.pdf",
      downloadUrl = "http://localhost/test.pdf",
      uploadTimestamp = LocalDateTime.of(2020, 12, 12, 0, 0),
      checksum = "checksum",
      fileMimeType = "application/pdf"
    )

  val uploadResult: FileUploadResult =
    FileUploadResult(
      upscanReference = doc.reference,
      fileName = doc.fileName,
      fileMimeType = doc.fileMimeType,
      success = true,
      httpStatus = Status.ACCEPTED,
      transferredAt = doc.uploadTimestamp,
      correlationId = "123",
      durationMillis = 1,
      error = None
    )

  val fileTransferResponse: FileTransferResponse =
    FileTransferResponse(
      doc.reference,
      doc.fileName,
      doc.fileMimeType,
      fileTransferSuccess = true,
      doc.uploadTimestamp,
      Some("123"),
      1
    )
  val multiFileTransferResponse: MultiFileTransferResponse =
    MultiFileTransferResponse("123", "C18123", "C18", Seq(uploadResult))

  val errorModel: HttpResponse = HttpResponse(Status.NOT_FOUND, "Error Message")

  val detailsJson: JsObject = Json.obj(
    "subscriptionDisplayResponse" -> Json.obj(
      "responseCommon" -> Json.obj(
        "status"         -> "OK",
        "statusText"     -> "Optional status text from ETMP",
        "processingDate" -> "2016-09-17T19:33:47Z",
        "returnParameters" -> Json.arr(
          Json.obj("paramName" -> "POSITION", "paramValue" -> "LINK")
        )
      ),
      "responseDetail" -> Json.obj(
        "EORINo"      -> "GB987654321000",
        "CDSFullName" -> "Fast Food ltd",
        "CDSEstablishmentAddress" -> Json.obj(
          "streetAndNumber" -> "99 Avenue Road",
          "city"            -> "Anyold Town",
          "postalCode"      -> "99JZ 1AA",
          "countryCode"     -> "GB"
        )
      )
    )
  )

  val cleanedDetailsJson: JsObject = Json.obj(
    "eori"            -> "GB987654321000",
    "name"            -> "Fast Food ltd",
    "streetAndNumber" -> "99 Avenue Road",
    "city"            -> "Anyold Town",
    "postalCode"      -> "99JZ 1AA",
    "countryCode"     -> "GB"
  )

  val cleanedDetailsWithVatIdJson: JsObject = Json.obj(
    "eori"            -> "GB987654321000",
    "name"            -> "Fast Food ltd",
    "streetAndNumber" -> "99 Avenue Road",
    "city"            -> "Anyold Town",
    "postalCode"      -> "99JZ 1AA",
    "countryCode"     -> "GB",
    "vatNumber"       -> "987654321000"
  )

}
