package com.acadaca.fakenews.rss

import scala.xml.Elem
import scala.concurrent.Future

/**
 * Processes an RSS feed and returns a list of URLs
 */
trait RssProcessor {
  
  def process(rootElement:Elem):Future[List[String]]

}