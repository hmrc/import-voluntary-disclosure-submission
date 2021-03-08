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

import models.DocumentTypes.DocumentType
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class CaseDetails(underpaymentDetails: UnderpaymentDetails,
                       duties: Seq[DutyItem],
                       documentsSupplied: Seq[DocumentType],
                       supportingDocuments: Seq[SupportingDocument],
                       amendedItems: Seq[BoxItem],
                       importer: TraderDetails,
                       representative: Option[TraderDetails] = None)

object CaseDetails {
  implicit val reads: Reads[CaseDetails] = (
    __.read[UnderpaymentDetails] and
      (__ \ "underpaymentDetails").read[Seq[DutyItem]] and
      (__ \ "supportingDocumentTypes").read[Seq[DocumentType]] and
      (__ \ "supportingDocuments").read[Seq[SupportingDocument]] and
      (__ \ "amendedItems").read[Seq[BoxItem]] and
      (__ \ "importer").read[TraderDetails] and
      (__ \ "representative").readNullable[TraderDetails]
    ) (CaseDetails.apply _)


  implicit val writes: Writes[CaseDetails] = (o: CaseDetails) => {

    val importer = Some(Json.toJson(o.importer).as[JsObject] ++ Json.obj("Type" -> TraderTypes.Importer))
    val representative = o.representative.map{ rep =>
      Json.toJson(rep).as[JsObject] ++ Json.obj("Type" -> TraderTypes.Representative)
    }

    val traders: Seq[JsObject] = Seq(representative, importer).flatten

    Json.obj(
      "UnderpaymentDetails" -> o.underpaymentDetails,
      "DutyTypeList" -> o.duties,
      "DocumentList" -> o.documentsSupplied,
      "ImportInfoList" -> o.amendedItems,
      "TraderList" -> traders
    )
  }
}
