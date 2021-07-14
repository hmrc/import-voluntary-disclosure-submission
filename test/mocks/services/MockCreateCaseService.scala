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

import models.responses.CreateCaseResponse
import models.{CreateCase, EisError}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import services.CreateCaseService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockCreateCaseService extends MockFactory {

  val mockCreateCaseService: CreateCaseService = mock[CreateCaseService]

  object MockedCreateCaseService {

    def createCase(caseDetails: CreateCase,
                   response: Either[EisError, CreateCaseResponse]): CallHandler[Future[Either[EisError, CreateCaseResponse]]] = {
      (mockCreateCaseService.createCase(_: CreateCase)(_: HeaderCarrier, _: ExecutionContext))
        .expects(caseDetails, *, *)
        .returns(Future.successful(response))
    }

  }

}
