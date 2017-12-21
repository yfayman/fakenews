package com.acadaca.fakenews.task.actor

import akka.actor._
import com.acadaca.fakenews.task._
import com.acadaca.fakenews.services.article._
import com.acadaca.fakenews.services.article.scrapper._
import scala.util.{Success,Failure}
import com.acadaca.fakenews.utilities.AkkaExecutionContextAware
import com.acadaca.fakenews.task.actor.ArticleUpdateWorkerActor.ReceiveScrappedData
import com.acadaca.fakenews.utilities.AkkaExecutionContextAware

object ArticleScrapperActor{
  
  def props(implicit scrapper:ArticleScrapper) = Props(new ArticleScrapperActor(scrapper))
  
  case class ScrapeUrl(url:String)
  
}

class ArticleScrapperActor(scrapper:ArticleScrapper) extends Actor with ActorLogging with AkkaExecutionContextAware{
  
  import ArticleScrapperActor._
  
  def receive = {
    case ScrapeUrl(url) => {
      scrapper.getArticle(url).onComplete( _ match {
        case Success(opt) => if(opt.isDefined) context.parent ! ReceiveScrappedData(opt.get)
        case Failure(e) =>
      })
    }
    case _ => log.warning("Received unknown message")
  }
}