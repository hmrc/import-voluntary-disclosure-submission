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

import play.api.libs.json.{Format, Json, Reads, _}

// TODO - change mandatory ones to non optionals
case class TraderAddress(streetAndNumber: Option[String], city: Option[String], postalCode: Option[String], countryCode: Option[String])

object TraderAddress {

  implicit val reads: Reads[TraderAddress] = for {
    streetAndNumber <- (__ \\ "streetAndNumber").readNullable[String]
    city <- (__ \\ "city").readNullable[String]
    postalCode <- (__ \\ "postalCode").readNullable[String]
    countryCode <- (__ \\ "countryCode").readNullable[String]
  } yield {
    (streetAndNumber, city, postalCode, countryCode) match {
      case (None, None, None, None) => TraderAddress(Some(""), Some(""), None, Some(""))
      case (_, _, _, _) => TraderAddress(streetAndNumber, city, postalCode, countryCode)
    }
  }

  implicit val format: Format[TraderAddress] = Json.format[TraderAddress]

}
