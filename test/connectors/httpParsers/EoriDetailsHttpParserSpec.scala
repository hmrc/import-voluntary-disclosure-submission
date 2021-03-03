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

package connectors.httpParsers

import base.SpecBase
import connectors.httpParsers.EoriDetailsHttpParser.EoriDetailsReads
import models.{EoriDetails, ErrorModel}
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HttpResponse
import utils.ReusableValues

class EoriDetailsHttpParserSpec extends SpecBase with ReusableValues {

  val eoriDetailsJsonWithoutPostcode: JsObject = Json.obj(
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

  val eoriDetailsWithoutPostcode: EoriDetails = EoriDetails(
    "GB987654321000",
    "Fast Food ltd",
    "99 Avenue Road",
    "Anyold Town",
    Some("99JZ 1AA"),
    "GB"
  )

  "Eori Details HttpParser" should {

    "the http response status is OK with valid Json" in {
      EoriDetailsReads.read("", "",
        HttpResponse(Status.OK, detailsJson, Map.empty[String, Seq[String]])) mustBe Right(eoriDetails)
    }

    "the http response status is OK with valid Json - postcode none" in {
      EoriDetailsReads.read("", "",
        HttpResponse(Status.OK, eoriDetailsJsonWithoutPostcode, Map.empty[String, Seq[String]])) mustBe Right(eoriDetailsWithoutPostcode)
    }

    "return an ErrorModel when invalid Json is returned" in {
      EoriDetailsReads.read("", "",
        HttpResponse(Status.OK, Json.obj(), Map.empty[String, Seq[String]])) mustBe
        Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json returned from SUB09 API for EoriDetailsHttpParser"))
    }

    "return an ErrorModel when NOT_FOUND is returned" in {
      EoriDetailsReads.read("", "",
        HttpResponse(Status.NOT_FOUND, "")) mustBe
        Left(ErrorModel(Status.NOT_FOUND, "Downstream error returned when retrieving EoriDetails model from Sub09 (stub atm)"))
    }
  }

}
