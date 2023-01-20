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

package controllers

import models.ErrorModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.EoriDetailsService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class EoriDetailsController @Inject() (
  cc: ControllerComponents,
  eoriDetailsService: EoriDetailsService,
  implicit val ec: ExecutionContext
) extends BackendController(cc) {

  def onLoad(id: String): Action[AnyContent] = Action.async { implicit request =>
    eoriDetailsService.retrieveEoriDetails(id).map {
      case Right(eoriDetails)             => Ok(Json.toJson(eoriDetails))
      case Left(ErrorModel(NOT_FOUND, _)) => NotFound("Could not retrieve eori details")
      case Left(_)                        => InternalServerError("Something went wrong retrieving eori details")
    }
  }

}
