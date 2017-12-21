package com.acadaca.fakenews.services.security

import javax.inject._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.github.t3hnar.bcrypt._
import com.acadaca.fakenews.daos.security._
import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.models._

class SecurityServiceImpl (securityDao: SecurityDao) extends SecurityService {

  import SecurityService._
  
  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  implicit val ec = ExecutionContext.global

  private val tokenGenerator: TokenGenerator = new UUIDTokenGenerator();
  
  private val renewAuthInterval = 30 * 60 * 1000

  def createAccount(username: String, password: String, email: String): Future[CommonCreateAccountResponse] = {
    val result = securityDao.create(username, password.bcrypt, email)
    result.map(successAndMessageTuple => CommonCreateAccountResponse(successAndMessageTuple._1, successAndMessageTuple._2))
  }

  def checkEmail(email: String): Future[CommonCheckEmailResponse] = {
    securityDao.checkEmail(email).map { idOpt => CommonCheckEmailResponse(idOpt.isDefined) }
  }

  def login(email: String, password: String): Future[CommonLoginResponse] = {
    val accountResult = securityDao.getAccountByEmail(email)
    accountResult.flatMap {
      userOption =>
        userOption match {
          case Some(user) => {
            if (password.isBcrypted(user.password)) {
              val token = tokenGenerator.generateToken()
              val newExpiration = java.lang.System.currentTimeMillis() + +1800000
              val tokenSaveSuccess = securityDao.saveAuthToken(user.id, token, newExpiration) // 30 min

              tokenSaveSuccess.map { tss =>
                if (tss)
                  CommonLoginResponse(true, Some(CommonAccount(user.id, user.username, user.email, Option(token), Option(newExpiration))))
                else
                  CommonLoginResponse(false, None)
              }
            } else {
              Future.successful(CommonLoginResponse(false, None))
            }
          }
          case None => Future.successful(CommonLoginResponse(false, None))
        }
    }

  }

  def getAccountInfoByToken(token: String): Future[CommonGetAccountInfoResponse] = {
    val result = securityDao.getAccountByAuthToken(token);
    result.map { optUser =>
      optUser match {
        case Some(userData) =>{ 
          val expireTime = userData.authTokenExp.map { _.getTime }
          val commonAccount = CommonAccount(userData.id, userData.username, userData.email, userData.authToken, expireTime)
          userData.authTokenExp match {
          case Some(expirationDate) => {
            if (expirationDate.getTime > java.lang.System.currentTimeMillis()) {
              CommonGetAccountInfoResponse(Option(commonAccount), true, "token active")
            } else {
              CommonGetAccountInfoResponse(None, false, "token expired")
            }
          }
          case None => { CommonGetAccountInfoResponse(None, false, "token expired") }
        }}
        case None => { CommonGetAccountInfoResponse(None, false, "Account not found") }
      }
    }
  }

  def renewAuth(req: CommonRenewAuthAccountRequest):Future[Boolean] = securityDao.renewAuth(req.ca.userId, System.currentTimeMillis() + renewAuthInterval)

}
