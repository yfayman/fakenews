package com.acadaca.fakenews.task.actor

import akka.actor._
import com.acadaca.fakenews.task._
import com.acadaca.fakenews.services.article._
import com.acadaca.fakenews.services.article.scrapper._

object ArticleUpdateMasterActor {
  def props(implicit articleSerice: ArticleService, scrapper: ArticleScrapper) = Props(new ArticleUpdateMasterActor())

  case class Start(articleFinders: List[ArticleFinder])
}

class ArticleUpdateMasterActor(implicit articleService: ArticleService, scrapper: ArticleScrapper)
    extends Actor with ActorLogging {

  import ArticleUpdateMasterActor._
  import com.acadaca.fakenews.task.actor.ArticleUpdateWorkerActor._

  def receive = {
    case Start(finders) => {
      log.info("Received start command - ActorLogging")

      finders.foreach(finder => {
        val workerActor = context.actorOf(ArticleUpdateWorkerActor.props)
        workerActor ! FinderInit(finder)
      })
    }
    case _ => log.warning("Received unknown message")
  }
}