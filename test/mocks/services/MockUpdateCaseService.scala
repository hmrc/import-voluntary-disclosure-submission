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

package mocks.services

import connectors.httpParsers.ResponseHttpParser.ExternalResponse
import models.UpdateCase
import models.responses.UpdateCaseResponse
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import services.UpdateCaseService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockUpdateCaseService extends MockFactory {

  val mockUpdateCaseService: UpdateCaseService = mock[UpdateCaseService]

  object MockedUpdateCaseService {

    def updateCase(caseDetails: UpdateCase,
                   response: ExternalResponse[UpdateCaseResponse]): CallHandler[Future[ExternalResponse[UpdateCaseResponse]]] = {
      (mockUpdateCaseService.updateCase(_: UpdateCase)(_: HeaderCarrier, _: ExecutionContext))
        .expects(caseDetails, *, *)
        .returns(Future.successful(response))
    }

  }

}
