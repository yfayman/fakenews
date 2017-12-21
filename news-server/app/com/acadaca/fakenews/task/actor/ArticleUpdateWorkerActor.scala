package com.acadaca.fakenews.task.actor

import akka.actor._
import com.acadaca.fakenews.task._
import com.acadaca.fakenews.services.article._
import com.acadaca.fakenews.services.article.scrapper._

object ArticleUpdateWorkerActor {

  def props(implicit articleService: ArticleService, scrapper: ArticleScrapper) = Props(new ArticleUpdateWorkerActor(articleService, scrapper))

  case class FinderInit(finder: ArticleFinder)
  case class ReceiveUrls(urls: List[String])
  case class ReceiveScrappedData(scrappedData: ScrappedData)
}

class ArticleUpdateWorkerActor(articleService: ArticleService, scrapper: ArticleScrapper) extends Actor with ActorLogging {
  import ArticleUpdateWorkerActor._
  import com.acadaca.fakenews.task.actor.ArticleFindActor._
  import com.acadaca.fakenews.task.actor.ArticleScrapperActor._
  import com.acadaca.fakenews.task.actor.ArticleSaveActor._
  
  def receive = {
    case FinderInit(finder) => {
      val finderActor = context.actorOf(ArticleFindActor.props(finder))
      finderActor ! SearchForArticles()
    }
    case ReceiveUrls(urls) => {
      urls.foreach(url => {
        val scrapper = context.actorOf(ArticleScrapperActor.props(this.scrapper))
        scrapper ! ScrapeUrl(url)
      })
    }
    case ReceiveScrappedData(scraps) => {
      val saver = context.actorOf(ArticleSaveActor.props(this.articleService))
      saver ! SaveScrappedData(scraps)
    }
    case _ => log.warning("Received unknown message")
  }
}