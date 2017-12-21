package services

import org.scalatest._
import org.scalatest.Assertions._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import com.acadaca.fakenews.services.article.ArticleServiceImpl
import com.acadaca.fakenews.daos.article.ArticleDao
import com.acadaca.fakenews.daos.security.SecurityDao
import com.acadaca.fakenews.services.article.scrapper.ArticleScrapper
import scala.language.postfixOps
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import com.acadaca.fakenews.daos.article._
import com.acadaca.fakenews.services.article.ArticleStatusEnum
import scala.util.{ Success, Failure }
import com.acadaca.fakenews.services.article.scrapper.ScrappedData
import com.acadaca.fakenews.services.article.ArticleRatingEnum

class ArticleServiceTest extends FunSuite with MockitoSugar {

  import com.acadaca.fakenews.services.article.ArticleService._
  import com.acadaca.fakenews.daos.article.ArticleDao._

  val articleDaoMock = mock[ArticleDao]
  val securityDaoMock = mock[SecurityDao]
  val scrapperMock = mock[ArticleScrapper]
  val service = new ArticleServiceImpl(articleDaoMock, securityDaoMock, scrapperMock)

  test("When get article by id, article exists") {
    val articleId = 5
    val inputArticle = ArticleData(articleId, "http://www.cnn.com/abc", "Title Test", "<h1>abc</h1>",
      "test short description", "ACTIVE", None)

    when(articleDaoMock.getArticleById(articleId))
      .thenReturn(Future.successful(Some(inputArticle)))

    val serviceResponse = service.getArticleById(articleId)
    val articleOption = Await.result(serviceResponse, 2.seconds)
    assert(articleOption.isDefined)
    val outputCommonArticle = articleOption.get
    assert(areEquivalent(inputArticle, outputCommonArticle))

  }

  test("When get article by id, article dne") {
    val articleId = 3
    when(articleDaoMock.getArticleById(articleId)).thenReturn(Future.successful(None))

    val serviceResponse = service.getArticleById(articleId)
    val articleOption = Await.result(serviceResponse, 2.seconds)
    assert(articleOption.isEmpty)

  }

  test("When get article by url, article exists") {
    val inputArticleUrl = "http://www.somearticle.com"
    val inputArticle = ArticleData(5, inputArticleUrl, "Some Article Title",
      "<h1>Some article html</h1><p>Junk</p>", "test short description", "ACTIVE", None)

    when(articleDaoMock.getArticleByUrl(inputArticleUrl)).thenReturn(Future.successful(Some(inputArticle)))

    val serviceResponse = service.getArticleByUrl(inputArticleUrl)
    val articleOption = Await.result(serviceResponse, 2.seconds)
    assert(articleOption.isDefined)
    val outputCommonArticle = articleOption.get

  }

  test("When get article url, article dne") {
    val inputArticleUrl = "http://www.somearticle.com"
    when(articleDaoMock.getArticleByUrl(inputArticleUrl)).thenReturn(Future.successful(None))

    val serviceResponse = service.getArticleByUrl(inputArticleUrl)
    val articleOption = Await.result(serviceResponse, 2.seconds)
    assert(articleOption.isEmpty)
  }

  test("Insert article success") {
    val request = CommonCreateArticleRequest("http://www.cnn.com/a", "A title", "<h2>title</h2>", "a description", None)
    val createdArticleId = 5

    when(articleDaoMock.createArticle(request)).thenReturn(Future.successful(Success(createdArticleId)))

    val serviceResponseFuture = service.insertArticle(request)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(serviceResponse.success)
    assert(serviceResponse.id.isDefined && serviceResponse.id.get == createdArticleId)
    assert(serviceResponse.error.isEmpty)
  }

  test("Insert article failure") {
    val request = CommonCreateArticleRequest("http://www.cnn.com/a", "A title", "<h2>title</h2>", "a description", None)
    val exception = new Exception

    when(articleDaoMock.createArticle(request)).thenReturn(Future.successful(Failure(exception)))

    val serviceResponseFuture = service.insertArticle(request)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(!serviceResponse.success)
    assert(serviceResponse.error.isDefined)
    assert(serviceResponse.id.isEmpty)
  }

  test("Create article for url") {
    val url = "http://www.foxnews.com"
    val articleId = 5

    val mockRetTitle = "Bill Clinton stories"
    val mockRetShortDesc = "Secrets of Bill revealed"
    val mockHtml = "<h4>section</h4>"
    val mockScrappedData = ScrappedData(url, mockRetTitle, mockRetShortDesc, mockHtml)
    val insertRequest = CommonCreateArticleRequest(url, mockRetTitle, mockHtml, mockRetShortDesc, None)
    when(scrapperMock.getArticle(url)).thenReturn(Future.successful(Option(mockScrappedData)))
    when(articleDaoMock.createArticle(insertRequest)).thenReturn(Future.successful(Success(articleId)))
    when(articleDaoMock.getArticleByUrl(url)).thenReturn(Future.successful(None))

    val serviceResponseFuture = service.createArticleForUrl(url, None)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)
    assert(serviceResponse.isDefined)
  }

  test("Update article status success") {
    val request = CommonArticleUpdateStatusRequest(5, ArticleStatusEnum.ACTIVE)

    when(articleDaoMock.updateArticleStatus(request)).thenReturn(Future.successful(true))

    val serviceReponseFuture = service.updateArticleStatus(request)
    val serviceResponse = Await.result(serviceReponseFuture, 2.seconds)
    assert(serviceResponse.success)

  }

  test("Get recent articles test with skip html") {
    val request = CommonGetArticlesRequest(true)
    val recentArticleList = List(
      ArticleData(1, "http://www.breaitbart.com", "Fake Title", "<h1>Fake Article</h1>", "What is fake?", "ACTIVE", None),
      ArticleData(2, "http://www.breaitbart.com/abc", "Fake Title 2", "<h1>Fake Article</h1>", "What is fake news?", "ACTIVE", None),
      ArticleData(3, "http://www.breaitbart.com/def", "Fake Title 3", "<h2>Fake Article</h2>", "Banon rules", "ACTIVE", None))

    when(articleDaoMock.getRecentArticles(request)).thenReturn(Future.successful(recentArticleList))

    val serviceResponseFuture = service.getRecentArticles(request)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(!serviceResponse.isEmpty)
    assert(serviceResponse.size == recentArticleList.size)

  }

  test("Rate article test") {
    val request = CommonArticleRateRequest(1, 2, ArticleRatingEnum.REAL)
    when(articleDaoMock.rateArticle(request)).thenReturn(Future.successful(Success(25)))

    val serviceResponseFuture = service.rateArticle(request)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)
    assert(serviceResponse.success)
  }

  test("Articles Rated by Users") {
    val userId = "5"
    val articleRatings = List(
      ArticleRatingData(1, 1, 5, ArticleRatingEnum.REAL),
      ArticleRatingData(2, 2, 5, ArticleRatingEnum.REAL),
      ArticleRatingData(3, 3, 5, ArticleRatingEnum.FAKE),
      ArticleRatingData(4, 4, 5, ArticleRatingEnum.FAKE))

    when(articleDaoMock.getArticleRatingDataForUser(userId.toInt)).thenReturn(Future.successful(articleRatings))
    val serviceResultFuture = service.getArticlesRatedByUser(userId)
    val serviceResult = Await.result(serviceResultFuture, 2.seconds)
    assert(serviceResult.size == articleRatings.size)
  }

  private def areEquivalent(articleData: ArticleData, commonArticle: CommonArticle): Boolean = {
    articleData.id == commonArticle.id &&
      articleData.url == commonArticle.url &&
      articleData.shortDescription == commonArticle.shortDescription &&
      articleData.title == commonArticle.title &&
      articleData.html == commonArticle.html
  }
}