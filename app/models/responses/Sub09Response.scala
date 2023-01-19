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

package models.responses

import models.{EoriDetails, EoriStatus}
import play.api.libs.json.{Format, Json, Reads, Writes, __}

case class Sub09Response(eoriStatus: EoriStatus, eoriDetails: Option[EoriDetails])

object Sub09Response {
  val reads: Reads[Sub09Response] = for {
    eoriStatus  <- (__ \\ "subscriptionDisplayResponse" \\ "responseCommon").read[EoriStatus]
    eoriDetails <- (__ \\ "subscriptionDisplayResponse" \\ "responseDetail").readNullable[EoriDetails]
  } yield Sub09Response(eoriStatus, eoriDetails)

  val writes: Writes[Sub09Response] = Json.writes[Sub09Response]

  implicit val formats: Format[Sub09Response] = Format(reads, writes)
}
