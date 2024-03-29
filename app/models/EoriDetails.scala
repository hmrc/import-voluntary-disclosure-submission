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

import play.api.libs.json.{Json, Reads, _}

case class EoriDetails(
  eori: String,
  name: String,
  streetAndNumber: String,
  city: String,
  postalCode: Option[String],
  countryCode: String,
  vatNumber: Option[String]
)

object EoriDetails {

  private def getExpectedString(json: JsObject, key: String): Option[String] = (json \ key) match {
    case JsDefined(data: JsString) => Some(data.value)
    case _                         => None
  }

  implicit val reads: Reads[EoriDetails] = for {
    eori            <- (__ \\ "EORINo").read[String]
    name            <- (__ \\ "CDSFullName").read[String]
    streetAndNumber <- (__ \\ "CDSEstablishmentAddress" \ "streetAndNumber").read[String]
    city            <- (__ \\ "CDSEstablishmentAddress" \ "city").read[String]
    postalCode      <- (__ \\ "CDSEstablishmentAddress" \ "postalCode").readNullable[String]
    countryCode     <- (__ \\ "CDSEstablishmentAddress" \ "countryCode").read[String]
    vatIds          <- (__ \\ "VATIDs").readNullable[Seq[JsObject]]
  } yield {
    val vatNumber = vatIds.getOrElse(Seq(Json.obj()))
      .find(getExpectedString(_, "countryCode").contains("GB"))
      .flatMap(getExpectedString(_, "VATID"))
    EoriDetails(eori, name, streetAndNumber, city, postalCode, countryCode, vatNumber)
  }

  implicit val writes: Writes[EoriDetails] = Json.writes[EoriDetails]

}
