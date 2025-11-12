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

package endpoint

import data.SampleData
import play.api.http.Status
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuditStub, AuthStub, CreateCaseStub, FileTransferStub}
import support.IntegrationSpec

class CreateCaseEndpointSpec extends IntegrationSpec with SampleData {

  "calling the create case endpoint with a valid payload" should {

    "return an OK response" in {

      AuditStub.audit()
      AuthStub.authorised()
      CreateCaseStub.success()
      FileTransferStub.success()

      val request: WSRequest = buildRequest("/case")

      val response: WSResponse = await(request.post(incomingJson))

      response.status shouldBe Status.OK

    }

  }
}
