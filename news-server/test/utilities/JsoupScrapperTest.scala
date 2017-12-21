package utilities

import org.scalatest._
import org.scalatest.Assertions._
import com.acadaca.fakenews.services.article.scrapper._
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import scala.language.postfixOps

class JsoupScrapperTest extends FunSuite{
  
  val scrapper: ArticleScrapper = new JsoupScrapper
  
  test("Scrapper returns correct data 1"){
    val urlToQuery = "https://www.nytimes.com/2017/05/02/us/politics/health-care-paul-ryan-fred-upton-congress.html"
    val scrappedDataFuture = scrapper.getArticle(urlToQuery)
    
    val scrappedDataOption = Await.result(scrappedDataFuture, 10 seconds)
    assert(scrappedDataOption.isDefined)
    val scrappedData = scrappedDataOption.get
    
    assert(scrappedData.url == urlToQuery)
    assert(scrappedData.shortDescription.nonEmpty)
    
  }
  
  test("Scrapper returns None when data is wrong"){
    val urlToQuery = "http://www.cnn.com/cnnisfakenews.html"
    val scrappedDataFuture = scrapper.getArticle(urlToQuery)
    
    val scrappedDataOption = Await.result(scrappedDataFuture, 10 seconds)
    assert(scrappedDataOption.isEmpty)
  }
  
}