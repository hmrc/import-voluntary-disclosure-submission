/*
 * Copyright 2022 HM Revenue & Customs
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
import models.ErrorModel
import models.audit.FilesUploadedAuditEvent
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.{Eventually, Waiters}
import org.scalatest.matchers.must.Matchers
import play.api.http.Status
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import utils.ReusableValues

import scala.concurrent.Future

class FileTransferServiceSpec extends SpecBase with Matchers with MockFactory with Waiters {

  trait Test extends MockFileTransferConnector with MockAuditService with ReusableValues with Eventually {
    val system: ActorSystem            = injector.instanceOf[ActorSystem]
    val servicesConfig: ServicesConfig = injector.instanceOf[ServicesConfig]
    def appConfig: AppConfig           = new AppConfigImpl(configuration, servicesConfig)
    lazy val service = new FileTransferService(system, mockFileTransferConnector, mockAuditService, appConfig)
  }

  "Attempting a file transfer" when {
    "the multiFileUpload feature is on" should {
      "make a batch request with supplied files" in new Test {
        FileTransferConnector.transferMultipleFiles(Future.successful(Right(())))

        await(service.transferFiles("C18123", "123", Seq(doc))(hc, ec, fakeRequest))
        withExpectations(())
      }

      "succeed even after 2 requests fail" in new Test {
        FileTransferConnector.transferMultipleFiles(
          Future.successful(Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "temporary issue")))
        )
          .repeat(2)
        FileTransferConnector.transferMultipleFiles(Future.successful(Right(())))

        await(service.transferFiles("C18123", "123", Seq(doc))(hc, ec, fakeRequest))
        withExpectations(())
      }

      "give up after 3 failed attempts" in new Test {
        override lazy val service: FileTransferService =
          new FileTransferService(system, mockFileTransferConnector, mockAuditService, appConfig) {
            override def newCorrelationId(): String = "123"
          }

        FileTransferConnector.transferMultipleFiles(
          Future.successful(Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "temporary issue")))
        ).repeat(3)

        AuditService.audit(
          FilesUploadedAuditEvent(Seq(fileTransferResponse.copy(fileTransferSuccess = false, duration = 0)), "C18123")
        )

        await(service.transferFiles("C18123", "123", Seq(doc))(hc, ec, fakeRequest))
        withExpectations(())
      }
    }

  }
}
