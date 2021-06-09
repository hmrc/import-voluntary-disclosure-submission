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

import play.api.libs.json.{Json, Reads, _}

case class EoriDetails(eori: String,
                       name: String,
                       streetAndNumber: String,
                       city: String,
                       postalCode: Option[String],
                       countryCode: String,
                       vatIds: Option[String]
                      )

object EoriDetails {

  implicit val reads: Reads[EoriDetails] = for {
    eori <- (__ \\ "EORINo").read[String]
    name <- (__ \\ "CDSFullName").read[String]
    streetAndNumber <- (__ \\ "CDSEstablishmentAddress" \\ "streetAndNumber").read[String]
    city <- (__ \\ "CDSEstablishmentAddress" \\ "city").read[String]
    postalCode <- (__ \\ "CDSEstablishmentAddress" \\ "postalCode").readNullable[String]
    countryCode <- (__ \\ "CDSEstablishmentAddress" \\ "countryCode").read[String]
    vatIds <- (__ \\ "VATIDs").readNullable[Seq[JsObject]]
  } yield {
    val vatId = vatIds.getOrElse(Seq(Json.obj()))
    EoriDetails(eori, name, streetAndNumber, city, postalCode, countryCode,
      vatId.find(_("countryCode").as[String] == "GB")
        .map(_("VATID").as[String])
    )
  }

  implicit val writes: Writes[EoriDetails] = Json.writes[EoriDetails]

}
