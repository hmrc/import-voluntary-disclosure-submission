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

package controllers.actions

import com.google.inject.Inject
import models.auth.AuthorisedRequest
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc.Results.Unauthorized
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.externalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisationException, AuthorisedFunctions, Enrolment}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait AuthorisedAction extends ActionRefiner[Request, AuthorisedRequest]

class AuthAction @Inject() (override val authConnector: AuthConnector)(implicit val executionContext: ExecutionContext)
    extends AuthorisedAction
    with AuthorisedFunctions
    with Logging {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthorisedRequest[A]]] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    authorised(Enrolment("HMRC-CTS-ORG")).retrieve(externalId) {
      case Some(userId) =>
        val authRequest = AuthorisedRequest(request, userId)
        Future.successful(Right(authRequest))
      case _ =>
        logger.warn("Auth error - Unable to retrieve an external ID from auth")
        val unknownUserError = Json.obj("error" -> "Unable to retrieve an external ID from auth")
        Future.successful(Left(Unauthorized(unknownUserError)))
    } recover { case x: AuthorisationException =>
      logger.warn(s"Authorisation Exception ${x.reason}")
      Left(Unauthorized(Json.obj("error" -> x.reason)))
    }

  }
}
