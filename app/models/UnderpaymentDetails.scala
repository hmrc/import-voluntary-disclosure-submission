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

import models.EntryTypes.EntryType
import models.UserTypes.UserType
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class UnderpaymentDetails(userType: UserType,
                               entryType: EntryType,
                               entryProcessingUnit: String,
                               entryNumber: String,
                               entryDate: LocalDate,
                               originalCustomsProcedureCode: String,
                               declarantName: String,
                               declarantPhoneNumber: String
                              )

object UnderpaymentDetails {

  private val formattedDate = DateTimeFormatter.ofPattern("yyMMdd")

  implicit val reads: Reads[UnderpaymentDetails] = (
    (__ \ "userType").read[UserType] and
      (__ \ "numEntries").read[EntryType] and
      (__ \ "entryDetails" \ "epu").read[String] and
      (__ \ "entryDetails" \ "entryNumber").read[String] and
      (__ \ "entryDetails" \ "entryDate").read[LocalDate] and
      (__ \ "originalCpc").read[String] and
      (__ \ "traderContactDetails" \ "fullName").read[String] and // TODO: needs to come from declarant specific location
      (__ \ "traderContactDetails" \ "phoneNumber").read[String] // TODO: needs to come from declarant specific location
    ) (UnderpaymentDetails.apply _)

  implicit val writes: Writes[UnderpaymentDetails] = (data: UnderpaymentDetails) => Json.obj(
    "RequestedBy" -> data.userType,
    "IsBulkEntry" -> data.entryType,
    "EPU" -> data.entryProcessingUnit,
    "EntryNumber" -> data.entryNumber,
    "EntryDate" -> data.entryDate.format(formattedDate),
    "IsEUDuty" -> "01", // TODO: needs to come from frontend
    "ReasonForAmendment" -> "Not Applicable", // TODO: needs to come from frontend
    "OriginalCustomsProcCode" -> data.originalCustomsProcedureCode,
    "DeclarantDate" -> LocalDate.now().format(formattedDate),
    "DeclarantPhoneNumber" -> data.declarantPhoneNumber,
    "DeclarantName" -> data.declarantName
  )
}
