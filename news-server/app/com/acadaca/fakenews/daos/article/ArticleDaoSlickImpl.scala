package com.acadaca.fakenews.daos.article

import scala.language.postfixOps
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import slick.driver.MySQLDriver.api._
import com.acadaca.fakenews.daos.article._
import com.acadaca.fakenews.daos.security._
import com.acadaca.fakenews.config.ConfiguredExecutionContexts
import com.acadaca.fakenews.services.article._
import scala.util.Try
import play.api.inject.ApplicationLifecycle
import com.acadaca.fakenews.tables.Tables
import com.acadaca.fakenews.services.article.ArticleService._

class ArticleDaoSlickImpl(lifecycle: ApplicationLifecycle) extends ArticleDao {
  import com.acadaca.fakenews.daos.article.ArticleDao._

  implicit val ec = ConfiguredExecutionContexts.databaseExecutionContext

  val articles = TableQuery[Tables.Article]
  val articleStati = TableQuery[Tables.ArticleStatus]
  val articleRatings = TableQuery[Tables.ArticleRating]
  val userArticleRatings = TableQuery[Tables.UserArticleRating]

  lazy val articlePendingStatusId: Int = {
    val futureId = getFutureArticleStatusIdByRefCode(ArticleStatusEnum.PENDING.toString())
    Await.result(futureId, 3 seconds)
  }

  lazy val articleActiveStatusId: Int = {
    val futureId = getFutureArticleStatusIdByRefCode(ArticleStatusEnum.ACTIVE.toString())
    Await.result(futureId, 3 seconds)
  }

  lazy val articleDeletedStatusId: Int = {
    val futureId = getFutureArticleStatusIdByRefCode(ArticleStatusEnum.DELETED.toString())
    Await.result(futureId, 3 seconds)
  }

  lazy val fakeRatingId: Int = {
    val futureId = getFutureArticleRatingIdByRefCode(ArticleRatingEnum.FAKE.toString())
    Await.result(futureId, 3 seconds)
  }

  lazy val realRatingId: Int = {
    val futureId = getFutureArticleRatingIdByRefCode(ArticleRatingEnum.REAL.toString())
    Await.result(futureId, 3 seconds)
  }

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  val db = Database.forConfig("mySqlDb")

  def getArticleById(id: Int): Future[Option[ArticleData]] = {
    val articleWithStatusAction = for {
      article <- articles if article.articleId === id
      articleStatus <- articleStati if article.articleStatusId === articleStatus.articleStatusId
    } yield (article.articleId,
      article.articleUrl,
      article.articleHtml,
      article.shortDescription,
      articleStatus.refCode,
      article.userId,
      article.title)

    val resultFuture = db.run(articleWithStatusAction.result.headOption)
    resultFuture.map(articleOption => articleOption.map(article => mapDbResponseToArticleData(article)))
  }

  def getArticleByUrl(url: String): Future[Option[ArticleData]] = {
    val articleWithStatusAction = for {
      article <- articles if article.articleUrl === url
      articleStatus <- articleStati if article.articleStatusId === articleStatus.articleStatusId
    } yield (article.articleId,
      article.articleUrl,
      article.articleHtml,
      article.shortDescription,
      articleStatus.refCode,
      article.userId,
      article.title)

    val resultFuture = db.run(articleWithStatusAction.result.headOption)

    resultFuture.map(articleOption => articleOption.map(article => mapDbResponseToArticleData(article)))
  }

  def createArticle(car: CommonCreateArticleRequest): Future[Try[Int]] = {
    val insertAction = articles.returning(articles.map { _.articleId }) +=
      Tables.ArticleRow(-1, car.url, car.html, articlePendingStatusId, car.userId, car.title, car.shortDescription)
    db.run(insertAction.asTry)
  }

  def getRecentArticles(request: CommonGetArticlesRequest): Future[List[ArticleData]] = {

    val htmlColumn = if (request.skipHtml) " \"\" as article_html " else "article_html "
    val queryCols = s" article_id, article_url, title, short_description, $htmlColumn"
    val queryFrom = s"FROM article "
    val queryWhere = s"WHERE article_status_id = $articleActiveStatusId"
    val query = sql"SELECT #$queryCols #$queryFrom #$queryWhere"

    val action = query.as[(Int, String, String, String, String)]
    val result = db.run(action)
    result.map(v => v.toList).map(li => li.map(tup => ArticleData(tup._1, tup._2, tup._3, tup._5, tup._4, "ACTIVE", None)))
  }

  def updateArticleStatus(request: CommonArticleUpdateStatusRequest): Future[Boolean] = {
    val updateStatus = for { a <- articles if a.articleId === request.articleId } yield a.articleStatusId
    val newStatusId = request.newStatus match {
      case ArticleStatusEnum.ACTIVE  => articleActiveStatusId
      case ArticleStatusEnum.DELETED => articleDeletedStatusId
      case _                         => articlePendingStatusId
    }
    val articleUpdateStatusAction = updateStatus.update(newStatusId)
    val tryFuture = db.run(articleUpdateStatusAction.asTry)
    tryFuture.map { qTry => qTry.isSuccess }
  }

  def rateArticle(request: CommonArticleRateRequest): Future[Try[Int]] = {
    val ratingId = request.rating match {
      case ArticleRatingEnum.FAKE => fakeRatingId
      case ArticleRatingEnum.REAL => realRatingId
    }
    val articleRateAction = userArticleRatings.returning(userArticleRatings.map { _.userArticleRatingId }) += Tables.UserArticleRatingRow(-1, request.userId, request.articleId, ratingId)
    db.run(articleRateAction.asTry)
  }

  def getArticleRatingDataForUser(userId: Int): Future[List[ArticleRatingData]] = {
    val query = userArticleRatings.filter { _.userId === userId }
    val actionResult = db.run(query.result)
    actionResult.map {
      seq =>
        seq.toList.map {
          uar => ArticleRatingData(uar.userArticleRatingId, uar.articleId, uar.userId, getArticleRatingById(uar.articleRatingId))
        }
    }
  }

  private def mapDbResponseToArticleData(tup: (Int, String, String, String, String, Option[Int], String)): ArticleData = {
    ArticleData(tup._1, tup._2, tup._7, tup._3, tup._4, tup._5, tup._6)
  }

  private def getFutureArticleStatusIdByRefCode(refCode: String): Future[Int] = {
    val idQuery = articleStati.filter { articleStatus => articleStatus.refCode === refCode }
      .map { articleStatus => articleStatus.articleStatusId }
    db.run(idQuery.result.head)
  }

  private def getFutureArticleRatingIdByRefCode(refCode: String): Future[Int] = {
    val idQuery = articleRatings.filter { articleRating => articleRating.refCode === refCode }
      .map { articleRating => articleRating.articleRatingId }
    db.run(idQuery.result.head)
  }

  private def getArticleRatingById(id: Int): ArticleRatingEnum.Value = {
    id match {
      case `fakeRatingId` => ArticleRatingEnum.FAKE
      case `realRatingId` => ArticleRatingEnum.REAL
    }
  }

  lifecycle.addStopHook { () => Future.successful(db.close()) }
}