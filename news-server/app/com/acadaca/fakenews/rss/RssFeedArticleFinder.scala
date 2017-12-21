package com.acadaca.fakenews.rss

import scala.concurrent.Future
import com.acadaca.fakenews.config.ConfiguredExecutionContexts
import com.acadaca.fakenews.services.article.scrapper.ArticleFinder

/**
 * As the name suggests, this gets an RSS feed turns it into URLs
 */
class RssFeedArticleFinder(rssFetcher: RssFetcher, processor: RssProcessor) extends ArticleFinder {

  implicit val ec = ConfiguredExecutionContexts.akkaExecutionContext

  def find(): Future[List[String]] = {
    for {
      rootNode <- rssFetcher.getRootElement()
      urls <- processor.process(rootNode)
    } yield (urls)
  }

}