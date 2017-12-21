package controllers

import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.mvc._
import play.api.test._
import com.acadaca.fakenews.task._
import com.acadaca.fakenews.controllers.SecurityController
import com.acadaca.fakenews.task._
import org.scalatest._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import com.acadaca.fakenews.services.security.SecurityService
import be.objectify.deadbolt.scala.DeadboltActions
import scala.concurrent.Future
import play.api.libs.json._

class SecurityControllerTest extends PlaySpec with Results with MockitoSugar {
  import com.acadaca.fakenews.services.security.SecurityService._
  import com.acadaca.fakenews.controllers.SecurityController._
  import com.acadaca.fakenews.serializers.SecurityJsonSerializer._

  val secService = mock[SecurityService]
  val dba = mock[DeadboltActions]
  val controller = new SecurityController(secService, dba)

  "SecurityController#getAccountByToken with bad token" should {

    // case class CommonGetAccountInfoResponse(account: Option[CommonAccount], activeAuth: Boolean, message: String)

    when(secService.getAccountInfoByToken("abc")).thenReturn(Future.successful(CommonGetAccountInfoResponse(None, false, "Token is bad")))

    val req = FakeRequest("GET", "/app/auth/getAcc/abc")
    val result = controller.getAccountByToken("abc").apply(req)
    val res = contentAsJson(result).as[CommonGetAccountInfoResponse]

    assert(!res.activeAuth)
    assert(res.account.isEmpty)
    assert(!res.message.isEmpty())
    assert(status(result) == play.api.http.Status.OK)
  }

  "SecurityController#getAccountByToken with good token" should {

    // case class CommonGetAccountInfoResponse(account: Option[CommonAccount], activeAuth: Boolean, message: String)
    val testAcc = CommonAccount(5, "yan", "yfayman@gmail.com", Option("23634634657ABSF"), Option(System.currentTimeMillis()))
    when(secService.getAccountInfoByToken("abc")).thenReturn(Future.successful(CommonGetAccountInfoResponse(Option(testAcc), true, "")))

    val req = FakeRequest("GET", "/app/auth/getAcc/abc")
    val result = controller.getAccountByToken("abc").apply(req)
    val res = contentAsJson(result).as[CommonGetAccountInfoResponse]
    assert(res.activeAuth)
    assert(res.account.isDefined)
    assert(res.message.isEmpty())
    assert(status(result) == play.api.http.Status.OK)
  }

  "SecurityController#checkEmail with bad req" should {

    val json = Json.obj("email." -> "yfayman@gmail.com")
    val request: Request[JsValue] = FakeRequest("POST", "/").withBody(json)
    val result: Future[Result] = controller.checkEmail.apply(request)
    assert(status(result) == play.api.http.Status.BAD_REQUEST)
  }

  "SecurityController#checkEmail with good req" should {

    when(secService.checkEmail("yfayman@gmail.com")).thenReturn(Future.successful(CommonCheckEmailResponse(true)))
    
    val json = Json.obj("email" -> "yfayman@gmail.com")
    val request: Request[JsValue] = FakeRequest("POST", "/").withBody(json)
    val result: Future[Result] = controller.checkEmail.apply(request)
    val jsonResponse = contentAsJson(result).validate[CheckEmailResponse]
    
    assert(status(result) == play.api.http.Status.OK)
  }
}