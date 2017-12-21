package utilities

import org.scalatest._
import org.scalatest.Assertions._
import com.acadaca.fakenews.services.article.scrapper._
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import scala.language.postfixOps
import com.acadaca.fakenews.utilities.ScrapperUtils

class ScrapperUtilsTest extends FunSuite {

  test("Clean test simple") {
    val rawHtml = "<h1>text to clean</h1>"
    val wordsMap = ScrapperUtils.extractWords(rawHtml)
    assert(wordsMap.size == 3)
    assert(wordsMap("text") == 1)
    assert(wordsMap("clean") == 1)

  }

  test("Clean test simple with illegal chars") {
    val rawHtml = "<h1>text to clean.</h1>"
    val wordsMap = ScrapperUtils.extractWords(rawHtml)
    val wordsSet = ScrapperUtils.uniqueWords(rawHtml)
    assert(wordsMap.size == 3)
    assert(wordsSet.size == 3)
    assert(wordsSet == Set("text","to","clean"))
    assert(wordsMap("text") == 1)
    assert(wordsMap("clean") == 1)
   // assertThrows[NoSuchElementException] { wordsMap("clean.") }
  }

  test("Clean test nested") {
    val rawHtml = "<h1>text to <a href=\"http://www.google.com\" >clean</a></h1>"
    val wordsMap = ScrapperUtils.extractWords(rawHtml)
    val wordsSet = ScrapperUtils.uniqueWords(rawHtml)
    assert(wordsMap.size == 3)
    assert(wordsSet.size == 3)
    assert(wordsSet == Set("text","to","clean"))
    assert(wordsMap("text") == 1)
    assert(wordsMap("clean") == 1)
  }

  test("Multiple nested text") {
    val rawHtml = "<h1>Text to <a href=\"http://www.google.com\" >clean.</a></h1><p>Clean text is important</p>"
    val wordsMap = ScrapperUtils.extractWords(rawHtml)
    val wordsSet = ScrapperUtils.uniqueWords(rawHtml)
    assert(wordsMap.size == 5)
    assert(wordsSet.size == 5)
    assert(wordsSet == Set("text","to","clean", "is", "important"))
    assert(wordsMap("text") == 2)
 //   assertThrows[NoSuchElementException] { wordsMap("Text") }
    assert(wordsMap("clean") == 2)
  }

  test("Triple nested") {
    val rawHtml = "<div><h1>Text to <a href=\"http://www.google.com\" >clean.</a></h1><p>Clean text is important</p></div>"
    val wordsMap = ScrapperUtils.extractWords(rawHtml)
    val wordsSet = ScrapperUtils.uniqueWords(rawHtml)
    assert(wordsMap.size == 5)
    assert(wordsSet.size == 5)
    assert(wordsSet == Set("text","to","clean", "is", "important"))
    assert(wordsMap("text") == 2)
 //   assertThrows[NoSuchElementException] { wordsMap("Text") }
    assert(wordsMap("clean") == 2)
  }

  test("Empty text test") {
    val rawHtml = ""
    val wordsMap = ScrapperUtils.extractWords(rawHtml)
    val wordsSet = ScrapperUtils.uniqueWords(rawHtml)
    assert(wordsMap.size == 0)
    assert(wordsSet.size == 0)
    assert(wordsSet == Set())
  }

}