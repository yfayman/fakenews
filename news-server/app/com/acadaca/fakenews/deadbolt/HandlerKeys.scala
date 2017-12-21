package com.acadaca.fakenews.deadbolt

import be.objectify.deadbolt.scala.HandlerKey

object HandlerKeys {

  //This key just checks to make sure there is a user
  val defaultKey = Key("defaultKey")
  
  //The user must own the article that the action is being performed on
  val userOwnsArticle = Key("userOwnsArticle")
  
  case class Key(name: String) extends HandlerKey

}