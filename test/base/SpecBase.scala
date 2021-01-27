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

package base

import config.AppConfig
import org.scalamock.scalatest.MockFactory
import org.scalatest.TryValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.Helpers.baseApplicationBuilder.injector
import play.api.test.{FakeRequest, Helpers}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

trait SpecBase extends AnyWordSpec
  with GuiceOneAppPerSuite
  with TryValues
  with ScalaFutures
  with IntegrationPatience
  with MaterializerSupport
  with MockFactory {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/foo")
    .withSession(SessionKeys.sessionId -> "foo")
    .withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val env: Environment = Environment.simple()
  val configuration: Configuration = Configuration.load(env)

  val serviceConfig = new ServicesConfig(configuration)
  val appConfig = new AppConfig(configuration, serviceConfig)

  val controllerComponents: ControllerComponents = Helpers.stubControllerComponents()

  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  implicit val hc: HeaderCarrier = HeaderCarrier()

}
