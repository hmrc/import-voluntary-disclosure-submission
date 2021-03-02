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

import play.api.libs.json.{Json, Reads, Writes}

object DocumentTypes extends Enumeration {

  type DocumentType = Value

  val OriginalC88: DocumentType = Value("OriginalC88")
  val OriginalC2: DocumentType = Value("OriginalC2")
  val AmendedSubstituteEntryWorksheet: DocumentType = Value("AmendedSubstituteEntryWorksheet")
  val AmendedC88: DocumentType = Value("AmendedC88")
  val AmendedC2: DocumentType = Value("AmendedC2")
  val InvoiceAirwayBillPreferenceCertificate: DocumentType = Value("InvoiceAirwayBillPreferenceCertificate")
  val DefermentAuthorisation: DocumentType = Value("DefermentAuthorisation")
  val Other: DocumentType = Value("Other")

  implicit val reads: Reads[DocumentType] = Reads.enumNameReads(this)

  implicit val writes: Writes[DocumentType] = {
    case OriginalC88 => Json.obj("Type" -> "01")
    case OriginalC2 => Json.obj("Type" -> "02")
    case AmendedSubstituteEntryWorksheet => Json.obj("Type" -> "03")
    case AmendedC88 => Json.obj("Type" -> "04")
    case AmendedC2 => Json.obj("Type" -> "05")
    case InvoiceAirwayBillPreferenceCertificate => Json.obj("Type" -> "06")
    case DefermentAuthorisation => Json.obj("Type" -> "07")
    case Other => Json.obj("Type" -> "08")
  }
}
