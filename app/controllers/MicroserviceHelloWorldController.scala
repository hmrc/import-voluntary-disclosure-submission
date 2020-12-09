/*
 * Copyright 2020 HM Revenue & Customs
 *
 */

package controllers

import config.AppConfig
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton()
class MicroserviceHelloWorldController @Inject()(appConfig: AppConfig, cc: ControllerComponents)
    extends BackendController(cc) {

  def hello(): Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Hello world"))
  }
}
