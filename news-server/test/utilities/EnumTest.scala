package utilities


import org.scalatest._
import org.scalatest.Assertions._
import com.acadaca.fakenews.services.article.scrapper._
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import scala.language.postfixOps
import com.acadaca.fakenews.tables.Tables.ArticleRating
import com.acadaca.fakenews.services.article.ArticleRatingEnum

class EnumTest  extends FunSuite {
  
  test("Convert enum to string"){
    val rating  = ArticleRatingEnum.REAL
    assert(rating.toString() == "REAL")
  }
  
}