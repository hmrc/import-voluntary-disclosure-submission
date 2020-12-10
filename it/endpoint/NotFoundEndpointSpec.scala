/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.http.Status
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.AuditStub
import support.IntegrationSpec

class NotFoundEndpointSpec extends IntegrationSpec {

  "calling an unknown route" should {

    "return an Not Found response" in {

      AuditStub.audit()

      val request: WSRequest = buildRequest("/some-path-that-does-not-exist")

      val response: WSResponse = await(request.get())

      response.status shouldBe Status.NOT_FOUND

    }

  }
}

