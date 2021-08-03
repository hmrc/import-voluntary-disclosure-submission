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

package config

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfigImpl @Inject()(config: Configuration, servicesConfig: ServicesConfig) extends AppConfig {

  val appName: String = servicesConfig.getString("appName")

  val authBaseUrl: String = servicesConfig.baseUrl("auth")

  val auditingEnabled: Boolean = config.get[Boolean]("auditing.enabled")
  val multiFileUploadEnabled: Boolean = config.get[Boolean]("features.multiFileUpload")

  val graphiteHost: String = config.get[String]("microservice.metrics.graphite.host")

  lazy val sub09: String = servicesConfig.baseUrl("mdg")
  lazy val eisBaseUrl: String = servicesConfig.baseUrl("eis")
  lazy val fileTransferUrl: String = servicesConfig.baseUrl("file-transmission-synchronous")
  lazy val createCaseToken: String = config.get[String]("microservice.services.eis.tokens.create")
  lazy val eoriDetailsToken: String = config.get[String]("microservice.services.mdg.tokens.eoriDetails")

  val fileUploadCallbackUrl: String = config.get[String]("file-upload.callback-url")
}

trait AppConfig {

  val appName: String
  val authBaseUrl: String
  val auditingEnabled: Boolean
  val multiFileUploadEnabled: Boolean
  val graphiteHost: String
  val sub09: String
  val eisBaseUrl: String
  val fileTransferUrl: String
  val createCaseToken: String
  val eoriDetailsToken: String
  val fileUploadCallbackUrl: String
}
