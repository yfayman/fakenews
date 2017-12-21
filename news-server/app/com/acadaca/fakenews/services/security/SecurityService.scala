package com.acadaca.fakenews.services.security

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.models._
import scala.concurrent.Future

object SecurityService {
  case class CommonCheckEmailResponse(emailTaken: Boolean)
  case class CommonCreateAccountResponse(success: Boolean, message: String)
  case class CommonLoginResponse(success: Boolean, account: Option[CommonAccount])
  case class CommonGetAccountInfoResponse(account: Option[CommonAccount], activeAuth: Boolean, message: String)
  case class CommonRenewAuthAccountRequest(ca: CommonAccount)
  case class CommonAccount(userId: Int, username: String, email: String, token: Option[String], tokenExp: Option[Long]) extends Subject {
    def identifier: String = userId.toString()

    def roles: List[Role] = List()

    def permissions: List[Permission] = List()
  }

}

trait SecurityService {

  import SecurityService._

  def createAccount(username: String, password: String, email: String): Future[CommonCreateAccountResponse]
  
  def checkEmail(email: String): Future[CommonCheckEmailResponse]

  def login(email: String, password: String): Future[CommonLoginResponse]

  def getAccountInfoByToken(token: String): Future[CommonGetAccountInfoResponse]

  def renewAuth(req: CommonRenewAuthAccountRequest): Future[Boolean]
}
