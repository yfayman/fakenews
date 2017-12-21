package com.acadaca.fakenews.task.actor

import akka.actor._
import scala.util.{ Try, Success, Failure }
import com.acadaca.fakenews.services.article.scrapper.ArticleFinder
import com.acadaca.fakenews.utilities.AkkaExecutionContextAware

object ArticleFindActor {
  def props(finder: ArticleFinder) = Props(new ArticleFindActor(finder))

  case class SearchForArticles()
}

class ArticleFindActor(finder: ArticleFinder) extends Actor with ActorLogging with AkkaExecutionContextAware {

  import ArticleFindActor._
  import com.acadaca.fakenews.task.actor.ArticleUpdateWorkerActor._
  
  def receive = {
    case SearchForArticles() => {
      val articleUrlsFuture = finder.find()
      articleUrlsFuture.onComplete(_ match {
        case Success(urls)  => context.parent ! ReceiveUrls(urls)
        case Failure(error) => {}
      })
    }
     case _ => log.warning("Received unknown message")
  }

}