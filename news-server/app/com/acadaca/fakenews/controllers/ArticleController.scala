package com.acadaca.fakenews.controllers

import play.api.libs.json.Json
import play.api.mvc._
import com.acadaca.fakenews.services.article._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Singleton
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import com.acadaca.fakenews.validation.ValidationHelper
import com.acadaca.fakenews.utilities._
import com.acadaca.fakenews.services.article.scrapper._
import com.acadaca.fakenews.services.article._
import be.objectify.deadbolt.scala._
import scala.language.reflectiveCalls
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.acadaca.fakenews.deadbolt._
import com.acadaca.fakenews.services.article.ArticleService._

object ArticleController {
  case class ArticleCreateRequest(url: String)
  case class ArticleResponse(id: Int, url: String, title: String, html: String, shortDescription: String, status: ArticleStatusEnum.Value)
  case class ArticleUpdateStatus(id: Int, status: ArticleStatusEnum.Value)
  case class ArticleRatingRequest(id: Int, rating: ArticleRatingEnum.Value)
  case class MyArticleRatingsResponse(ratings: List[CommonArticleRating])

}

/**
 * Takes HTTP requests and produces JSON.
 */
class ArticleController(service: ArticleService, deadbolt: DeadboltActions, handlers: HandlerCache)
    extends Controller with GlobalExecutionContextAware {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  import com.acadaca.fakenews.serializers.ArticleJsonSerializer._
  import ArticleController._

  def index = Action.async { request =>
    val skipHtml = request.getQueryString("skipHtml").map { str => str == "true" }
    val fut = service.getRecentArticles(CommonGetArticlesRequest(skipHtml.getOrElse(false))).map { list =>
      list.map {
        a => ArticleResponse(a.id, a.url, a.title, a.html, a.shortDescription, a.status)
      }
    };
    fut.map { ar => Ok(Json.toJson(ar)) }
  }

  def process = deadbolt.SubjectPresent()(BodyParsers.parse.json) { implicit request =>
    val userId = request.subject.map { sub => sub.identifier.toInt }
    val articleCreateRequestJS = request.body.validate[ArticleCreateRequest]
    articleCreateRequestJS.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      success => {
        service.createArticleForUrl(success.url, userId).map { artOpt =>
          artOpt match {
            case Some(a) => Ok(Json.toJson(ArticleResponse(a.id, a.url, a.title, a.html, a.shortDescription, a.status)))
            case None    => ServiceUnavailable
          }
        }

      });
  }

  def show(id: String) = Action.async { authRequest =>
    val i = id.toInt;
    service.getArticleById(i).map { opt =>
      opt match {
        case Some(a) => Ok(Json.toJson(ArticleResponse(a.id, a.url, a.title, a.html, a.shortDescription, a.status)))
        case None    => NotFound

      }
    }
  }

  def articleByUrl(url: String) = Action.async { implicit request =>
    if (!ValidationHelper.isUrl(url)) {
      Future.successful(BadRequest)
    } else {
      val art = service.getArticleByUrl(url)
      art.map { opt =>
        opt match {
          case Some(a) => Ok(Json.toJson(ArticleResponse(a.id, a.url, a.title, a.html, a.shortDescription, a.status)))
          case None    => NotFound
        }
      }
    }
  }

  def updateArticleStatus(id: String) = deadbolt.SubjectPresent(handlers(HandlerKeys.userOwnsArticle))(BodyParsers.parse.json)(implicit request => {
    val validationResult = request.body.validate[ArticleUpdateStatus]
    validationResult.fold(
      error => {
        Future.successful(BadRequest(JsError.toJson(error)))
      },
      articleUpdateStatus => {
        service.updateArticleStatus(CommonArticleUpdateStatusRequest(id.toInt, articleUpdateStatus.status))
          .map { ausr => if (ausr.success) Ok else InternalServerError }
      })
  })

  def rateArticle() = deadbolt.SubjectPresent()(BodyParsers.parse.json) { request =>
    val jsVal = request.body.validate[ArticleRatingRequest]
    jsVal.fold(error => {
      Future { BadRequest(JsError.toJson(error)) }
    }, arr => {
      val userId = request.subject.map { s => s.identifier.toInt }.get
      service.rateArticle(CommonArticleRateRequest(arr.id, userId, arr.rating))
        .map { response => if (response.success) Ok else ServiceUnavailable }
    })
  }

  def myRatedArticles() = deadbolt.SubjectPresent()() { request =>
    val userId = request.subject.get.identifier
    logger.info(s"Getting ratings submitted by $userId")
    val result = service.getArticlesRatedByUser(userId)
    result.map { articleratings => Ok(Json.toJson(articleratings)) }
  }

}
 
