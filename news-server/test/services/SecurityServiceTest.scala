package services

import org.scalatest._
import org.scalatest.Assertions._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import com.acadaca.fakenews.services.security.SecurityServiceImpl
import com.acadaca.fakenews.daos.security.SecurityDao
import scala.language.postfixOps
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import com.github.t3hnar.bcrypt._
import org.mockito.ArgumentMatchers
import com.acadaca.fakenews.daos.security.SecurityDao.UserData

class SecurityServiceTest extends FunSuite with MockitoSugar {

  import com.acadaca.fakenews.services.security.SecurityService._

  val secDao = mock[SecurityDao]
  val service = new SecurityServiceImpl(secDao)

  test("When create account success") {

    val username = "yfayman"
    val password = "123456"
    val email = "yfayman@gmail.com"

    when(secDao.create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
      .thenReturn(Future.successful((true, "success")))
    val serviceResponseFuture = service.createAccount(username, password, email)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(serviceResponse.success)
    assert(serviceResponse.message == "success")
  }

  test("When create account fails") {
    val username = "yfayman"
    val password = "123456"
    val email = "yfayman@gmail.com"

    when(secDao.create(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
      .thenReturn(Future.successful((false, "database error")))
    val serviceResponseFuture = service.createAccount(username, password, email)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(!serviceResponse.success)
    assert(serviceResponse.message == "database error")
  }

  test("Check email test - email taken") {
    val email = "yfayman@gmail.com"

    when(secDao.checkEmail(email)).thenReturn(Future.successful(Option(5)))
    val serviceResponseFuture = service.checkEmail(email)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(serviceResponse.emailTaken)
  }

  test("Check email test - email not taken") {
    val email = "yfayman@gmail.com"

    when(secDao.checkEmail(email)).thenReturn(Future.successful(None))
    val serviceResponseFuture = service.checkEmail(email)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(!serviceResponse.emailTaken)
  }

  test("Login success") {
    val email = "yfayman@gmail.com"
    val password = "password"
    val encryptedPw = password.bcrypt

    val userDataToReturn = UserData(1, email, "yfayman", encryptedPw, None, None)
    when(secDao.getAccountByEmail(email)).thenReturn(Future.successful(Option(userDataToReturn)))
    when(secDao.saveAuthToken(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong()))
      .thenReturn(Future.successful(true))

    val serviceResponseFuture = service.login(email, password)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(serviceResponse.success)
  }

  test("Login failure") {
    val email = "yfayman@gmail.com"
    val password = "password"
    val encryptedPw = password.bcrypt

    val userDataToReturn = UserData(1, email, "yfayman", encryptedPw, None, None)
    when(secDao.getAccountByEmail(email)).thenReturn(Future.successful(None))

    val serviceResponseFuture = service.login(email, password)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(!serviceResponse.success)
  }

  test("Get account info by token - expired account that exists") {

    val token = "abcde"
    val userDataToReturn = UserData(1, "yfayman@gmail.com", "yfayman", "235235235346", None, None)
    when(secDao.getAccountByAuthToken(token)).thenReturn(Future.successful(Option(userDataToReturn)))

    val serviceResponseFuture = service.getAccountInfoByToken(token)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(!serviceResponse.activeAuth)
    assert(serviceResponse.account.isEmpty)
  }

  test("Get account info by token - expired account that dne") {
    val token = "abcde"
    when(secDao.getAccountByAuthToken(token)).thenReturn(Future.successful(None))

    val serviceResponseFuture = service.getAccountInfoByToken(token)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(serviceResponse.account.isEmpty)
    assert(!serviceResponse.activeAuth)
  }

  test("Renew auth success") {
    val commonAccount = CommonAccount(5, "yfayman", "yfayman@gmail.com", None, None)
    val req = CommonRenewAuthAccountRequest(commonAccount)

    when(secDao.renewAuth(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong())).
      thenReturn(Future.successful(true))

    val serviceResponseFuture = service.renewAuth(req)
    val serviceResponse = Await.result(serviceResponseFuture, 2.seconds)

    assert(serviceResponse)
  }

}