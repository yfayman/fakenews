package com.acadaca.fakenews.controllers

import play.api._
import play.api.mvc._
import controllers._
import com.acadaca.fakenews.task._

// Putting the task here so it fires up when the controllers load
class IndexController (startupTask: StartupTask) extends Controller {

  startupTask.run()

  def index = Action {
    Ok(com.acadaca.fakenews.views.html.index())
  }
}