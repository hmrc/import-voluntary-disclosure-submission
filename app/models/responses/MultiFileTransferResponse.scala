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

package models.responses

import play.api.libs.json.{Format, Json, OFormat}

import java.time.LocalDateTime

final case class MultiFileTransferResponse(
  conversationId: String,
  caseReferenceNumber: String,
  applicationName: String,
  results: Seq[FileUploadResult]
)

object MultiFileTransferResponse {
  implicit val formats: OFormat[MultiFileTransferResponse] = Json.format[MultiFileTransferResponse]
}

final case class FileUploadResult(
  upscanReference: String,
  fileName: String,
  fileMimeType: String,
  success: Boolean,
  httpStatus: Int,
  transferredAt: LocalDateTime,
  correlationId: String,
  durationMillis: Long,
  error: Option[String]
)

object FileUploadResult {
  implicit val formats: Format[FileUploadResult] = Json.format[FileUploadResult]
}
