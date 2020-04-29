package model.persistence

import model._
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

trait TableDefs extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  protected implicit val executionContext: ExecutionContext

  // Table queries

  protected val users = TableQuery[UsersTable]

  // Reading

  def userByName(username: String): Future[Option[User]] = db.run(
    users
      .filter(_.username === username)
      .result
      .headOption
  )

  def saveUser(user: User): Future[Boolean] = db.run(users += user).transform(_ == 1, identity)

  // Column types

  private val roleColumnType: BaseColumnType[Role] =
    MappedColumnType.base[Role, String](_.entryName, Role.withNameInsensitive)

  // Tables

  protected class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    private implicit val rct: BaseColumnType[Role] = roleColumnType

    def username: Rep[String] = column[String]("username", O.PrimaryKey)

    def pwHash: Rep[Option[String]] = column[Option[String]]("pw_hash")

    def role: Rep[Role] = column[Role]("std_role")

    override def * : ProvenShape[User] = (username, pwHash, role) <> (User.tupled, User.unapply)

  }

}
