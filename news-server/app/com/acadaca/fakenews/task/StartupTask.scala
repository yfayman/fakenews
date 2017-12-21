package com.acadaca.fakenews.task

import javax.inject._;
/**
 * Just a marker interface
 */
trait StartupTask extends Task{
  
}

class StartupTaskArticleUpdateImpl (articleUpdateTask:ArticleUpdateTask) extends StartupTask{
  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  def run = {
    logger.info("Initializing startup tasks")
    articleUpdateTask.run()
  }
}