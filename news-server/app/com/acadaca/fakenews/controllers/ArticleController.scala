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
    service.getRecentArticles(CommonGetArticlesRequest(skipHtml.getOrElse(false)))
       .map { articles => articles.map {a => ArticleResponse(a.id,
                                          a.url, 
                                          a.title, 
                                          a.html,
                                          a.shortDescription, 
                                          a.status
                                         )}
        }.map { articleResponse => Ok(Json.toJson(articleResponse)) }
  }

  def process = deadbolt.SubjectPresent()(BodyParsers.parse.json) { implicit request =>
    val userId = request.subject.map { sub => sub.identifier.toInt }
    val articleCreateRequestJS = request.body.validate[ArticleCreateRequest]
    articleCreateRequestJS.fold(
      errors => {
        Future.successful(BadRequest(JsError.toJson(errors)))
      },
      success => {
        service.createArticleForUrl(success.url, userId).map { articleOpt =>
          articleOpt match {
            case Some(a) => Ok(Json.toJson(ArticleResponse(a.id, a.url, a.title, a.html, a.shortDescription, a.status)))
            case None    => ServiceUnavailable
          }
        }

      });
  }

  def show(id: String) = Action.async { authRequest =>
    val articleId = id.toInt;
    service.getArticleById(articleId).map { articleOpt =>
      articleOpt match {
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
      art.map { articleOpt =>
        articleOpt match {
          case Some(a) => Ok(Json.toJson(ArticleResponse(a.id, a.url, a.title, a.html, a.shortDescription, a.status)))
          case None    => NotFound
        }
      }
    }
  }

  def updateArticleStatus(id: String) = deadbolt.SubjectPresent(handlers(HandlerKeys.userOwnsArticle))(BodyParsers.parse.json)(implicit request => {
    request.body.validate[ArticleUpdateStatus].fold(
        error => {
          Future.successful(BadRequest(JsError.toJson(error)))
        },
        articleUpdateStatus => {
          service.updateArticleStatus(CommonArticleUpdateStatusRequest(id.toInt, articleUpdateStatus.status))
            .map { ausr => if (ausr.success) Ok else InternalServerError }
        })
  })

  def rateArticle() = deadbolt.SubjectPresent()(BodyParsers.parse.json) { request =>
    request.subject match {
      case Some(sub) => {
          request.body.validate[ArticleRatingRequest].fold(error => {
              Future { BadRequest(JsError.toJson(error)) }
            }, arr => {             
              service.rateArticle(CommonArticleRateRequest(arr.id, sub.identifier.toInt, arr.rating))
                .map { response => if (response.success) Ok else ServiceUnavailable }
            }
          )
      }
      case None => Future.successful(Unauthorized)
    }
    
    
  }

  def myRatedArticles() = deadbolt.SubjectPresent()() { request =>
    request.subject match {
      case Some(sub) => {
        val userId = sub.identifier
         logger.info(s"Getting ratings submitted by $userId")
          service.getArticlesRatedByUser(userId)
            .map { articleratings => Ok(Json.toJson(articleratings)) }
      }
      case None => Future.successful(Unauthorized)
    }
   
   
  }

}
 
