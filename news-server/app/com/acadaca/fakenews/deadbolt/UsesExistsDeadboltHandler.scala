package com.acadaca.fakenews.deadbolt
import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{ AuthenticatedRequest, DynamicResourceHandler, DeadboltHandler }
import play.api.mvc.{ Results, Result, Request }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.slf4j.LoggerFactory
import com.acadaca.fakenews.services.security._
import com.acadaca.fakenews.services.security.SecurityService._
import scala.util.{ Try, Success, Failure }

/**
 * This handler just cares if a user exists
 */
class UsesExistsDeadboltHandler(securityService: SecurityService) extends DeadboltHandler {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = {
    logger.info("Before auth check on UsesExistsDeadboltHandler")
    Future.successful(None)
  }

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = {
    logger.info("getDynamicResourceHandler on MyDeadboltHandler")
    Future.successful(None)
  }

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    logger.info("Getting subject")
   request.headers.get("authToken")
     match {
      case Some(token)=> {
             securityService.getAccountInfoByToken(token)
                             .map { accountInfoRes => accountInfoRes.account }
                             .andThen { 
                                case Success(accountOpt) =>  {
                                  accountOpt match {
                                    case Some(acc) if shouldRenew(acc) =>  securityService.renewAuth(CommonRenewAuthAccountRequest(acc))
                                    case  None => // Do nothing
                                  }
                                }
                              }
      }
      case None => Future.successful(None)
    }
  }
  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = {
    logger.info("Auth failure")
    Future.successful(Results.Forbidden)
  }

  // if the token expires in 20 minutes, renew it to 30 minutes
  private def shouldRenew (ca: CommonAccount): Boolean =
    ca.tokenExp match {
        case Some(exp) => exp - System.currentTimeMillis() > (20 * 60 * 1000)
        case None => false
    }

}