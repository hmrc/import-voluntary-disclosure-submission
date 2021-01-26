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

package controllers

import javax.inject.{Inject, Singleton}
import models.ErrorModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.ImporterAddressService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton()
class ImporterAddressController @Inject()(cc: ControllerComponents, importAddressService: ImporterAddressService)
  extends BackendController(cc) {

  def onLoad(id: String): Action[AnyContent] = Action.async { implicit request =>
    importAddressService.retrieveAddress(id).map {
      case Right(traderAddress) => Ok(Json.obj(
        "streetAndNumber" -> traderAddress.streetAndNumber,
        "city" -> traderAddress.city,
        "postalCode" -> traderAddress.postalCode,
        "countryCode" -> traderAddress.countryCode
      ))
      case Left(_) => BadRequest("Could not retrieve address")
    }

  }

}
