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

package services

import connectors.EisConnector
import models.responses.CreateCaseResponse
import models.{CreateCase, EisError}
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateCaseService @Inject()(connector: EisConnector, fileTransferService: FileTransferService) {

  def createCase(caseDetails: CreateCase)
                (implicit hc: HeaderCarrier, executionContext: ExecutionContext, request: Request[_]): Future[Either[EisError, CreateCaseResponse]] = {
    connector.createCase(caseDetails) map {
      case success@Right(details) =>
        fileTransferService.transferFiles(details.id, details.correlationId, caseDetails.supportingDocuments)
        success
      case failure => failure
    }
  }

}
