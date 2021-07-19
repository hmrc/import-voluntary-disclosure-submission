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

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class UpdateCase(caseId: String,
                      supportingDocuments: Seq[SupportingDocument],
                      additionalInfo: String)

object UpdateCase {
  implicit val reads: Reads[UpdateCase] = (
    (__ \ "caseId").read[String] and
      (__ \ "supportingDocuments").readNullable[Seq[SupportingDocument]].map(_.getOrElse(Seq.empty)) and
      (__ \ "additionalInfo").read[String]
    ) (UpdateCase.apply _)


  implicit val writes: Writes[UpdateCase] = (update: UpdateCase) =>
    Json.obj(
      "CaseID" -> update.caseId,
      "Description" -> update.additionalInfo
    )
}
