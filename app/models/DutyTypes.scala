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

object DutyTypes extends Enumeration {

  type DutyType = Value

  val A30: DutyType = Value("A30")
  val A35: DutyType = Value("A35")
  val A20: DutyType = Value("A20")
  val D10: DutyType = Value("D10")
  val ImportVat: DutyType = Value("importVat") // TODO - remove when feature switch is taken out
  val B00: DutyType = Value("B00")
  val ExciseDuty: DutyType = Value("exciseDuty") // TODO - remove when feature switch is taken out
  val E00: DutyType = Value("E00")
  val A40: DutyType = Value("A40")
  val A45: DutyType = Value("A45")
  val A10: DutyType = Value("A10")
  val CustomsDuty: DutyType = Value("customsDuty") // TODO - remove when feature switch is taken out
  val A00: DutyType = Value("A00")

  implicit val reads: Reads[DutyType] = Reads.enumNameReads(this)

  implicit val writes: Writes[DutyType] = {
    case A30 => JsString("A30")
    case A35 => JsString("A35")
    case A20 => JsString("A20")
    case D10 => JsString("D10")
    case ImportVat => JsString("B00") // TODO - remove when feature switch is taken out
    case B00 => JsString("B00")
    case ExciseDuty => JsString("E00") // TODO - remove when feature switch is taken out
    case E00 => JsString("E00")
    case A40 => JsString("A40")
    case A45 => JsString("A45")
    case A10 => JsString("A10")
    case CustomsDuty => JsString("A00") // TODO - remove when feature switch is taken out
    case A00 => JsString("A00")
  }
}
