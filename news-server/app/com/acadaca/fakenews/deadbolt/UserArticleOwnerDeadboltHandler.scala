package com.acadaca.fakenews.deadbolt

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{ AuthenticatedRequest, DynamicResourceHandler, DeadboltHandler }
import play.api.mvc.{ Results, Result, Request }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.slf4j.LoggerFactory
import com.acadaca.fakenews.services.security.SecurityService
import play.api.libs.json._
import play.mvc.BodyParser
import play.api.libs.json._
import com.acadaca.fakenews.services.article.ArticleService

/**
 * The user must have ownership of the article, which means the original submitter
 * of the article
 */
class UserArticleOwnerDeadboltHandler(articleService: ArticleService, securityService: SecurityService) extends UsesExistsDeadboltHandler(securityService) {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    logger.info("In UserArticleOwnerDeadboltHandler getSubject method")
    val json = Json.parse(request.body.toString())

    val articleIdOption = json.\("articleId").validate[Int].asOpt

    articleIdOption match {
      case Some(articleId) => {
        logger.info(s"Looking up article $articleId")
        for {
          articleOpt <- articleService.getArticleById(articleIdOption.get)
          subjectOpt <- super.getSubject(request)
        } yield for {
          article <- articleOpt
          subject <- subjectOpt if (article.userId.getOrElse(-999) == subject.identifier.toInt)
        } yield (subject)
      }
      case None => Future.successful(None)
    }
  }

}