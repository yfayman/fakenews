package com.acadaca.fakenews.deadbolt

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala._
import com.acadaca.fakenews.services.security.SecurityService
import com.acadaca.fakenews.services.article.ArticleService

class MyHandlerCache (securityService: SecurityService, articleService: ArticleService) extends HandlerCache {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  val defaultHandler: DeadboltHandler = new UsesExistsDeadboltHandler(securityService)
  val userOwnsArticleHandler:DeadboltHandler = new UserArticleOwnerDeadboltHandler(articleService, securityService)

  val handlers: Map[Any, DeadboltHandler] = 
    Map(HandlerKeys.defaultKey -> defaultHandler, HandlerKeys.userOwnsArticle -> userOwnsArticleHandler )

  // Get the default handler.
  override def apply(): DeadboltHandler = {
    defaultHandler
  }

  // Get a named handler
  override def apply(handlerKey: HandlerKey): DeadboltHandler = {
    handlers.getOrElse(handlerKey, defaultHandler)
  }
}