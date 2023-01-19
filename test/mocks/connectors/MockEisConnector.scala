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

package mocks.connectors

import connectors.EisConnector
import models.responses.{CreateCaseResponse, UpdateCaseResponse}
import models.{CreateCase, EisError, UpdateCase, UpdateCaseError}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockEisConnector extends MockFactory {

  val mockEisConnector: EisConnector = mock[EisConnector]

  object MockedEisConnector {

    def createCase(
      caseDetails: CreateCase,
      response: Either[EisError, CreateCaseResponse]
    ): CallHandler[Future[Either[EisError, CreateCaseResponse]]] =
      (mockEisConnector.createCase(_: CreateCase)(_: HeaderCarrier, _: ExecutionContext))
        .expects(caseDetails, *, *)
        .returns(Future.successful(response))

    def updateCase(
      caseDetails: UpdateCase,
      response: Either[UpdateCaseError, UpdateCaseResponse]
    ): CallHandler[Future[Either[UpdateCaseError, UpdateCaseResponse]]] =
      (mockEisConnector.updateCase(_: UpdateCase)(_: HeaderCarrier, _: ExecutionContext))
        .expects(caseDetails, *, *)
        .returns(Future.successful(response))
  }

}
