package com.acadaca.fakenews.serializers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import com.acadaca.fakenews.controllers._
import com.acadaca.fakenews.services.security._
import com.acadaca.fakenews.validation.ValidationHelper

/**
 * Authentication related reads/writes
 */
object SecurityJsonSerializer {

  import com.acadaca.fakenews.services.security.SecurityService._
  import com.acadaca.fakenews.controllers.SecurityController._

  implicit val accountWrites = new Writes[CommonAccount] {
    def writes(acc: CommonAccount): JsValue = Json.obj("userId" -> acc.userId, "username" -> acc.username, "email" -> acc.email, "token" -> acc.token)
  }

  implicit val accountReads = new Reads[CommonAccount] {
    def reads(json: JsValue): JsResult[CommonAccount] = {
      val userIdResult = (json \ "userId").validate[Int]
      val usernameResult = (json \ "username").validate[String]
      val emailResult = (json \ "email").validate[String]
      val tokenResult = (json \ "email").validate[String]
      val expOpt = (json \ "tokenExp").validate[Long].asOpt

      for {
        userId <- userIdResult
        username <- usernameResult
        email <- emailResult
        token <- tokenResult
      } yield (CommonAccount(userId, username, email, Option(token), expOpt))
    }
  }

  implicit val loginResponseWrites = new Writes[CommonLoginResponse] {
    def writes(lr: CommonLoginResponse): JsValue = Json.obj("accountInfo" -> Json.toJson(lr.account.getOrElse(null)))
  }

  implicit val getAccountInfoResponseReads = new Reads[CommonGetAccountInfoResponse] {
    def reads(json: JsValue): JsResult[CommonGetAccountInfoResponse] = {
      val accOptResult = (json \ "account").validateOpt[CommonAccount]
      val activeAuthResult = (json \ "activeAuth").validate[Boolean] // This doesn't seem to work. Will use Opt in the meantime
      val messageResult = (json \ "message").validate[String]

      for {
        accOpt <- accOptResult
        activeAuth <- activeAuthResult
        message <- messageResult
      } yield (CommonGetAccountInfoResponse(accOpt, activeAuth, message))
    }
  }

  implicit val getAccountInfoResponseWrites = new Writes[CommonGetAccountInfoResponse] {
    def writes(gai: CommonGetAccountInfoResponse): JsValue =
      Json.obj("account" -> gai.account, "activeAuth" -> gai.activeAuth, "message" -> gai.message)
  }

  implicit val loginReads: Reads[Login] = (
    (JsPath \ "email").read[String](email) and
    (JsPath \ "password").read[String])(Login.apply _)

  implicit val createAccountReads: Reads[CreateAccount] = (
    (JsPath \ "email").read[String](email) and
    (JsPath \ "username").read[String] and
    (JsPath \ "password").read[String])(CreateAccount.apply _)

  implicit val checkEmailRequestReads = new Reads[CheckEmailRequest] {
    def reads(json: JsValue): JsResult[CheckEmailRequest] = {
      val emailResult = json.\("email")
      emailResult.validate[String](email)
                  .map { email => CheckEmailRequest(email) }
    }
  }
  
  implicit val checkEmailResponseWrites = new Writes[CheckEmailResponse] {
    def writes(cer: CheckEmailResponse):JsValue =  Json.obj("emailTaken" -> cer.emailTaken)
  }
  
  implicit val checkEmailResponseReads = new Reads[CheckEmailResponse] {
    def reads(json:JsValue):JsResult[CheckEmailResponse] = {
      for {
        taken <- (json \ "emailTaken").validate[Boolean]
      }yield(CheckEmailResponse(taken))
    }
  }
}