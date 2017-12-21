package com.acadaca.fakenews.daos.article

import scala.concurrent.Future
import com.acadaca.fakenews.services.article._
import com.acadaca.fakenews.services.article.ArticleService._
import scala.util.Try

object ArticleDao{
  case class ArticleData(id:Int, url:String, title:String, html: String, shortDescription:String, status:String, userId:Option[Int])
  case class ArticleRatingData(id: Int, articleId: Int, userId: Int, rating: ArticleRatingEnum.Value)
}

trait ArticleDao {
  
  import ArticleDao._
  
  def getArticleById(id:Int):Future[Option[ArticleData]]
  
  def getArticleByUrl(url:String):Future[Option[ArticleData]]
  
  def createArticle(createArticleRequest:CommonCreateArticleRequest):Future[Try[Int]]
  
  def getRecentArticles(request:CommonGetArticlesRequest):Future[List[ArticleData]]
  
  def updateArticleStatus(request:CommonArticleUpdateStatusRequest):Future[Boolean]
  
  def rateArticle(request:CommonArticleRateRequest):Future[Try[Int]]
  
  def getArticleRatingDataForUser(userId:Int):Future[List[ArticleRatingData]]
  
}

