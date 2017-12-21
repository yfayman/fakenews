package com.acadaca.fakenews.services.article.scrapper

import scala.concurrent.Future
/**
 * This should return a list of URLs
 */
trait ArticleFinder {
  def find():Future[List[String]]
}