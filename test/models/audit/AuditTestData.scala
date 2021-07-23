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

package models.audit

import models.responses.FileTransferResponse

import java.time.LocalDateTime

trait AuditTestData {

  val year = 2021
  val month = 7
  val dayOfMonth = 21
  val hour = 11
  val minute = 45
  val second = 36
  val nanoOfSecond = 286
  val duration = 100
  val caseID = "C18-101"

  val fileTransferResponse: FileTransferResponse = FileTransferResponse(
    upscanReference = "8d53fae2-4e5b-4351-b0d3-b996ec35d808",
    fileName = "AnExampleDoc.pdf",
    fileMimeType = "application/pdf",
    fileTransferSuccess = true,
    transferredAt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond),
    duration = duration,
    fileTransferError = None
  )

  val auditOutputJson: String =
    """
      |{
      |   "summary":{
      |      "caseID":"C18-101",
      |      "totalFiles":1,
      |      "filesTransferredSuccessfully":1,
      |      "filesTransferFailures":0
      |   },
      |   "files":[
      |      {
      |         "upscanReference":"8d53fae2-4e5b-4351-b0d3-b996ec35d808",
      |         "fileName":"AnExampleDoc.pdf",
      |         "fileMimeType":"application/pdf",
      |         "success":true,
      |         "transferredAt":"2021-07-21T11:45:36.000000286",
      |         "duration":100
      |      }
      |   ]
      |}
      |""".stripMargin

}
