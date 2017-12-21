package com.acadaca.fakenews.task

import play.api.mvc._
import akka.actor._
import javax.inject._
import scala.concurrent.duration._
import com.acadaca.fakenews.utilities.GlobalExecutionContextAware
import com.acadaca.fakenews.services.article.scrapper._
import com.acadaca.fakenews.services.article._

class ArticleUpdateTask(system: ActorSystem, articleFinders: List[ArticleFinder])(implicit val articleService: ArticleService, scrapper: ArticleScrapper)
    extends Task with GlobalExecutionContextAware {

  import com.acadaca.fakenews.task.actor.ArticleUpdateMasterActor
  import com.acadaca.fakenews.task.actor.ArticleUpdateMasterActor._

  override def run = {
    val runningMan = system.actorOf(ArticleUpdateMasterActor.props, "Running-Man")
    system.scheduler.schedule(1.minutes, 15.minutes, runningMan, Start(articleFinders))
  }

}