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

final case class MultiFileTransferRequest(
  conversationId: String,
  caseReferenceNumber: String,
  applicationName: String,
  files: Seq[SingleFile],
  callbackUrl: String,
  correlationId: Option[String] = None
)

object MultiFileTransferRequest {
  def fromSupportingDocuments(
    caseReferenceNumber: String,
    conversationId: String,
    correlationId: String,
    applicationName: String,
    uploadedFiles: Seq[SupportingDocument],
    callbackUrl: String
  ): MultiFileTransferRequest =
    MultiFileTransferRequest(
      conversationId = conversationId,
      caseReferenceNumber = caseReferenceNumber,
      applicationName = applicationName,
      files = uploadedFiles.map(SingleFile.fromSupportingDocument),
      correlationId = Some(correlationId),
      callbackUrl = callbackUrl
    )

  implicit val formats: Format[MultiFileTransferRequest] = Json.format[MultiFileTransferRequest]
}

final case class SingleFile(
  upscanReference: String,
  downloadUrl: String,
  fileName: String,
  fileMimeType: String,
  checksum: String,
  fileSize: Option[Int] = None
)

object SingleFile {
  def fromSupportingDocument(doc: SupportingDocument): SingleFile =
    SingleFile(
      upscanReference = doc.reference,
      downloadUrl = doc.downloadUrl,
      fileName = doc.fileName,
      fileMimeType = doc.fileMimeType,
      checksum = doc.checksum
    )

  implicit val formats: Format[SingleFile] = Json.format[SingleFile]
}
