package com.acadaca.fakenews.config.loader

import play.api._
import play.api.i18n._
import play.api.inject._
import play.api.routing.Router
import router.Routes
import com.acadaca.fakenews.daos.article._
import com.acadaca.fakenews.services.article._
import com.acadaca.fakenews.services.article.scrapper._
import com.acadaca.fakenews.daos.security._
import com.acadaca.fakenews.services.security._
import akka.actor.{ ActorSystem }
import com.acadaca.fakenews.controllers._
import be.objectify.deadbolt.scala.DeadboltComponents
import be.objectify.deadbolt.scala.cache.{ DefaultPatternCache, CompositeCache, HandlerCache, PatternCache }
import play.core.DefaultWebCommands
import com.acadaca.fakenews.task._
import com.acadaca.fakenews.controllers.ArticleController
import play.api.cache.EhCacheComponents
import com.acadaca.fakenews.deadbolt._
import com.acadaca.fakenews.filters._
import scala.concurrent.ExecutionContext
import com.acadaca.fakenews.response.ErrorHandler
import play.filters.gzip.GzipFilter

class MyApplicationLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new MyComponents(context).application
  }
}

class MyComponents(context: ApplicationLoader.Context)
    extends BuiltInComponentsFromContext(context)
    with I18nComponents
    with DeadboltComponents
    with EhCacheComponents {

  implicit val ec = ExecutionContext.global

  //Deadbolt
  override lazy val defaultEcContextProvider = new MyDeadboltEc
  override lazy val patternCache: PatternCache = new DefaultPatternCache(defaultCacheApi)

  def compositeCache: CompositeCache = new PassThroughCompositeCache
  def handlers: HandlerCache = new MyHandlerCache(securityService, articleService)

  //Filters 
  lazy val responseTimeFilter = new ResponseTimeFilter()
  lazy val gzipFilter = new GzipFilter
  override lazy val httpFilters = Seq(responseTimeFilter, gzipFilter)

  override lazy val httpErrorHandler = new ErrorHandler(environment, configuration)

  //Akka
  lazy val system = ActorSystem("jobRunner")

  // DAOS
  lazy val articleDao = new ArticleDaoSlickImpl(applicationLifecycle)
  lazy val securityDao = new SecurityDaoSlickImpl(applicationLifecycle)

  // Scrapper 
  lazy val scrapper = new JsoupScrapper

  //Services
  lazy val articleService = new ArticleServiceImpl(articleDao, securityDao, scrapper)
  lazy val securityService = new SecurityServiceImpl(securityDao)

  //Tasks
  lazy val articleTask = new ArticleUpdateTask(system, List())(articleService, scrapper)
  lazy val startTask = new StartupTaskArticleUpdateImpl(articleTask)

  //Controllers 
  lazy val indexController = new IndexController(startTask)
  lazy val articleController = new ArticleController(articleService, deadboltActions, handlers)
  lazy val securtyController = new SecurityController(securityService, deadboltActions)

  lazy val router: Router = new Routes(httpErrorHandler, indexController, articleController, securtyController, assets)

  lazy val assets = new controllers.Assets(httpErrorHandler)

}