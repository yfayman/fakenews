package com.acadaca.fakenews.deadbolt
import be.objectify.deadbolt.scala.cache.CompositeCache
import be.objectify.deadbolt.scala.composite._
import play.api.mvc.AnyContent
import scala.concurrent.Future
import com.acadaca.fakenews.utilities.GlobalExecutionContextAware

/**
 * This does nothing.......hopefully
 */
class PassThroughCompositeCache extends CompositeCache with GlobalExecutionContextAware{
  
  def apply(str:String):Constraint[play.api.mvc.AnyContent] = {
    (a,b) => Future.successful(true)
  }
  
  override def register(name: String, constraint: Constraint[AnyContent]) = {
    
  }
}