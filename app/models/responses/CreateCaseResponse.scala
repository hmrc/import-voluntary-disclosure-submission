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

package models.responses

import play.api.libs.json._

case class CreateCaseResponse(id: String)

object CreateCaseResponse {
  val reads: Reads[CreateCaseResponse] = (json: JsValue) => {
    (json \ "CaseID").validate[String].fold(
      error => JsError(error),
      caseId => JsSuccess(CreateCaseResponse(caseId))
    )
  }

  val writes: Writes[CreateCaseResponse] = Json.writes[CreateCaseResponse]

  implicit val formats: Format[CreateCaseResponse] = Format(reads, writes)
}
