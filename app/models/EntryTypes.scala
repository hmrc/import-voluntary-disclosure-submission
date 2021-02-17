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

import play.api.libs.json.{JsString, Reads, Writes}

object EntryTypes extends Enumeration {

  type EntryType = Value

  val Multiple: EntryType = Value("moreThanOneEntry")
  val Single: EntryType = Value("oneEntry")

  implicit val reads: Reads[EntryType] = Reads.enumNameReads(this)

  implicit val writes: Writes[EntryType] = {
    case Multiple => JsString("01")
    case Single => JsString("02")
  }
}
