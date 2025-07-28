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

package services

import connectors.EisConnector
import models.responses.UpdateCaseResponse
import models.{UpdateCase, UpdateCaseError}
import play.api.Logging
import play.api.mvc.Request
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UpdateCaseService @Inject() (connector: EisConnector, fileTransferService: FileTransferService) extends Logging {

  def updateCase(updateCase: UpdateCase)(implicit
    hc: HeaderCarrier,
    executionContext: ExecutionContext,
    request: Request[_]
  ): Future[Either[UpdateCaseError, UpdateCaseResponse]] = {
    connector.updateCase(updateCase) map {
      case success @ Right(details) =>
        if (updateCase.supportingDocuments.nonEmpty) {
          fileTransferService.transferFiles(details.id, details.correlationId, updateCase.supportingDocuments)
        }
        success
      case error @ Left(UpdateCaseError.UnexpectedError(status, message)) =>
        logger.error(
          s"Received an unexpected error for update case, cause: ${message.getOrElse("unknown")}, status: $status"
        )
        error
      case error @ Left(_) =>
        error
    }
  }

}
