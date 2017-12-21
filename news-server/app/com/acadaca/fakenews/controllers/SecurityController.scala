package com.acadaca.fakenews.controllers

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import com.acadaca.fakenews.services.security._
import com.acadaca.fakenews.utilities._
import be.objectify.deadbolt.scala._
import scala.language.reflectiveCalls

object SecurityController {
  case class Login(email: String, password: String)
  case class CreateAccount(email: String, username: String, password: String)
  case class CheckEmailRequest(email: String)
  case class CheckEmailResponse(emailTaken:Boolean)
}
class SecurityController(service: SecurityService, deadbolt: DeadboltActions) extends Controller with GlobalExecutionContextAware {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  import com.acadaca.fakenews.serializers.SecurityJsonSerializer._
  import SecurityController._

  def logout = Action.async {
    Future { Ok(Json.obj("status" -> "success")) }
  }

  def login = deadbolt.SubjectNotPresent()(BodyParsers.parse.json) { request =>

    val loginRequest = request.body.validate[Login]
    loginRequest.fold(
      errors => {
        logger.info("Login data failed to bind")
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      success => {
        logger.info(s"user ${success.email} has tried to log in")
        val loginResponse = service.login(success.email, success.password)
        loginResponse.map { loginResponse => if (loginResponse.success) Ok(Json.toJson(loginResponse)) else Unauthorized }
      })
  }

  def createAccount = deadbolt.SubjectNotPresent()(BodyParsers.parse.json) { request =>
    val createAccountRequest = request.body.validate[CreateAccount]
    createAccountRequest.fold(
      errors => {
        logger.info("Login data failed to bind")
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      success => {
        logger.info("Trying to create account")
        val createAccountResult = service.createAccount(success.username, success.password, success.email)
        createAccountResult.map {
          createAccountResponse =>
            if (createAccountResponse.success) {
              Ok(Json.obj("status" -> "success"))
            } else {
              BadRequest(Json.obj("status" -> "error", "message" -> createAccountResponse.message))
            }
        }
      });
  }

  def checkEmail = Action.async(BodyParsers.parse.json) { request =>
    val checkEmailRequest = request.body.validate[CheckEmailRequest]

    checkEmailRequest match {
      case s: JsSuccess[CheckEmailRequest] => {
        service.checkEmail(s.value.email).map { ccer =>
         Ok(Json.toJson(CheckEmailResponse(ccer.emailTaken)))
        }
      }
      case e: JsError => Future.successful(BadRequest(JsError.toJson(e)))
    }
  }

  def getAccountByToken(token: String) = Action.async {
    request =>
      {
        val user = service.getAccountInfoByToken(token)
        user.map { userInfo => Ok(Json.toJson(userInfo)) }
      }

  }

}