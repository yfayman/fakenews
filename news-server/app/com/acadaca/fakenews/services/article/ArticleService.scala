package com.acadaca.fakenews.services.article

import com.acadaca.fakenews.daos.security._
import com.acadaca.fakenews.daos.article._
import com.acadaca.fakenews.services.article.scrapper._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.acadaca.fakenews.utilities._
import scala.util.{ Try, Success, Failure }

object ArticleService {
 
  case class CommonArticle(id: Int, url: String, title: String, html: String, shortDescription: String, userId: Option[Int], status: ArticleStatusEnum.Value)
  case class CommonCreateArticleRequest(url: String, title: String, html: String, shortDescription: String, userId: Option[Int])
  case class CommonCreateArticleResponse(id: Option[Int], success: Boolean, error: Option[ArticleServiceError.Value])
  case class CommonGetArticlesRequest(skipHtml: Boolean)
  case class CommonArticleUpdateStatusRequest(articleId: Int, newStatus: ArticleStatusEnum.Value)
  case class CommonArticleUpdateStatusResponse(success: Boolean)
  case class CommonArticleRateRequest(articleId: Int, userId: Int, rating: ArticleRatingEnum.Value)
  case class CommonArticleRateResponse(success: Boolean)
  // Keep it simple for now
  case class CommonArticleRating(articleId: Int, rating: ArticleRatingEnum.Value)

  case class CommonArticleDataRequest(articleCount: Int, orderBy: String)
  case class CommonArticleData(articleId: Int, positiveRatings: Int, negativeRatings: Int)
}

 object ArticleServiceError extends Enumeration {
    type ArticleServiceError = Value
    val DATABASE_ERROR, BAD_DATA = Value
  }

  object ArticleStatusEnum extends Enumeration {
    type ArticleStatusEnum = Value
    val ACTIVE, PENDING, DELETED = Value
  }

  object ArticleRatingEnum extends Enumeration {
    type ArticleRatingEnum = Value
    val FAKE, REAL = Value
  }

trait ArticleService {

  import ArticleService._ 
  
  def getArticleById(id: Int): Future[Option[CommonArticle]] 
  
  def getArticleByUrl(url: String): Future[Option[CommonArticle]]
  
  def getHtmlByUrl(url: String): Future[Option[ScrappedData]]
  
  def insertArticle(articleRequest: CommonCreateArticleRequest): Future[CommonCreateArticleResponse]
  
  def createArticleForUrl(url: String, userId: Option[Int]): Future[Option[CommonArticle]]
  
  def updateArticleStatus(request: CommonArticleUpdateStatusRequest): Future[CommonArticleUpdateStatusResponse]
  
  def getRecentArticles(request: CommonGetArticlesRequest): Future[List[CommonArticle]]
  
  def rateArticle(request: CommonArticleRateRequest): Future[CommonArticleRateResponse]
  
  def getArticlesRatedByUser(userId: String): Future[List[CommonArticleRating]]
  
  def getArticleData(req: CommonArticleDataRequest): Future[List[CommonArticleData]]
}
