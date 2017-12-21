package com.acadaca.fakenews.serializers

import play.api.libs.json._
import com.acadaca.fakenews.controllers._
import com.acadaca.fakenews.services.article._
import com.acadaca.fakenews.validation.ValidationHelper
import com.acadaca.fakenews.services.article.ArticleService._

/**
 * Article related serializers/deserializers
 */
object ArticleJsonSerializer {

  import com.acadaca.fakenews.controllers.ArticleController._

  implicit val articleResponseWrites = new Writes[ArticleResponse] {
    def writes(ar: ArticleResponse): JsValue =
      Json.obj("articleId" -> ar.id,
        "url" -> ar.url,
        "html" -> ar.html,
        "shortDescription" -> ar.shortDescription,
        "title" -> ar.title,
        "status" -> ar.status)
  }

  implicit val commonArticleRatingWrites = new Writes[CommonArticleRating] {
    def writes(car: CommonArticleRating): JsValue =
      Json.obj("articleId" -> car.articleId,
        "rating" -> car.rating.toString())
  }

  implicit val myArticleRatingsResponseWrites = new Writes[MyArticleRatingsResponse] {
    def writes(res: MyArticleRatingsResponse): JsValue =
      Json.obj("ratings" -> res.ratings)
  }

  implicit val articleCreateRequestWrites: Writes[ArticleCreateRequest] = new Writes[ArticleCreateRequest]() {
    def writes(acr: ArticleCreateRequest): JsValue = {
      Json.obj("url" -> acr.url)
    }
  }

  implicit val articleUpdateStatusReads: Reads[ArticleUpdateStatus] = new Reads[ArticleUpdateStatus] {
    def reads(json: JsValue): JsResult[ArticleUpdateStatus] = {
      val idResult = json.\("articleId").validate[Int]
      val statusResult = json.\("status").validate[ArticleStatusEnum.Value]
      for {
        id <- idResult
        status <- statusResult
      } yield (ArticleUpdateStatus(id, status))
    }
  }

  implicit val articleStatusReads: Reads[ArticleStatusEnum.Value] = new Reads[ArticleStatusEnum.Value] {
    def reads(json: JsValue): JsResult[ArticleStatusEnum.Value] = {

      def parse(str: String): Option[ArticleStatusEnum.Value] = {
        str.toUpperCase() match {
          case "ACTIVE"  => Some(ArticleStatusEnum.ACTIVE)
          case "PENDING" => Some(ArticleStatusEnum.PENDING)
          case "DELETED" => Some(ArticleStatusEnum.DELETED)
          case _         => None
        }
      }

      json.result.validate[String].map { s => parse(s) }.flatMap { opt =>
        {
          if (opt.isDefined) JsSuccess(opt.get) else JsError("No a valid status")
        }
      }
    }
  }

  implicit val articleRatingReads: Reads[ArticleRatingEnum.Value] = new Reads[ArticleRatingEnum.Value] {
    def reads(json: JsValue): JsResult[ArticleRatingEnum.Value] = {

      def parse(str: String): Option[ArticleRatingEnum.Value] = {
        str.toUpperCase() match {
          case "REAL" => Some(ArticleRatingEnum.REAL)
          case "FAKE" => Some(ArticleRatingEnum.FAKE)
          case _      => None
        }
      }

      json.result.validate[String].map { s => parse(s) }.flatMap { opt =>
        {
          if (opt.isDefined) JsSuccess(opt.get) else JsError("No a valid rating")
        }
      }
    }
  }

  implicit val articleCreateRequestReads: Reads[ArticleCreateRequest] = new Reads[ArticleCreateRequest]() {
    def reads(json: JsValue): JsResult[ArticleCreateRequest] = {
      val urlString = json.\("url").as[String]
      if (ValidationHelper.isUrl(urlString))
        JsSuccess(ArticleCreateRequest(urlString))
      else
        JsError("not a URL")
    }
  }

  implicit val articleRatingRequestReads: Reads[ArticleRatingRequest] = new Reads[ArticleRatingRequest]() {

    def reads(json: JsValue): JsResult[ArticleRatingRequest] = {
      val articleIdResult = json.\("articleId").validate[Int].filter(JsError("id cannot be negative"))(_ > 0)
      val ratingResult = json.\("rating").validate[ArticleRatingEnum.Value]
      val articleAndRatingResult = for {
        articleId <- articleIdResult
        rating <- ratingResult
      } yield (articleId, rating)
      articleAndRatingResult.map(tup => ArticleRatingRequest(tup._1, tup._2))
    }
  }
}