package com.acadaca.fakenews.config

import java.util.concurrent.Executors
import concurrent.ExecutionContext

object ConfiguredExecutionContexts {
  private val databaseExecutorService = Executors.newFixedThreadPool(10)
  val databaseExecutionContext = ExecutionContext.fromExecutorService(databaseExecutorService)

  private val httpExecutorService = Executors.newFixedThreadPool(5)
  val httpExecutionContext = ExecutionContext.fromExecutorService(httpExecutorService)
  
  private val akkaExecutorService = Executors.newCachedThreadPool()
  val akkaExecutionContext = ExecutionContext.fromExecutorService(akkaExecutorService)
}