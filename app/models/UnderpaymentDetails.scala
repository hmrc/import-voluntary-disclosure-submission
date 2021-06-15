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

import models.UserTypes.UserType
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class UnderpaymentDetails(userType: UserType,
                               isBulkEntry: Boolean,
                               isEuropeanUnionDuty: Boolean,
                               reasonForAmendment: String,
                               entryProcessingUnit: Option[String],
                               entryNumber: Option[String],
                               entryDate: Option[LocalDate],
                               originalCustomsProcedureCode: String,
                               declarantName: String,
                               declarantPhoneNumber: String,
                               defermentType: Option[String] = None,
                               defermentAccountNumber: Option[String] = None,
                               additionalDefermentNumber: Option[String] = None
                              )

object UnderpaymentDetails {

  private val formattedDate = DateTimeFormatter.ofPattern("yyyyMMdd")
  private val knownDefermentTypes = Seq("A", "B", "C", "D")
  val validDefermentType: String => Boolean = defermentType => knownDefermentTypes.contains(defermentType)

  implicit val reads: Reads[UnderpaymentDetails] = (
    (__ \ "userType").read[UserType] and
      (__ \ "isBulkEntry").read[Boolean] and
      (__ \ "isEuropeanUnionDuty").read[Boolean] and
      (__ \ "additionalInfo").read[String] and
      (__ \\ "epu").readNullable[String] and
      (__ \\ "entryNumber").readNullable[String] and
      (__ \\ "entryDate").readNullable[LocalDate] and
      (__ \ "customsProcessingCode").read[String] and
      (__ \ "declarantContactDetails" \ "fullName").read[String] and // TODO: needs to come from declarant specific location
      (__ \ "declarantContactDetails" \ "phoneNumber").read[String] and
      (__ \ "defermentType").readNullable[String](filter(JsonValidationError("Invalid Deferement Type"))(validDefermentType)) and
      (__ \ "defermentAccountNumber").readNullable[String] and
      (__ \ "additionalDefermentNumber").readNullable[String]
    ) (UnderpaymentDetails.apply _)

  implicit val writes: Writes[UnderpaymentDetails] = (data: UnderpaymentDetails) => {

    val various = "VARIOUS"
    val isBulk = data.isBulkEntry
    val isBulkEntry = if (isBulk) "01" else "02"
    val isEuropeanUnionDuty = if (data.isEuropeanUnionDuty) "01" else "02"
    val epu = if (isBulk) various else data.entryProcessingUnit.getOrElse(throw new RuntimeException("EPU invalid"))
    val entryNumber = if (isBulk) various else data.entryNumber.getOrElse(throw new RuntimeException("Entry Number invalid"))
    val entryDate = if (isBulk) various else data.entryDate.getOrElse(throw new RuntimeException("Entry date invalid")).format(formattedDate)

    val defermentDetails = (data.defermentType, data.defermentAccountNumber, data.additionalDefermentNumber) match {
      case (Some(dt), Some(dan), Some(add)) =>
        Json.obj(
          "DefermentType" -> dt,
          "DefermentAccountNumber" -> dan,
          "AdditionalDefermentNumber" -> add
        )
      case (Some(dt), Some(dan), _) =>
        Json.obj(
          "DefermentType" -> dt,
          "DefermentAccountNumber" -> dan
        )
      case _ => Json.obj()
    }

    Json.obj(
      "RequestedBy" -> data.userType,
      "IsBulkEntry" -> isBulkEntry,
      "IsEUDuty" -> isEuropeanUnionDuty,
      "EPU" -> epu,
      "EntryNumber" -> entryNumber,
      "EntryDate" -> entryDate,
      "ReasonForAmendment" -> data.reasonForAmendment,
      "OriginalCustomsProcCode" -> data.originalCustomsProcedureCode,
      "DeclarantDate" -> LocalDate.now().format(formattedDate),
      "DeclarantPhoneNumber" -> data.declarantPhoneNumber,
      "DeclarantName" -> data.declarantName
    ) ++ defermentDetails
  }
}
