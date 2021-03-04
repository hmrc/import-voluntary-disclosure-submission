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

package connectors

import config.AppConfig
import connectors.httpParsers.EoriDetailsHttpParser.EoriDetailsReads
import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import models.EoriDetails
import org.scalatest.matchers.should.Matchers._
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}
import java.util.{Locale, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EoriDetailsConnector @Inject()(val http: HttpClient,
                                     implicit val config: AppConfig) {

  private val httpDateFormat = DateTimeFormatter
    .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    .withZone(ZoneId.of("GMT"))

  private[connectors] def getEoriDetailsUrl(id: String) = s"${config.sub09}/subscriptions/subscriptiondisplay/v1"

  private[connectors] def sub09HeaderCarrier()(implicit hc: HeaderCarrier, correlationId: UUID): HeaderCarrier =
    hc
    .copy(authorization = Some(Authorization(s"Bearer ${config.createCaseToken}")))
    .withExtraHeaders(
      headers = "Date" -> httpDateFormat.format(ZonedDateTime.now),
      "X-Correlation-ID" -> correlationId.toString,
      "X-Forwarded-Host" -> "",
      "Content-Type" -> "", // TODO
      "Accept" -> "application/json",
      "X-Source-System" -> "" // TODO
    )

  def getEoriDetails(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpGetResult[EoriDetails]] = {

    implicit val acknowledgementReference: UUID = UUID.randomUUID()

    val parameters = Seq(
      "regime" -> "CDS", // TODO - what is this
      "acknowledgementReference" -> "", // TODO - what is this
      "EORI" -> id
    )

    http.GET[HttpGetResult[EoriDetails]](
      url = getEoriDetailsUrl(id),
      queryParams = parameters
    )(hc = sub09HeaderCarrier(), ec = ec, rds = implicitly)
  }

}
