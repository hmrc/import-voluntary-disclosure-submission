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

import controllers.actions.AuthorisedAction
import models.{UpdateCase, UpdateCaseError}
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import services.UpdateCaseService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class UpdateCaseController @Inject() (
  cc: ControllerComponents,
  service: UpdateCaseService,
  authAction: AuthorisedAction,
  implicit val ec: ExecutionContext
) extends BackendController(cc) {

  def onSubmit(): Action[JsValue] = (Action(parse.json) andThen authAction).async { implicit request =>
    request.body.validate[UpdateCase] match {
      case JsSuccess(value, _) =>
        service.updateCase(value).map {
          case Right(response)                             => Ok(Json.toJson(response))
          case Left(UpdateCaseError.UnexpectedError(_, _)) => InternalServerError(Json.obj())
          case Left(err)                                   => BadRequest(Json.toJson(err))
        }
      case JsError(errors) =>
        val pathsWithErrors: Map[String, String] = errors.map { error =>
          val (path, errors) = error
          path.toString().substring(1) -> errors.head.message
        }.toMap
        Future.successful(BadRequest(Json.obj("errors" -> pathsWithErrors)))
    }

  }

}
