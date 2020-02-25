package model.persistence

import model._
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait TableDefs extends HasDatabaseConfigProvider[JdbcProfile] {

  private val logger = Logger(classOf[TableDefs])

  import profile.api._

  implicit val executionContext: ExecutionContext

  // Table queries

  protected val users = TableQuery[UsersTable]

  // Numbers

  def numOfUsers: Future[Int] = db.run(users.size.result)

  // Reading

  def allUsers: Future[Seq[User]] = db.run(users.result)

  def userByName(username: String): Future[Option[User]] =
    db.run(users.filter(_.username === username).result.headOption)

  // Update

  def updateUserRole(userToChangeName: String, newRole: Role): Future[Boolean] = {
    implicit val rct: BaseColumnType[Role] = roleColumnType

    val query = users.filter(_.username === userToChangeName).map(_.role).update(newRole)

    db.run(query).transform(_ == 1, identity)
  }

  // Insert

  def saveUser(user: User): Future[Boolean] = db.run(users += user).transform(_ == 1, identity)

  //  def savePwHash(pwHash: PwHash): Future[Boolean] = db.run(pwHashes += pwHash).transform(_ == 1, identity)

  // Abstract queries

  protected def saveSeq[T](
    seqToSave: Seq[T],
    save: T => Future[Any],
    saveType: Option[String] = None
  ): Future[Boolean] =
    Future
      .sequence(seqToSave.map {
        save(_).transform {
          case Success(_) => Success(true)
          case Failure(e) =>
            logger.error("Could not perform save option" + saveType.map(st => s" on $st").getOrElse(""), e)
            Success(false)
        }
      })
      .map(_.forall(identity))

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
