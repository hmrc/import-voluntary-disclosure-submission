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

import models.DocumentTypes.DocumentType
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class CreateCase(
  underpaymentDetails: UnderpaymentDetails,
  duties: Seq[DutyItem],
  documentsSupplied: Seq[DocumentType],
  supportingDocuments: Seq[SupportingDocument],
  amendedItems: Option[Seq[BoxItem]],
  importer: TraderDetails,
  representative: Option[TraderDetails] = None
)

object CreateCase {
  implicit val reads: Reads[CreateCase] = (
    __.read[UnderpaymentDetails] and
      (__ \ "underpaymentDetails").read[Seq[DutyItem]] and
      (__ \ "supportingDocumentTypes").read[Seq[DocumentType]] and
      (__ \ "supportingDocuments").read[Seq[SupportingDocument]] and
      (__ \\ "amendedItems").readNullable[Seq[BoxItem]] and
      (__ \ "importer").read[TraderDetails] and
      (__ \ "representative").readNullable[TraderDetails]
  )(CreateCase.apply _)

  implicit val writes: Writes[CreateCase] = (o: CreateCase) => {

    val importer       = Some(Json.toJson(o.importer).as[JsObject] ++ Json.obj("Type" -> TraderTypes.Importer))
    val representative = o.representative.map { rep =>
      Json.toJson(rep).as[JsObject] ++ Json.obj("Type" -> TraderTypes.Representative)
    }

    val traders: Seq[JsObject] = Seq(representative, importer).flatten
    val importInfoList         =
      if (o.underpaymentDetails.isBulkEntry) Json.obj() else Json.obj("ImportInfoList" -> o.amendedItems)

    Json.obj(
      "UnderpaymentDetails" -> o.underpaymentDetails,
      "DutyTypeList"        -> o.duties,
      "DocumentList"        -> o.documentsSupplied,
      "TraderList"          -> traders
    ) ++ importInfoList
  }
}
