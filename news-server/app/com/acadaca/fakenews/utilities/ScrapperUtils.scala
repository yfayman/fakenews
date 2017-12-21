package com.acadaca.fakenews.utilities

import org.jsoup._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser._

object ScrapperUtils {

  private val illegalChars = List(',', '.', ':')

  /**
   * Takes an html string and returns a word count. Case sensitivity does not matter
   */
  def extractWords(html: String): Map[String, Int] = {

    val doc = JsoupDocument(Jsoup.parse(html))
    val text = doc.body.text.filter { ch => !illegalChars.contains(ch) }
    text.split("\\W+")
      .filter { !_.isEmpty() }
      .map(_.toLowerCase())
      .groupBy(str => str)
      .mapValues(_.length)
  }

  /**
   * Returns a set of unique strings. Case sensitivity does not matter
   */
  def uniqueWords(html: String): Set[String] = {
    extractWords(html).keySet
  }

}