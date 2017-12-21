package rss

import org.scalatest._
import org.scalatest.Assertions._
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import scala.language.postfixOps
import com.acadaca.fakenews.rss.WebRequestRssFetcher

class RssFetcherTest extends FunSuite {
  
  
  test("Cnn RSS test"){
    val cnnWebRequestFetcher = new WebRequestRssFetcher("http://rss.cnn.com/rss/cnn_topstories.rss")
    val rootElementFuture = cnnWebRequestFetcher.getRootElement()
    val rootElement = Await.result(rootElementFuture, 5.seconds)
   
    assert(!rootElement.isEmpty)
    assert(rootElement.text.nonEmpty)
  }
}