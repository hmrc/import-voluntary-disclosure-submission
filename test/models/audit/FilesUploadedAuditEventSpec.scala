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

import base.SpecBase
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.libs.json.Json

class FilesUploadedAuditEventSpec extends SpecBase with AuditTestData {

  "A valid FilesUploadedAuditEvent model" should {

    "contain correct details" in {
      val event = FilesUploadedAuditEvent(Seq(fileTransferResponse), caseID)

      event.detail mustBe Json.parse(auditOutputJson)
    }

  }

}
