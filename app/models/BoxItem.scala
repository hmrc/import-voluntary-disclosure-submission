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

package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._

case class BoxItem(boxNumber: Int, itemNumber: Int, original: String, amended: String)

object BoxItem {

  private val knownBoxNumbers: Seq[Int] = Seq(22, 33, 34, 35, 36, 37, 38, 39, 41, 42, 43, 45, 46, 62, 63, 66, 67, 68)
  val validBoxNumber: Int => Boolean    = number => knownBoxNumbers.contains(number)

  implicit val reads: Reads[BoxItem] = (
    (__ \ "boxNumber").read[Int](filter(JsonValidationError("Invalid Box Number"))(validBoxNumber)) and
      (__ \ "itemNumber").read[Int] and
      (__ \ "original").read[String] and
      (__ \ "amended").read[String]
  )(BoxItem.apply _)

  implicit val writes: Writes[BoxItem] = (data: BoxItem) => {
    Json.obj(
      "BoxNumber"  -> "%02d".format(data.boxNumber),
      "ItemNumber" -> "%02d".format(data.itemNumber),
      "EnteredAs"  -> data.original,
      "AmendedTo"  -> data.amended
    )
  }
}
