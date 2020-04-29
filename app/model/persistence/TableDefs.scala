package model.persistence

import javax.inject.Inject
import model._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

class TableDefs @Inject() (
  override protected val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  // Table queries

  private val users = TableQuery[UsersTable]

  // Reading

  def userByName(username: String): Future[Option[User]] = db.run(
    users
      .filter(_.username === username)
      .result
      .headOption
  )

  def saveUser(user: User)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(users += user).transform(_ == 1, identity)

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
