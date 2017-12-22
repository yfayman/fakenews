package com.acadaca.fakenews.services.article.scrapper

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{ Try, Success, Failure }
import scala.language.postfixOps
import org.owasp.html._
import com.acadaca.fakenews.config.ConfiguredExecutionContexts

class JsoupScrapper extends ArticleScrapper {

  val jsoupBrowser = JsoupBrowser()

  implicit val ec = ConfiguredExecutionContexts.httpExecutionContext

  private val policy = Sanitizers.BLOCKS
  private val headerDescriptionCutoff = 40
  private val paragraphDescriptionCutoff = 70

  override def getArticle(url: String): Future[Option[ScrappedData]] = {

    getDocument(url).map(docOpt =>
      docOpt.map {
        doc =>
          val descOption = getShortDescriptionElement(doc.body).map { el => el.innerHtml }
          ScrappedData(url, doc.title, descOption.getOrElse(""), getScrappedData(doc))
      })
  }

  /**
   * attempts to get a document from a URL
   * @param url the url
   * @returns Document if it exists
   */
  private def getDocument(url: String): Future[Option[Document]] = {
    Future { Try { jsoupBrowser.get(url) } }
      .collect({
        case Success(doc) => Some(doc)
        case Failure(e) => None
      })
  }

  /**
   * Grabs the typical text elements from the body and transforms 
   * into a string
   * @param document
   * @returns A string reputation of the text in the document
   */
  private def getScrappedData(doc: Document): String = {
    val el = doc.body
    val ext = el >> elements("h1, h2, h3, h4, h5, p")
    ext.map { x => wrapTextInTag(x.tagName, x.innerHtml) }.mkString
  }
  /**
   * @param tag - the tag IE <h1> or <p>
   * @param text the text inside the tag
   * @returns <h1>text</h1>
   * This is used to get rid of attributes in the element and
   * also remove any unwanted items in the text
   */
  private def wrapTextInTag(tag: String, text: String): String = {
    val sanitizedText = policy.sanitize(text)
    s"<$tag>$sanitizedText</$tag>"
  }

  /**
   * Since Jsoup always returns a document, we qualify a document as
   * valid based on this method(currently just looking to see if there is a title
   */
  private def isEmpty(doc: Document): Boolean = {
    doc.title.isEmpty()
  }

  /**
   * Get a short description for the article. Right now this will look for 
   * h2 or p elements with a minimum text size
   */
  private def getShortDescriptionElement(rootEl: Element): Option[Element] = {
    val possibleDescriptions = rootEl >?> elementList("h2,p")
    possibleDescriptions.flatMap { pd =>
      pd.find(el =>
          (el.tagName == "h2" && el.text.length() > headerDescriptionCutoff) 
          || (el.tagName == "p" && el.text.length() > paragraphDescriptionCutoff)
       )
    }
  }
}