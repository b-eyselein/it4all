package model

import scala.concurrent.{ExecutionContext, Future}

final case class User(username: String, pwHash: Option[String] = None)

trait UserRepository {
  self: TableDefs =>

  import profile.api._

  protected implicit val ec: ExecutionContext

  protected val usersTQ = TableQuery[UsersTable]

  def futureUserByUsername(username: String): Future[Option[User]] = db.run(usersTQ.filter(_.username === username).result.headOption)

  def futureInsertUser(username: String, maybePwHash: Option[String]): Future[User] = db.run(usersTQ.returning(usersTQ) += User(username, maybePwHash))

  protected class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def username = column[String]("username", O.PrimaryKey)

    def maybePwHash = column[Option[String]]("maybe_pw_hash")

    override def * = (username, maybePwHash) <> (User.tupled, User.unapply)

  }

}
