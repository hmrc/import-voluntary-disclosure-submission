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
import connectors.httpParsers.CreateCaseHttpParser.CreateCaseHttpReads
import connectors.httpParsers.ResponseHttpParser.ExternalResponse
import models.CaseDetails
import models.requests.CreateCaseRequest
import models.responses.CreateCaseResponse
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}
import java.util.{Locale, UUID}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EisConnector @Inject()(http: HttpClient,
                             implicit val appConfig: AppConfig) {

  private val httpDateFormat = DateTimeFormatter
    .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
    .withZone(ZoneId.of("GMT"))

  private[connectors] lazy val createCaseUrl = s"${appConfig.eisBaseUrl}/cpr/caserequest/c18/create/v1"

  private[connectors] def eisHeaderCarrier()(implicit hc: HeaderCarrier): HeaderCarrier = hc
    .copy(authorization = Some(Authorization(s"Bearer ${appConfig.createCaseToken}")))
    .withExtraHeaders(
      "x-correlation-id" -> UUID.randomUUID().toString,
      "CustomProcessesHost" -> "Digital",
      "date" -> httpDateFormat.format(ZonedDateTime.now),
      "accept" -> "application/json"
    )

  def createCase(caseDetails: CaseDetails)
                (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[ExternalResponse[CreateCaseResponse]] = {
    val request = CreateCaseRequest(caseDetails)
    http.POST(createCaseUrl, request)(implicitly, CreateCaseHttpReads, eisHeaderCarrier(), implicitly)
  }

}
