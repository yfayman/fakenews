package com.acadaca.fakenews.daos.security

import slick.driver.MySQLDriver.api._
import play.api.inject.ApplicationLifecycle
import scala.concurrent.{ Future, Await }
import scala.concurrent.duration._
import scala.util.{ Success, Failure }
import slick.jdbc.GetResult
import scala.concurrent.ExecutionContext
import java.sql.Timestamp
import com.google.inject.ImplementedBy
import com.acadaca.fakenews.daos.security._
import scala.util.Try
import com.acadaca.fakenews.config.ConfiguredExecutionContexts
import scala.language.postfixOps
import com.acadaca.fakenews.tables.Tables

class SecurityDaoSlickImpl(lifecycle: ApplicationLifecycle) extends SecurityDao {

  import SecurityDao._

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  val db = Database.forConfig("mySqlDb")
  val users = TableQuery[Tables.User]
  val userTypes = TableQuery[Tables.UserType]

  lazy val userUserTypeId: Int = {
    val futureUserId = getFutureUserTypeIdByRefCode("USER")
    Await.result(futureUserId, 3 seconds)
  }

  lazy val userAdminTypeId: Int = {
    val futureUserId = getFutureUserTypeIdByRefCode("ADMIN")
    Await.result(futureUserId, 3 seconds)
  }

  implicit val ec = ConfiguredExecutionContexts.databaseExecutionContext
  implicit val getUserResult = GetResult(r =>
    UserData(r.nextInt, r.nextString, r.nextString, r.nextString, r.nextStringOption, r.nextTimestampOption()))

  def getPasswordByEmail(email: String): Future[Option[String]] = {
    val passwordQuery = users.filter { _.email === email }.map { _.password }
    db.run(passwordQuery.result.headOption)
  }

  def create(username: String, password: String, email: String): Future[(Boolean, String)] = {

    getUserTypeIdByRefCode("USER").flatMap { userTypeIdOption =>
      userTypeIdOption match {
        case Some(userTypeId) => {
          createNewUser(email, username, password)
        }
        case None => {
          Future.successful { (false, "database error") }
        }

      }
    }

  }

  def checkEmail(email: String): Future[Option[Int]] = {
    val query = users.filter { u => u.email === email }.map { u => u.userId }
    db.run(query.result.headOption)
  }

  def getAccountByEmail(email: String): Future[Option[UserData]] = {
    val result = db.run(users.filter { _.email === email }.result.headOption)
    result.map(userOption => userOption.map(user => convertTableUserToUser(user)))
  }

  def saveAuthToken(id: Int, authToken: String, newExpiration: Long): Future[Boolean] = {
    val tokenInfoQuery = for { user <- users if user.userId === id } yield (user.authToken, user.authTokenExp)
    val thirtyMinFromNow = new Timestamp(newExpiration)
    val saveTokenAction = tokenInfoQuery.update((Some(authToken), Some(thirtyMinFromNow)))
    val saveTokenResult = db.run(saveTokenAction.asTry)
    saveTokenResult.map { tryId => tryId.isSuccess }
  }

  def getAccountByAuthToken(token: String): Future[Option[UserData]] = {
    val query = users.filter { user => user.authToken === token }
    val result = db.run(query.result.headOption)
    result.map(accountOption => accountOption.map(user => convertTableUserToUser(user)))
  }

  def getAccountById(id: Int): Future[Option[UserData]] = {
    val query = users.filter { _.userId === id }
    val accountByIdResult = db.run(query.result.headOption)
    accountByIdResult.map(userOption => userOption.map(tableUser => convertTableUserToUser(tableUser)))
  }

  def renewAuth(userId: Int, newExpiration: Long): Future[Boolean] = {
    val tokenExpQuery = for { user <- users if user.userId === userId } yield (user.authTokenExp)
    val action = tokenExpQuery.update(Some(new Timestamp(newExpiration)))
    db.run(action.asTry).map { tryId => tryId.isSuccess }
  }

  private def getUserTypeIdByRefCode(refCode: String): Future[Option[Int]] = {
    val userTypeQuery = userTypes.filter(_.refCode === "USER").map { _.userTypeId }
    val userTypeAction = userTypeQuery.result.headOption
    db.run(userTypeAction)
  }

  private def createNewUser(email: String, username: String, password: String): Future[(Boolean, String)] = {
    val id = users.returning(users.map { _.userId }) += Tables.UserRow(-1, userUserTypeId, email, username, password, None, None)
    val createNewUserResult = db.run(id.asTry)
    createNewUserResult.map {
      tryInt =>
        tryInt match {
          case Success(userId) => (userId > 0, "success")
          case Failure(error)  => (false, error.getMessage)
        }
    }

  }

  private def convertTableUserToUser(utr: Tables.UserRow): UserData = {
    UserData(utr.userId, utr.email, utr.username, utr.password, utr.authToken, utr.authTokenExp)
  }

  private def getFutureUserTypeIdByRefCode(refCode: String): Future[Int] = {
    val idQuery = userTypes.filter { ut => ut.refCode === refCode }.map { ut => ut.userTypeId }
    db.run(idQuery.result.head)
  }

  lifecycle.addStopHook { () => Future.successful(db.close()) }
}

