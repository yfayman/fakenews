package controllers

import scala.concurrent.Future
import org.scalatest._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play._
import org.mockito.Mockito._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import com.acadaca.fakenews.services.article._
import be.objectify.deadbolt.scala.cache.HandlerCache
import com.acadaca.fakenews.deadbolt._
import be.objectify.deadbolt.scala._
import com.acadaca.fakenews.controllers.ArticleController
import com.acadaca.fakenews.services.article.ArticleService._
import com.acadaca.fakenews.controllers._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object ArticleControllerTest extends MockitoSugar {
  val articleService = mock[ArticleService]
  val dba = mock[DeadboltActions]
  val handlers = mock[HandlerCache]
  val controller = new ArticleController(articleService, dba, handlers)
}

class ArticleControllerTest extends PlaySpec with Results with MockitoSugar {

  import com.acadaca.fakenews.serializers.ArticleJsonSerializer._
  import ArticleControllerTest._
  import com.acadaca.fakenews.controllers.ArticleController._

  "ArticleController#index" should {
    val articleToReturnOne = CommonArticle(1, "url1", "title", "<h1>html1</h1>", "short descr", None, ArticleStatusEnum.ACTIVE)
    val articleToReturnTwo = CommonArticle(2, "url2", "title", "<h1>html1</h1>", "short descr2", None, ArticleStatusEnum.ACTIVE)

    when(articleService.getRecentArticles(CommonGetArticlesRequest(false))).thenReturn(Future.successful(List(articleToReturnOne)))

    val result = controller.index.apply(FakeRequest())
    val jsonResult = contentAsJson(result).as[JsArray]
    assert(status(result) == play.api.http.Status.OK)
    assert(jsonResult.value.nonEmpty)

    jsonResult.value.foreach { jsVal =>
      {
        val articleIdOpt = (jsVal \ "articleId").asOpt[Int]
        assert(articleIdOpt.isDefined)
        val urlOpt = (jsVal \ "url").asOpt[String]
        assert(urlOpt.isDefined)
        val htmlOpt = (jsVal \ "html").asOpt[String]
        assert(htmlOpt.isDefined)
        val shortDescriptionOpt = (jsVal \ "shortDescription").asOpt[String]
        assert(shortDescriptionOpt.isDefined)
        val titleOpt = (jsVal \ "title").asOpt[String]
        assert(titleOpt.isDefined)
        val statusOpt = (jsVal \ "status").asOpt[ArticleStatusEnum.Value]
        assert(statusOpt.isDefined)
        assert(statusOpt.get == ArticleStatusEnum.ACTIVE)
      }
    }

  }

}