package com.acadaca.fakenews.utilities

import scala.concurrent.ExecutionContext

trait GlobalExecutionContextAware {
  implicit val ec = ExecutionContext.global
}