package com.acadaca.fakenews.utilities

import com.acadaca.fakenews.config.ConfiguredExecutionContexts

trait AkkaExecutionContextAware {
  implicit val ec = ConfiguredExecutionContexts.akkaExecutionContext
}