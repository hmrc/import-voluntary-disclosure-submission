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

package base

import config.AppConfig
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.{AnyContentAsEmpty, ControllerComponents}
import play.api.test.CSRFTokenHelper.CSRFRequest
import play.api.test.{FakeRequest, Helpers}
import play.api.{Configuration, Environment}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import scala.concurrent.ExecutionContext

trait SpecBase extends AnyWordSpec with MockitoSugar {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/foo")
    .withSession(SessionKeys.sessionId -> "foo")
    .withCSRFToken
    .asInstanceOf[FakeRequest[AnyContentAsEmpty.type]]

  val env: Environment             = Environment.simple()
  val configuration: Configuration = Configuration.load(env)

  val injector: Injector   = new GuiceApplicationBuilder().injector()
  val appConfig: AppConfig = injector.instanceOf[AppConfig]

  val controllerComponents: ControllerComponents = Helpers.stubControllerComponents()

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit lazy val ec: ExecutionContext = injector.instanceOf[ExecutionContext]

}
