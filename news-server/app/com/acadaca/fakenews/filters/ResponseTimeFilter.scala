package com.acadaca.fakenews.filters

import play.api.mvc._
import scala.concurrent.{ ExecutionContext, Future }
import akka.stream.Materializer

class ResponseTimeFilter (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {
  
  private val logger = org.slf4j.LoggerFactory.getLogger("PERFORMANCE")
  
  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime
      logger.info(s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")
      
      result
    }

  }
}