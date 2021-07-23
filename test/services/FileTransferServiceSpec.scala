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

package services

import akka.actor.ActorSystem
import base.SpecBase
import config.{AppConfig, AppConfigImpl}
import mocks.connectors.MockFileTransferConnector
import mocks.services.MockAuditService
import models.audit.FilesUploadedAuditEvent
import models.responses._
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.{Eventually, Waiters}
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.mvc.Http.Status
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import utils.ReusableValues

import scala.concurrent.Future

class FileTransferServiceSpec extends SpecBase with MockFactory with Waiters {

  trait Test extends MockFileTransferConnector with MockAuditService with ReusableValues with Eventually {
    val system: ActorSystem = injector.instanceOf[ActorSystem]
    val servicesConfig: ServicesConfig = injector.instanceOf[ServicesConfig]
    def appConfig: AppConfig = new AppConfigImpl(configuration, servicesConfig)
    lazy val service = new FileTransferService(system, mockFileTransferConnector, mockAuditService, appConfig)

    val uploadResult: FileUploadResult =
      FileUploadResult(document.reference, document.fileName, document.fileMimeType, success = true, Status.ACCEPTED, document.uploadTimestamp, "123", None)
    val fileTransferResponse: FileTransferResponse =
      FileTransferResponse(document.reference, document.fileName, document.fileMimeType, success = true, document.uploadTimestamp, 0)
    val multiFileTransferResponse: MultiFileTransferResponse =
      MultiFileTransferResponse("123", "C18123", "C18", Seq(uploadResult))
  }

  "Attempting a file transfer" when {
    "the multiFileUpload feature is on" should {
      "make a batch request with supplied files" in new Test {
        override def appConfig: AppConfig = new AppConfigImpl(configuration, servicesConfig) {
          override val multiFileUploadEnabled: Boolean = true
        }
        FileTransferConnector.transferMultipleFiles(Future.successful(Right(multiFileTransferResponse)))

        service.transferFiles("C18123", "123", Seq(document))(hc, ec, fakeRequest)
        withExpectations(())
      }
    }

    "the multiFileUpload feature is off" should {
      "make individual requests with supplied files" in new Test {
        override def appConfig: AppConfig = new AppConfigImpl(configuration, servicesConfig) {
          override val multiFileUploadEnabled: Boolean = false
        }
        FileTransferConnector.transferFile(Future.successful(fileTransferResponse))
        AuditService.audit(FilesUploadedAuditEvent(Seq(fileTransferResponse), "C18"))

        service.transferFiles("C18123", "123", Seq(document))(hc, ec, fakeRequest)
        // files are uploaded asynchronously, so we have to wait
        eventually {
          Thread.sleep(100)
          withExpectations(())
        }
      }
    }
  }
}