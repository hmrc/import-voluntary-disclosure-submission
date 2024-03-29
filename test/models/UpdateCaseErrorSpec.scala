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

package models

import base.ModelSpecBase
import play.mvc.Http.Status

class UpdateCaseErrorSpec extends ModelSpecBase {

  "Converting to UpdateCaseError from EisError" should {
    "correctly handle InvalidCaseId" in {
      UpdateCaseError.fromEisError(EisError.BackendError("id", None, Some("9xx : 03- Invalid Case ID"))) shouldBe
        UpdateCaseError.InvalidCaseId
    }

    "correctly handle CaseAlreadyClosed" in {
      UpdateCaseError.fromEisError(
        EisError.BackendError("id", None, Some("9xx : 04 - Requested case already closed"))
      ) shouldBe UpdateCaseError.CaseAlreadyClosed
    }

    "correctly handle other, unexpected errors" in {
      UpdateCaseError.fromEisError(
        EisError.BackendError("id", Some("400"), Some("9xx : 06 - Invalid request format"))
      ) shouldBe UpdateCaseError.UnexpectedError(Status.BAD_REQUEST, Some("9xx : 06 - Invalid request format"))
    }

    "correctly handle InvalidCaseId without prefix and additional space" in {
      UpdateCaseError.fromEisError(EisError.BackendError("id", None, Some("03 - Invalid Case ID"))) shouldBe
        UpdateCaseError.InvalidCaseId
    }

    "correctly handle InvalidCaseId with a lowercase word" in {
      UpdateCaseError.fromEisError(EisError.BackendError("id", None, Some("9xx : 03- Invalid case ID"))) shouldBe
        UpdateCaseError.InvalidCaseId
    }
  }
}
