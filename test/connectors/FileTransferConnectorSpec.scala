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

package connectors

import base.SpecBase
import data.SampleData
import models.requests.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.EitherValues
import org.scalatest.matchers.should.Matchers.*
import play.api.http.Status
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}

import java.util.UUID
import scala.concurrent.{Future, TimeoutException}

class FileTransferConnectorSpec extends SpecBase with EitherValues {

  trait Test extends SampleData {
    val correlationId: UUID            = UUID.randomUUID()
    val mockHttpClient: HttpClientV2   = mock[HttpClientV2]
    val requestBuilder: RequestBuilder = mock[RequestBuilder]
    lazy val target                    = new FileTransferConnector(appConfig, mockHttpClient)
  }

  val expectedMultiFileUrl = "http://localhost:10003/transfer-multiple-files"

  "transferFile" should {

    "return the correct URLs" in new Test {
      target.multiFileUrl shouldBe expectedMultiFileUrl
    }

  }

  "transferMultipleFiles" when {

    val upscanReference = "XYZ0123456789"
    val request         = MultiFileTransferRequest(
      conversationId = "074c3823-c941-417e-a08b-e47b08e9a9b7",
      caseReferenceNumber = "C18123",
      applicationName = "C18",
      files = Seq(
        SingleFile(
          upscanReference = upscanReference,
          downloadUrl = "some url",
          checksum = "file checksum",
          fileName = "file name",
          fileMimeType = "file MIME type"
        )
      ),
      callbackUrl = "localhost/internal/callback"
    )

    "a success response is returned from the file transfer microservice" should {

      "return a success MultiFileTransferResponse" in new Test {
        when(mockHttpClient.post(any())(any())).thenReturn(requestBuilder)
        when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
        when(requestBuilder.execute(any(), any())).thenReturn(Future.successful(HttpResponse(Status.ACCEPTED, "")))

        private val resp = await(target.transferMultipleFiles(request))
        resp shouldBe Right(())
      }

    }

    "an error response is returned from the file transfer microservice" should {

      "return a failed response" in new Test {
        when(mockHttpClient.post(any())(any())).thenReturn(requestBuilder)
        when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
        when(requestBuilder.execute(any(), any())).thenReturn(
          Future.successful(HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))
        )

        private val resp = await(target.transferMultipleFiles(request)).left.value

        resp.status shouldBe Status.INTERNAL_SERVER_ERROR
        resp.message shouldBe "Unsuccessful file transfer response"
      }

    }

    "an error occurs whilst calling the file transfer microservice" should {

      "return a failed response" in new Test {
        val timeoutException = new TimeoutException("took too long")

        when(mockHttpClient.post(any())(any())).thenReturn(requestBuilder)
        when(requestBuilder.withBody(any())(any(), any(), any())).thenReturn(requestBuilder)
        when(requestBuilder.execute(any(), any())).thenReturn(Future.failed(timeoutException))

        private val resp = await(target.transferMultipleFiles(request)).left.value
        resp.message shouldBe "java.util.concurrent.TimeoutException: took too long"
      }

    }
  }

}
