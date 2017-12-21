package com.acadaca.fakenews.tables
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Article.schema, ArticleRating.schema, ArticleStatus.schema, User.schema, UserArticleRating.schema, UserType.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Article
   *  @param articleId Database column article_id SqlType(INT), AutoInc, PrimaryKey
   *  @param articleUrl Database column article_url SqlType(VARCHAR), Length(2047,true)
   *  @param articleHtml Database column article_html SqlType(MEDIUMTEXT), Length(16777215,true)
   *  @param articleStatusId Database column article_status_id SqlType(INT)
   *  @param userId Database column user_id SqlType(INT), Default(None)
   *  @param title Database column title SqlType(VARCHAR), Length(255,true)
   *  @param shortDescription Database column short_description SqlType(VARCHAR), Length(255,true) */
  case class ArticleRow(articleId: Int, articleUrl: String, articleHtml: String, articleStatusId: Int, userId: Option[Int] = None, title: String, shortDescription: String)
  /** GetResult implicit for fetching ArticleRow objects using plain SQL queries */
  implicit def GetResultArticleRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Int]]): GR[ArticleRow] = GR{
    prs => import prs._
    ArticleRow.tupled((<<[Int], <<[String], <<[String], <<[Int], <<?[Int], <<[String], <<[String]))
  }
  /** Table description of table article. Objects of this class serve as prototypes for rows in queries. */
  class Article(_tableTag: Tag) extends Table[ArticleRow](_tableTag, "article") {
    def * = (articleId, articleUrl, articleHtml, articleStatusId, userId, title, shortDescription) <> (ArticleRow.tupled, ArticleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(articleId), Rep.Some(articleUrl), Rep.Some(articleHtml), Rep.Some(articleStatusId), userId, Rep.Some(title), Rep.Some(shortDescription)).shaped.<>({r=>import r._; _1.map(_=> ArticleRow.tupled((_1.get, _2.get, _3.get, _4.get, _5, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column article_id SqlType(INT), AutoInc, PrimaryKey */
    val articleId: Rep[Int] = column[Int]("article_id", O.AutoInc, O.PrimaryKey)
    /** Database column article_url SqlType(VARCHAR), Length(2047,true) */
    val articleUrl: Rep[String] = column[String]("article_url", O.Length(2047,varying=true))
    /** Database column article_html SqlType(MEDIUMTEXT), Length(16777215,true) */
    val articleHtml: Rep[String] = column[String]("article_html", O.Length(16777215,varying=true))
    /** Database column article_status_id SqlType(INT) */
    val articleStatusId: Rep[Int] = column[Int]("article_status_id")
    /** Database column user_id SqlType(INT), Default(None) */
    val userId: Rep[Option[Int]] = column[Option[Int]]("user_id", O.Default(None))
    /** Database column title SqlType(VARCHAR), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column short_description SqlType(VARCHAR), Length(255,true) */
    val shortDescription: Rep[String] = column[String]("short_description", O.Length(255,varying=true))

    /** Foreign key referencing ArticleStatus (database name fk_article_1) */
    lazy val articleStatusFk = foreignKey("fk_article_1", articleStatusId, ArticleStatus)(r => r.articleStatusId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing User (database name fk_article_2) */
    lazy val userFk = foreignKey("fk_article_2", userId, User)(r => Rep.Some(r.userId), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (articleUrl) (database name article_url) */
    val index1 = index("article_url", articleUrl, unique=true)
  }
  /** Collection-like TableQuery object for table Article */
  lazy val Article = new TableQuery(tag => new Article(tag))

  /** Entity class storing rows of table ArticleRating
   *  @param articleRatingId Database column article_rating_id SqlType(INT), AutoInc, PrimaryKey
   *  @param refCode Database column ref_code SqlType(VARCHAR), Length(255,true) */
  case class ArticleRatingRow(articleRatingId: Int, refCode: String)
  /** GetResult implicit for fetching ArticleRatingRow objects using plain SQL queries */
  implicit def GetResultArticleRatingRow(implicit e0: GR[Int], e1: GR[String]): GR[ArticleRatingRow] = GR{
    prs => import prs._
    ArticleRatingRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table article_rating. Objects of this class serve as prototypes for rows in queries. */
  class ArticleRating(_tableTag: Tag) extends Table[ArticleRatingRow](_tableTag, "article_rating") {
    def * = (articleRatingId, refCode) <> (ArticleRatingRow.tupled, ArticleRatingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(articleRatingId), Rep.Some(refCode)).shaped.<>({r=>import r._; _1.map(_=> ArticleRatingRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column article_rating_id SqlType(INT), AutoInc, PrimaryKey */
    val articleRatingId: Rep[Int] = column[Int]("article_rating_id", O.AutoInc, O.PrimaryKey)
    /** Database column ref_code SqlType(VARCHAR), Length(255,true) */
    val refCode: Rep[String] = column[String]("ref_code", O.Length(255,varying=true))

    /** Uniqueness Index over (refCode) (database name ref_code) */
    val index1 = index("ref_code", refCode, unique=true)
  }
  /** Collection-like TableQuery object for table ArticleRating */
  lazy val ArticleRating = new TableQuery(tag => new ArticleRating(tag))

  /** Entity class storing rows of table ArticleStatus
   *  @param articleStatusId Database column article_status_id SqlType(INT), AutoInc, PrimaryKey
   *  @param refCode Database column ref_code SqlType(VARCHAR), Length(255,true) */
  case class ArticleStatusRow(articleStatusId: Int, refCode: String)
  /** GetResult implicit for fetching ArticleStatusRow objects using plain SQL queries */
  implicit def GetResultArticleStatusRow(implicit e0: GR[Int], e1: GR[String]): GR[ArticleStatusRow] = GR{
    prs => import prs._
    ArticleStatusRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table article_status. Objects of this class serve as prototypes for rows in queries. */
  class ArticleStatus(_tableTag: Tag) extends Table[ArticleStatusRow](_tableTag, "article_status") {
    def * = (articleStatusId, refCode) <> (ArticleStatusRow.tupled, ArticleStatusRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(articleStatusId), Rep.Some(refCode)).shaped.<>({r=>import r._; _1.map(_=> ArticleStatusRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column article_status_id SqlType(INT), AutoInc, PrimaryKey */
    val articleStatusId: Rep[Int] = column[Int]("article_status_id", O.AutoInc, O.PrimaryKey)
    /** Database column ref_code SqlType(VARCHAR), Length(255,true) */
    val refCode: Rep[String] = column[String]("ref_code", O.Length(255,varying=true))

    /** Uniqueness Index over (refCode) (database name ref_code) */
    val index1 = index("ref_code", refCode, unique=true)
  }
  /** Collection-like TableQuery object for table ArticleStatus */
  lazy val ArticleStatus = new TableQuery(tag => new ArticleStatus(tag))

  /** Entity class storing rows of table User
   *  @param userId Database column user_id SqlType(INT), AutoInc, PrimaryKey
   *  @param userTypeId Database column user_type_id SqlType(INT)
   *  @param email Database column email SqlType(VARCHAR), Length(255,true)
   *  @param username Database column username SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true)
   *  @param authToken Database column auth_token SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param authTokenExp Database column auth_token_exp SqlType(DATETIME), Default(None) */
  case class UserRow(userId: Int, userTypeId: Int, email: String, username: String, password: String, authToken: Option[String] = None, authTokenExp: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[String]], e3: GR[Option[java.sql.Timestamp]]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[String], <<?[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends Table[UserRow](_tableTag, "user") {
    def * = (userId, userTypeId, email, username, password, authToken, authTokenExp) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userId), Rep.Some(userTypeId), Rep.Some(email), Rep.Some(username), Rep.Some(password), authToken, authTokenExp).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_id SqlType(INT), AutoInc, PrimaryKey */
    val userId: Rep[Int] = column[Int]("user_id", O.AutoInc, O.PrimaryKey)
    /** Database column user_type_id SqlType(INT) */
    val userTypeId: Rep[Int] = column[Int]("user_type_id")
    /** Database column email SqlType(VARCHAR), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column username SqlType(VARCHAR), Length(255,true) */
    val username: Rep[String] = column[String]("username", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
    /** Database column auth_token SqlType(VARCHAR), Length(255,true), Default(None) */
    val authToken: Rep[Option[String]] = column[Option[String]]("auth_token", O.Length(255,varying=true), O.Default(None))
    /** Database column auth_token_exp SqlType(DATETIME), Default(None) */
    val authTokenExp: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("auth_token_exp", O.Default(None))

    /** Foreign key referencing UserType (database name user_ibfk_1) */
    lazy val userTypeFk = foreignKey("user_ibfk_1", userTypeId, UserType)(r => r.userTypeId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (email) (database name email_UNIQUE) */
    val index1 = index("email_UNIQUE", email, unique=true)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))

  /** Entity class storing rows of table UserArticleRating
   *  @param userArticleRatingId Database column user_article_rating_id SqlType(INT), PrimaryKey
   *  @param userId Database column user_id SqlType(INT)
   *  @param articleId Database column article_id SqlType(INT)
   *  @param articleRatingId Database column article_rating_id SqlType(INT) */
  case class UserArticleRatingRow(userArticleRatingId: Int, userId: Int, articleId: Int, articleRatingId: Int)
  /** GetResult implicit for fetching UserArticleRatingRow objects using plain SQL queries */
  implicit def GetResultUserArticleRatingRow(implicit e0: GR[Int]): GR[UserArticleRatingRow] = GR{
    prs => import prs._
    UserArticleRatingRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table user_article_rating. Objects of this class serve as prototypes for rows in queries. */
  class UserArticleRating(_tableTag: Tag) extends Table[UserArticleRatingRow](_tableTag, "user_article_rating") {
    def * = (userArticleRatingId, userId, articleId, articleRatingId) <> (UserArticleRatingRow.tupled, UserArticleRatingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userArticleRatingId), Rep.Some(userId), Rep.Some(articleId), Rep.Some(articleRatingId)).shaped.<>({r=>import r._; _1.map(_=> UserArticleRatingRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_article_rating_id SqlType(INT), PrimaryKey */
    val userArticleRatingId: Rep[Int] = column[Int]("user_article_rating_id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column article_id SqlType(INT) */
    val articleId: Rep[Int] = column[Int]("article_id")
    /** Database column article_rating_id SqlType(INT) */
    val articleRatingId: Rep[Int] = column[Int]("article_rating_id")

    /** Foreign key referencing Article (database name fk_user_article_rating_2) */
    lazy val articleFk = foreignKey("fk_user_article_rating_2", articleId, Article)(r => r.articleId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing ArticleRating (database name fk_user_article_rating_3) */
    lazy val articleRatingFk = foreignKey("fk_user_article_rating_3", articleRatingId, ArticleRating)(r => r.articleRatingId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing User (database name fk_user_article_rating_1) */
    lazy val userFk = foreignKey("fk_user_article_rating_1", userId, User)(r => r.userId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (userId,articleId) (database name user_id) */
    val index1 = index("user_id", (userId, articleId), unique=true)
  }
  /** Collection-like TableQuery object for table UserArticleRating */
  lazy val UserArticleRating = new TableQuery(tag => new UserArticleRating(tag))

  /** Entity class storing rows of table UserType
   *  @param userTypeId Database column user_type_id SqlType(INT), AutoInc, PrimaryKey
   *  @param refCode Database column ref_code SqlType(VARCHAR), Length(255,true) */
  case class UserTypeRow(userTypeId: Int, refCode: String)
  /** GetResult implicit for fetching UserTypeRow objects using plain SQL queries */
  implicit def GetResultUserTypeRow(implicit e0: GR[Int], e1: GR[String]): GR[UserTypeRow] = GR{
    prs => import prs._
    UserTypeRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table user_type. Objects of this class serve as prototypes for rows in queries. */
  class UserType(_tableTag: Tag) extends Table[UserTypeRow](_tableTag, "user_type") {
    def * = (userTypeId, refCode) <> (UserTypeRow.tupled, UserTypeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(userTypeId), Rep.Some(refCode)).shaped.<>({r=>import r._; _1.map(_=> UserTypeRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column user_type_id SqlType(INT), AutoInc, PrimaryKey */
    val userTypeId: Rep[Int] = column[Int]("user_type_id", O.AutoInc, O.PrimaryKey)
    /** Database column ref_code SqlType(VARCHAR), Length(255,true) */
    val refCode: Rep[String] = column[String]("ref_code", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table UserType */
  lazy val UserType = new TableQuery(tag => new UserType(tag))
}
