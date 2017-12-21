package com.acadaca.fakenews.rss

import scala.xml.Elem
import scala.concurrent.Future

/**
 * Grabs an RSS feed from some source
 */
trait RssFetcher {
  
  def getRootElement():Future[Elem]
  
}