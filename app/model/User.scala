package model

import scala.concurrent.{ExecutionContext, Future}

final case class RegisterValues(username: String, firstPassword: String, secondPassword: String) {

  def isInvalid: Boolean = firstPassword != secondPassword

}

final case class UserCredentials(username: String, password: String)

final case class User(username: String, pwHash: Option[String] = None)

final case class LoginResult(
  username: String,
  jwt: String
)

trait UserRepository {
  self: play.api.db.slick.HasDatabaseConfig[slick.jdbc.JdbcProfile] =>

  import profile.api._

  protected implicit val ec: ExecutionContext

  private val usersTQ = TableQuery[UsersTable]

  def futureUserByUsername(username: String): Future[Option[User]] = db.run(usersTQ.filter(_.username === username).result.headOption)

  def futureInsertUser(username: String, maybePwHash: Option[String]): Future[User] = db.run(usersTQ.returning(usersTQ) += User(username, maybePwHash))

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def username = column[String]("username", O.PrimaryKey)

    def maybePwHash = column[Option[String]]("maybe_pw_hash")

    override def * = (username, maybePwHash) <> (User.tupled, User.unapply)

  }

}
