package com.acadaca.fakenews.rss

import scala.xml.Elem
import scala.concurrent.Future
import play.api.libs.ws._
import play.api.libs.ws.ahc.AhcWSClient
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.acadaca.fakenews.utilities.AkkaExecutionContextAware
import scala.xml.XML

class WebRequestRssFetcher(url: String) extends RssFetcher with AkkaExecutionContextAware {

  //TODO clean this up
  def getRootElement(): Future[Elem] = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    val wsClient = AhcWSClient()
    wsClient.url(url).get()
                 .map(_.body)
                 .map(bodyString => XML.loadString(bodyString))
                 .andThen { case _ => { wsClient.close(); system.terminate() } }
  }

}