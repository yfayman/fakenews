package com.acadaca.fakenews.services.article.scrapper

import scala.concurrent.Future

trait ArticleScrapper {
  
  def getArticle(url:String):Future[Option[ScrappedData]]
  
}

case class ScrappedData(url:String, title:String, shortDescription:String, html:String)