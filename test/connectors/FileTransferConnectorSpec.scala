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

package connectors

import base.SpecBase
import data.SampleData
import mocks.MockHttp
import models.requests.FileTransferRequest
import org.scalatest.matchers.should.Matchers._
import play.api.http.Status
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HttpResponse

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.TimeoutException

class FileTransferConnectorSpec extends SpecBase {

  trait Test extends MockHttp with SampleData {
    val correlationId: UUID = UUID.randomUUID()
    lazy val target = new FileTransferConnector(appConfig, mockHttp)
  }

  val expectedUrl = "http://localhost:10003/transfer-file"

  "transferFile" should {

    "return the correct URL" in new Test {
      target.url shouldBe expectedUrl
    }

  }

  "transferFile" when {

    val upscanReference = "some reference"
    val request = FileTransferRequest(
      conversationId = "conversation ID",
      caseReferenceNumber = "case reference",
      applicationName = "C18",
      upscanReference = upscanReference,
      downloadUrl = "some url",
      checksum = "file checksum",
      fileName = "file name",
      fileMimeType = "file MIME type",
      batchSize = 1,
      batchCount = 1
    )

    "a success response is returned from the file transfer microservice" should {

      "return a success FileTransferResponse" in new Test {
        MockedHttp.post[FileTransferRequest, HttpResponse](expectedUrl, HttpResponse(Status.ACCEPTED, ""))

        private val result = await(target.transferFile(request))

        result.success shouldBe true
        result.upscanReference shouldBe upscanReference
        result.error shouldBe None
      }

    }

    "an error response is returned from the file transfer microservice" should {

      "return a failed FileTransferResponse" in new Test {
        MockedHttp.post[FileTransferRequest, HttpResponse](expectedUrl, HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        private val result = await(target.transferFile(request))

        result.success shouldBe false
        result.upscanReference shouldBe upscanReference
        result.error shouldBe Some("HTTP response status 500")
      }

    }

    "an error occurs whilst calling the file transfer microservice" should {

      "return a failed FileTransferResponse" in new Test {
        val timeoutException = new TimeoutException("took too long")
        MockedHttp.postError[FileTransferRequest, HttpResponse](expectedUrl, timeoutException)

        private val result = await(target.transferFile(request))

        result.success shouldBe false
        result.upscanReference shouldBe upscanReference
        result.error shouldBe Some("took too long")
      }

    }

  }

}
