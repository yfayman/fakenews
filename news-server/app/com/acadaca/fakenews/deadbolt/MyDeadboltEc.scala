package com.acadaca.fakenews.deadbolt

import be.objectify.deadbolt.scala.ExecutionContextProvider
import com.acadaca.fakenews.utilities.GlobalExecutionContextAware
import scala.concurrent.ExecutionContext
import org.slf4j.LoggerFactory

class MyDeadboltEc extends ExecutionContextProvider with GlobalExecutionContextAware {
  
  private val logger = LoggerFactory.getLogger(this.getClass)
  
  override def get(): ExecutionContext = {
    ec
  }
}