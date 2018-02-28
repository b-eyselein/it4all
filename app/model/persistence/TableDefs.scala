package model.persistence

import com.github.t3hnar.bcrypt._
import model.Enums.{ExerciseState, Role, ShowHideAggregate}
import model.core.CoreConsts._
import model.{ExInColl, HasBaseValues, _}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


trait TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val users = TableQuery[UsersTable]

  val courses = TableQuery[CoursesTable]

  val tipps = TableQuery[TippsTable]

  def userByName(username: String): Future[Option[User]] = db.run(users.findBy(_.username).apply(username).result.headOption)

  def allUsers: Future[Seq[User]] = db.run(users.result)

  def numOfUsers: Future[Int] = db.run(users.size.result)

  def allCourses: Future[Seq[Course]] = db.run(courses.result)

  def numOfCourses: Future[Int] = db.run(courses.size.result)

  protected def saveSeq[T](seqToSave: Seq[T], save: T => Future[Any])(implicit ec: ExecutionContext): Future[Boolean] = Future.sequence(seqToSave map {
    toSave => save(toSave) map (_ => true) recover { case _: Exception => false }
  }) map (_ forall identity)

  def updateShowHideAggregate(user: User, newPref: ShowHideAggregate): Future[Int] =
    db.run(users filter (_.username === user.username) map (_.todo) update newPref)

  def updateUserPassword(user: User, newPW: String): Future[Int] =
    db.run(users filter (_.username === user.username) map (_.pwHash) update newPW.bcrypt)

  def saveUser(username: String, pw: String): Future[Int] = db.run(users += User(username, pw.bcrypt))

  implicit val roleColumnType: BaseColumnType[Role] =
    MappedColumnType.base[Role, String](_.name, str => Option(Role.valueOf(str)).getOrElse(Role.RoleUser))

  implicit val showhideaggrColumnType: BaseColumnType[ShowHideAggregate] =
    MappedColumnType.base[ShowHideAggregate, String](_.name, str => ShowHideAggregate.byString(str) getOrElse ShowHideAggregate.SHOW)

  implicit val exercisetypeColumnType: BaseColumnType[ExerciseState] =
    MappedColumnType.base[ExerciseState, String](_.name, str => ExerciseState.byString(str) getOrElse ExerciseState.CREATED)

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def username = column[String]("username", O.PrimaryKey)

    def pwHash = column[String]("pw_hash")

    def role = column[Role]("std_role")

    def todo = column[ShowHideAggregate]("todo")

    def * = (username, pwHash, role, todo) <> (User.tupled, User.unapply)

  }

  class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") {

    def id = column[Int](ID_NAME, O.PrimaryKey, O.AutoInc)

    def courseName = column[String]("course_name")

    def * = (id, courseName) <> (Course.tupled, Course.unapply)

  }

  class TippsTable(tag: Tag) extends Table[Tipp](tag, "tipps") {

    def id = column[Int](ID_NAME, O.PrimaryKey, O.AutoInc)

    def str = column[String]("str")

    def * = (id, str) <> (Tipp.tupled, Tipp.unapply)

  }

  abstract class HasBaseValuesTable[E <: HasBaseValues](tag: Tag, name: String) extends Table[E](tag, name) {

    def id = column[Int](ID_NAME)

    def title = column[String]("title")

    def author = column[String]("author")

    def text = column[String]("ex_text")

    def state = column[ExerciseState]("ex_state")

  }

}
