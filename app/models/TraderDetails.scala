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

import models.TraderTypes.TraderType
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class TraderDetails(traderType: TraderType,
                         name: String,
                         emailAddress: String,
                         phoneNumber: String,
                         addressLine1: String,
                         addressLine2: Option[String],
                         city: String,
                         county: Option[String],
                         countryCode: String,
                         postalCode: String)

object TraderDetails {
  implicit val reads: Reads[TraderDetails] = (
    (__ \ "userType").read[TraderType] and
      (__ \ "traderContactDetails" \ "fullName").read[String] and
      (__ \ "traderContactDetails" \ "email").read[String] and
      (__ \ "traderContactDetails" \ "phoneNumber").read[String] and
      (__ \ "traderAddress" \ "streetAndNumber").read[String] and
      (__ \ "traderAddress" \ "addressLine2").readNullable[String] and
      (__ \ "traderAddress" \ "city").read[String] and
      (__ \ "traderAddress" \ "county").readNullable[String] and
      (__ \ "traderAddress" \ "countryCode").read[String] and
      (__ \ "traderAddress" \ "postalCode").read[String]

    ) (TraderDetails.apply _)

  implicit val writes: Writes[TraderDetails] = (data: TraderDetails) => Json.obj(
    "Type" -> data.traderType,
    "EORI" -> "GB000000000000000", // TODO: Needs to come from frontend
    "Name" -> data.name,
    "EstablishmentAddress" -> Json.obj(
      "AddressLine1" -> data.addressLine1,
      "City" -> data.city,
      "CountryCode" -> data.countryCode,
      "PostalCode" -> data.postalCode,
      "TelephoneNumber" -> data.phoneNumber,
      "EmailAddress" -> data.emailAddress
    )
  )
}
