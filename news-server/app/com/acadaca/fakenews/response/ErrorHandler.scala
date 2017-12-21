package com.acadaca.fakenews.response

import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import scala.concurrent._

class ErrorHandler(env: Environment, config: Configuration) extends DefaultHttpErrorHandler(env, config) {

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(Ok(com.acadaca.fakenews.views.html.index()))
  }
}