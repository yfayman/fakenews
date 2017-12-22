package com.acadaca.fakenews.task.actor

import akka.actor._
import com.acadaca.fakenews.task._
import com.acadaca.fakenews.services.article._
import com.acadaca.fakenews.services.article.scrapper._
import scala.util.{Failure,Success}
import com.acadaca.fakenews.utilities.AkkaExecutionContextAware
import com.acadaca.fakenews.utilities.AkkaExecutionContextAware

object ArticleSaveActor {
  def props(implicit articleService: ArticleService) = Props(new ArticleSaveActor(articleService))

  case class SaveScrappedData(scrappedData: ScrappedData)
}

class ArticleSaveActor(articleService: ArticleService) extends Actor with ActorLogging with AkkaExecutionContextAware {

  import ArticleSaveActor._
  import com.acadaca.fakenews.services.article.ArticleService._

  def receive = {
    case SaveScrappedData(sd) => {
      articleService.insertArticle(CommonCreateArticleRequest(sd.url, sd.title, sd.html, sd.shortDescription, None))
                     .onComplete({
                         case Success(createResponse) =>
                         case Failure(e) =>
                     })
    }
     
     case _ => log.warning("Received unknown message")
  }
}