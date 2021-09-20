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

import models.DutyTypes.DutyType
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class DutyItem(dutyType: DutyType, originalAmount: BigDecimal, amendedAmount: BigDecimal)

object DutyItem {
  implicit val reads: Reads[DutyItem] = (
    (__ \ "duty").read[DutyType] and
      (__ \ "original").read[BigDecimal] and
      (__ \ "amended").read[BigDecimal]
  )(DutyItem.apply _)

  implicit val writes: Writes[DutyItem] = (data: DutyItem) =>
    Json.obj(
      "Type"              -> data.dutyType,
      "PaidAmount"        -> data.originalAmount.toString(),
      "DueAmount"         -> data.amendedAmount.toString(),
      "OutstandingAmount" -> (data.amendedAmount - data.originalAmount).toString()
    )
}
