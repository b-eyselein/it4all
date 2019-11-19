package model.persistence

import model._
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  private val logger = Logger(classOf[TableDefs])

  import profile.api._

  implicit val executionContext: ExecutionContext

  // Table queries

  protected val users = TableQuery[UsersTable]

  private val pwHashes = TableQuery[PwHashesTable]


  // Numbers

  def numOfUsers: Future[Int] = db.run(users.size.result)

  // Reading

  def allUsers: Future[Seq[User]] = db.run(users.result)

  def userByName(username: String): Future[Option[User]] = db.run(users.filter(_.username === username).result.headOption)

  def pwHashForUser(username: String): Future[Option[PwHash]] = db.run(pwHashes.filter(_.username === username).result.headOption)


  // Update

  def updateUserRole(userToChangeName: String, newRole: Role): Future[Boolean] = {
    implicit val rct: BaseColumnType[Role] = roleColumnType

    db.run(users.filter(_.username === userToChangeName).map(_.role).update(newRole))
      .map(_ => true)
      .recover { case e: Throwable =>
        logger.error(s"Could not update std role of user $userToChangeName to ${newRole.entryName}", e)
        false
      }
  }

  // Insert

  def saveUser(user: User): Future[Boolean] = db.run(users += user).transform(_ == 1, identity)

  //  def savePwHash(pwHash: PwHash): Future[Boolean] = db.run(pwHashes += pwHash).transform(_ == 1, identity)

  // Abstract queries

  protected def saveSeq[T](seqToSave: Seq[T], save: T => Future[Any], saveType: Option[String] = None): Future[Boolean] = Future.sequence(
    seqToSave.map {
      save(_).transform {
        case Success(_) => Success(true)
        case Failure(e) =>
          logger.error("Could not perform save option" + saveType.map(st => s" on $st").getOrElse(""), e)
          Success(false)
      }
    }).map(_.forall(identity))

  protected def saveSingle(performSave: => Future[Any]): Future[Boolean] = performSave.transform {
    case Success(_) => Success(true)
    case Failure(e) =>
      logger.error("Could not perform save option", e)
      Success(false)
  }

  // Column types

  private val roleColumnType: BaseColumnType[Role] =
    MappedColumnType.base[Role, String](_.entryName, str => Role.withNameInsensitiveOption(str) getOrElse Role.RoleUser)

  protected implicit val exerciseStateColumnType: BaseColumnType[ExerciseState] =
    MappedColumnType.base[ExerciseState, String](_.entryName, str => ExerciseState.withNameInsensitiveOption(str) getOrElse ExerciseState.CREATED)

  protected implicit val semanticVersionColumnType: BaseColumnType[SemanticVersion] =
    MappedColumnType.base[SemanticVersion, String](_.asString, SemanticVersionHelper.parseFromString(_).getOrElse(SemanticVersionHelper.DEFAULT))

  // Tables

  protected class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    private implicit val rct: BaseColumnType[Role] = roleColumnType


    def userType: Rep[Int] = column[Int]("user_type")

    def username: Rep[String] = column[String]("username", O.PrimaryKey)

    def role: Rep[Role] = column[Role]("std_role")


    override def * : ProvenShape[User] = (userType, username, role) <> (tupled, unapplied)


    def tupled(values: (Int, String, Role)): User = values match {
      case (1, username, role) => LtiUser(username, role)
      case (_, username, role) => RegisteredUser(username, role)
    }

    def unapplied(user: User): Option[(Int, String, Role)] = user match {
      case LtiUser(username, role)        => Some((1, username, role))
      case RegisteredUser(username, role) => Some((0, username, role))
    }

  }

  protected class PwHashesTable(tag: Tag) extends Table[PwHash](tag, "pw_hashes") {

    def username: Rep[String] = column[String]("username", O.PrimaryKey)

    def pwHash: Rep[String] = column[String]("pw_hash")


    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)


    override def * : ProvenShape[PwHash] = (username, pwHash) <> (PwHash.tupled, PwHash.unapply)

  }

}
