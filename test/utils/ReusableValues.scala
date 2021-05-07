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

package utils

import models.EoriDetails
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse

trait ReusableValues {

  val idOne: String = "1"

  val eoriDetails: EoriDetails = EoriDetails(
    "GB987654321000",
    "Fast Food ltd",
    "99 Avenue Road",
    "Anyold Town",
    Some("99JZ 1AA"),
    "GB"
  )

  val errorModel: HttpResponse = HttpResponse(Status.NOT_FOUND, "Error Message")

  val detailsJson: JsObject = Json.obj(
    "subscriptionDisplayResponse" -> Json.obj(
      "responseCommon" -> Json.obj(
        "status" -> "OK",
        "statusText" -> "Optional status text from ETMP",
        "processingDate" -> "2016-09-17T19:33:47Z",
        "returnParameters" -> Json.arr(
          Json.obj("paramName" -> "POSITION",
            "paramValue" -> "LINK")
        )
      ),
      "responseDetail" -> Json.obj(
        "EORINo" -> "GB987654321000",
        "CDSFullName" -> "Fast Food ltd",
        "CDSEstablishmentAddress" -> Json.obj(
          "streetAndNumber" -> "99 Avenue Road",
          "city" -> "Anyold Town",
          "postalCode" -> "99JZ 1AA",
          "countryCode" -> "GB"
        )
      )
    )
  )

  val cleanedDetailsJson: JsObject = Json.obj(
    "eori" -> "GB987654321000",
    "name" -> "Fast Food ltd",
    "streetAndNumber" -> "99 Avenue Road",
    "city" -> "Anyold Town",
    "postalCode" -> "99JZ 1AA",
    "countryCode" -> "GB"
  )

}
