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

package connectors

import config.AppConfig
import connectors.httpParsers.EoriDetailsHttpParser.EoriDetailsReads
import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import models.EoriDetails
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}
import java.util.{Locale, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EoriDetailsConnector @Inject() (val http: HttpClient, implicit val config: AppConfig) {

  private[connectors] val httpDateFormat = DateTimeFormatter
    .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    .withZone(ZoneId.of("GMT"))

  private[connectors] def getEoriDetailsUrl = s"${config.sub09}/subscriptions/subscriptiondisplay/v1"

  private[connectors] def headers(correlationId: UUID): Seq[(String, String)] = Seq(
    "Authorization"    -> s"Bearer ${config.eoriDetailsToken}",
    "Date"             -> httpDateFormat.format(ZonedDateTime.now),
    "X-Correlation-ID" -> correlationId.toString,
    "Accept"           -> "application/json",
    "X-Forwarded-Host" -> "MDTP"
  )

  def getEoriDetails(
    id: String
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[EoriDetails]] = {

    val acknowledgementReference: UUID = UUID.randomUUID()

    val parameters = Seq(
      "regime"                   -> "CDS",
      "acknowledgementReference" -> acknowledgementReference.toString.replace("-", ""),
      "EORI"                     -> id
    )

    http.GET[HttpGetResult[EoriDetails]](
      url = getEoriDetailsUrl,
      queryParams = parameters,
      headers = headers(acknowledgementReference)
    )
  }

}
