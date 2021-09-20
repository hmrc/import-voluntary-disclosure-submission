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

package models.requests

import models.SupportingDocument
import play.api.libs.json.{Format, Json}

case class FileTransferRequest(
  conversationId: String,
  caseReferenceNumber: String,
  applicationName: String,
  upscanReference: String,
  downloadUrl: String,
  checksum: String,
  fileName: String,
  fileMimeType: String,
  batchSize: Int,
  batchCount: Int,
  correlationId: Option[String] = None,
  fileSize: Option[Int] = None
)

object FileTransferRequest {

  def fromSupportingDocument(
    caseReferenceNumber: String,
    conversationId: String,
    correlationId: String,
    applicationName: String,
    batchSize: Int,
    batchCount: Int,
    uploadedFile: SupportingDocument
  ): FileTransferRequest =
    FileTransferRequest(
      conversationId = conversationId,
      caseReferenceNumber = caseReferenceNumber,
      applicationName = applicationName,
      upscanReference = uploadedFile.reference,
      downloadUrl = uploadedFile.downloadUrl,
      checksum = uploadedFile.checksum,
      fileName = uploadedFile.fileName,
      fileMimeType = uploadedFile.fileMimeType,
      fileSize = None,
      batchSize = batchSize,
      batchCount = batchCount,
      correlationId = Some(correlationId)
    )

  implicit val formats: Format[FileTransferRequest] = Json.format[FileTransferRequest]
}
