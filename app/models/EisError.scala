/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.Reads

sealed trait EisError extends Product with Serializable

object EisError {
  final case class BackendError(correlationId: String, errorCode: Option[String], errorMessage: Option[String])
      extends EisError
  final case class UnexpectedError(status: Int, reason: String) extends EisError

  implicit val reads: Reads[EisError] =
    Reads { json =>
      val detail = json \ "errorDetail"
      for {
        correlationId <- (detail \ "correlationId").validate[String]
        code          <- (detail \ "errorCode").validateOpt[String]
        message       <- (detail \ "errorMessage").validateOpt[String]
      } yield EisError.BackendError(correlationId, code, message)
    }
}
