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

package models

import play.api.libs.json.{Json, Writes}
import play.api.http.Status

sealed trait UpdateCaseError extends Product with Serializable

object UpdateCaseError {
  case object InvalidCaseId                                              extends UpdateCaseError
  case object CaseAlreadyClosed                                          extends UpdateCaseError
  final case class UnexpectedError(status: Int, message: Option[String]) extends UpdateCaseError

  def fromEisError(error: EisError): UpdateCaseError = {
    error match {
      case EisError.BackendError(_, _, Some("9xx : 03- Invalid Case ID")) =>
        UpdateCaseError.InvalidCaseId
      case EisError.BackendError(_, _, Some("9xx : 04 - Requested case already closed")) =>
        UpdateCaseError.CaseAlreadyClosed
      case EisError.BackendError(_, code, message) =>
        UpdateCaseError.UnexpectedError(code.map(_.toInt).getOrElse(Status.INTERNAL_SERVER_ERROR), message)
      case EisError.UnexpectedError(status, reason) =>
        UpdateCaseError.UnexpectedError(status, Some(reason))
    }
  }

  implicit val writes: Writes[UpdateCaseError] = Writes {
    case UpdateCaseError.InvalidCaseId =>
      Json.obj("errorCode" -> 1, "errorMessage" -> "Invalid case ID")
    case UpdateCaseError.CaseAlreadyClosed =>
      Json.obj("errorCode" -> 2, "errorMessage" -> "Requested case is already closed")
    case UpdateCaseError.UnexpectedError(message, _) =>
      Json.obj("errorCode" -> 3, "errorMessage" -> message)
  }
}
