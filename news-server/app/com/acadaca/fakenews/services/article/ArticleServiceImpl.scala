package com.acadaca.fakenews.services.article

import com.acadaca.fakenews.daos.security._
import com.acadaca.fakenews.daos.article._
import com.acadaca.fakenews.services.article.scrapper._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import com.acadaca.fakenews.utilities._
import scala.util.{ Try, Success, Failure }
import com.acadaca.fakenews.services.article.ArticleService._

class ArticleServiceImpl(articleDao: ArticleDao, securityDao: SecurityDao, articleScrapper: ArticleScrapper)
    extends ArticleService with GlobalExecutionContextAware {

  import ArticleService._
  import com.acadaca.fakenews.daos.article.ArticleDao._
  
  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def getArticleById(id: Int): Future[Option[CommonArticle]] = {
    articleDao.getArticleById(id).map { articleDataOption => articleDataOption.map { ad => convertArticleDataToArticle(ad) } }
  }

  def getArticleByUrl(url: String): Future[Option[CommonArticle]] = {
    articleDao.getArticleByUrl(url).map { articleDataOption => articleDataOption.map { ad => convertArticleDataToArticle(ad) } }
  }

  def getHtmlByUrl(url: String): Future[Option[ScrappedData]] = {
    articleScrapper.getArticle(url)
  }

  def insertArticle(articleRequest: CommonCreateArticleRequest): Future[CommonCreateArticleResponse] = {
    articleDao.createArticle(articleRequest).map { articleIdTry =>
      articleIdTry match {
        case Success(id) => CommonCreateArticleResponse(Some(id), true, None)
        case Failure(error) => CommonCreateArticleResponse(None, false, Some(ArticleServiceError.DATABASE_ERROR))
      }
    }
  }

  def createArticleForUrl(url: String, userId: Option[Int]): Future[Option[CommonArticle]] = {
    getArticleByUrl(url).flatMap { articleOption =>
      articleOption match {
        case Some(commonArticle) => Future.successful(Some(commonArticle))
        case None => scrapAndInsert(url,userId)
      }
    }
  }

  def updateArticleStatus(request: CommonArticleUpdateStatusRequest): Future[CommonArticleUpdateStatusResponse] = {
    articleDao.updateArticleStatus(request)
              .map { success => CommonArticleUpdateStatusResponse(success) }
  }

  def getRecentArticles(request: CommonGetArticlesRequest): Future[List[CommonArticle]] = {
    articleDao.getRecentArticles(request)
              .map { articles => articles.map { articleData => convertArticleDataToArticle(articleData) }
    }
  }

  def rateArticle(request: CommonArticleRateRequest): Future[CommonArticleRateResponse] = {
    articleDao.rateArticle(request)
              .map { tr => CommonArticleRateResponse(tr.isSuccess) }
  }

  def getArticlesRatedByUser(userId: String): Future[List[CommonArticleRating]] =
    articleDao.getArticleRatingDataForUser(userId.toInt)
               .map { list => list.map { ard => CommonArticleRating(ard.articleId, ard.rating) } }

  def getArticleData(req: CommonArticleDataRequest): Future[List[CommonArticleData]] = {
    ???
  }

  private def convertArticleDataToArticle(ad: ArticleData): CommonArticle = {
    val status = ArticleStatusEnum.values.filter { as => as.toString() == ad.status }.head
    CommonArticle(ad.id,
                  ad.url, 
                  ad.title, 
                  ad.html, 
                  ad.shortDescription, 
                  ad.userId, 
                  status)
  }
  
  private def scrapAndInsert(url: String, userId: Option[Int]): Future[Option[CommonArticle]] = {
    val insertResult = 
        for {
        scrappedDataOpt <- getHtmlByUrl(url)
        result <- if (scrappedDataOpt.isDefined) {
          val scrappedData = scrappedDataOpt.get
          insertArticle(CommonCreateArticleRequest(url, scrappedDataOpt.get.title, scrappedData.html, scrappedData.shortDescription, userId))
        } else
          Future.successful(CommonCreateArticleResponse(None, false, Some(ArticleServiceError.BAD_DATA)))
      } yield (scrappedDataOpt, result)

      insertResult.map(scrapsAndResponse => {
        scrapsAndResponse match {
          case (Some(scrappedData), CommonCreateArticleResponse(Some(id), true, None)) =>
            Some(CommonArticle(id, 
                               scrappedData.url, 
                               scrappedData.title, 
                               scrappedData.html, 
                               scrappedData.shortDescription, 
                               userId, 
                               ArticleStatusEnum.PENDING))
          case _ => None
        }
      })
  }

}



