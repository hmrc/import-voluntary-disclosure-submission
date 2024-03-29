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

package stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import data.SampleData
import play.api.http.Status.ACCEPTED
import play.api.libs.json.{JsObject, Json}
import support.WireMockMethods

object FileTransferStub extends WireMockMethods with SampleData {

  private val authoriseUri = "/transfer-multiple-files"

  val headers: Map[String, String] = Map.empty
  val body: JsObject               = Json.obj()

  def success(): StubMapping =
    when(method = POST, uri = authoriseUri)
      .thenReturn(ACCEPTED, headers, body)
}
