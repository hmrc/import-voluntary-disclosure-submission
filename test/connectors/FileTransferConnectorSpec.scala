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
import models.requests._
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers._
import play.api.http.Status
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HttpResponse

import java.util.UUID
import scala.concurrent.TimeoutException

class FileTransferConnectorSpec extends SpecBase with EitherValues {

  trait Test extends MockHttp with SampleData {
    val correlationId: UUID = UUID.randomUUID()
    lazy val target = new FileTransferConnector(appConfig, mockHttp)
  }

  val expectedSingleFileUrl = "http://localhost:10003/transfer-file"
  val expectedMultiFileUrl = "http://localhost:10003/transfer-multiple-files"

  "transferFile" should {

    "return the correct URLs" in new Test {
      target.singleFileUrl shouldBe expectedSingleFileUrl
      target.multiFileUrl shouldBe expectedMultiFileUrl
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
        MockedHttp.post[FileTransferRequest, HttpResponse](expectedSingleFileUrl, HttpResponse(Status.ACCEPTED, ""))

        private val result = await(target.transferFile(request))

        result.fileTransferSuccess shouldBe true
        result.upscanReference shouldBe upscanReference
        result.fileTransferError shouldBe None
      }

    }

    "an error response is returned from the file transfer microservice" should {

      "return a failed FileTransferResponse" in new Test {
        MockedHttp.post[FileTransferRequest, HttpResponse](expectedSingleFileUrl, HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        private val result = await(target.transferFile(request))

        result.fileTransferSuccess shouldBe false
        result.upscanReference shouldBe upscanReference
        result.fileTransferError shouldBe Some("HTTP response status 500")
      }

    }

    "an error occurs whilst calling the file transfer microservice" should {

      "return a failed FileTransferResponse" in new Test {
        val timeoutException = new TimeoutException("took too long")
        MockedHttp.postError[FileTransferRequest, HttpResponse](expectedSingleFileUrl, timeoutException)

        private val result = await(target.transferFile(request))

        result.fileTransferSuccess shouldBe false
        result.upscanReference shouldBe upscanReference
        result.fileTransferError shouldBe Some("took too long")
      }

    }

  }

  "transferMultipleFiles" when {

    val upscanReference = "XYZ0123456789"
    val request = MultiFileTransferRequest(
      conversationId = "074c3823-c941-417e-a08b-e47b08e9a9b7",
      caseReferenceNumber = "C18123",
      applicationName = "C18",
      files = Seq(
        SingleFile(
          upscanReference = upscanReference,
          downloadUrl = "some url",
          checksum = "file checksum",
          fileName = "file name",
          fileMimeType = "file MIME type",
        )
      )
    )

    "a success response is returned from the file transfer microservice" should {

      "return a success MultiFileTransferResponse" in new Test {
        MockedHttp.post[MultiFileTransferRequest, HttpResponse](expectedMultiFileUrl, HttpResponse(Status.ACCEPTED, ""))

        private val resp = await(target.transferMultipleFiles(request))
        resp shouldBe Right(())
      }

    }

    "an error response is returned from the file transfer microservice" should {

      "return a failed response" in new Test {
        MockedHttp.post[MultiFileTransferRequest, HttpResponse](expectedMultiFileUrl, HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        private val resp = await(target.transferMultipleFiles(request)).left.value

        resp.status shouldBe Status.INTERNAL_SERVER_ERROR
        resp.message shouldBe "Unsuccessful file transfer response"
      }

    }

    "an error occurs whilst calling the file transfer microservice" should {

      "return a failed response" in new Test {
        val timeoutException = new TimeoutException("took too long")
        MockedHttp.postError[MultiFileTransferRequest, HttpResponse](expectedMultiFileUrl, timeoutException)

        private val resp = await(target.transferMultipleFiles(request)).left.value
        resp.message shouldBe "java.util.concurrent.TimeoutException: took too long"
      }

    }
  }

}
