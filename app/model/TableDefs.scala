package model

import model.Enums.{ExerciseState, Role, ShowHideAggregate}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

case class User(username: String, pwHash: String, stdRole: Role = Role.RoleUser, todo: ShowHideAggregate = ShowHideAggregate.SHOW) {
  val isAdmin: Boolean = stdRole ne Role.RoleUser
}

case class Course(id: Int, courseName: String)

object TippHelper {
  val ran = new Random

  val StdTipp = "Hier werden in Zukunft Tipps & Tricks zur Benutzung von it4all präsentiert."

  def getRandom: Tipp = null
}


case class Tipp(id: Int, str: String)

trait TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val users = TableQuery[UsersTable]

  val courses = TableQuery[CoursesTable]

  val tipps = TableQuery[TippsTable]

  abstract class ExerciseTableQuery[E <: Exercise, CompEx <: CompleteEx[E], T <: HasBaseValuesTable[E]](cons: Tag => T) extends TableQuery[T](cons) {

    //noinspection TypeAnnotation
    protected def exercise(id: Int) = this.filter(_.id === id).result.headOption

    def completeById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompEx]] = db.run(exercise(id)) flatMap {
      case Some(ex) => completeExForEx(ex) map (Some(_))
      case None     => Future(None)
    }

    def completeExes(implicit ec: ExecutionContext): Future[Seq[CompEx]] = db.run(this.sortBy(_.id).result)
      .flatMap(exes => Future.sequence(exes map completeExForEx))

    protected def saveEx(ex: E): DBIOAction[Int, NoStream, Effect.Write] = this insertOrUpdate ex


    def saveCompleteEx(completeEx: CompEx)(implicit ec: ExecutionContext): Future[Int]

    protected def completeExForEx(ex: E)(implicit ec: ExecutionContext): Future[CompEx]

  }

  implicit def roleColumnType: BaseColumnType[Role] =
    MappedColumnType.base[Role, String](_.name, str => Option(Role.valueOf(str)).getOrElse(Role.RoleUser))

  implicit def showhideaggrColumnType: BaseColumnType[ShowHideAggregate] =
    MappedColumnType.base[ShowHideAggregate, String](_.name, str => Option(ShowHideAggregate.valueOf(str)).getOrElse(ShowHideAggregate.SHOW))

  implicit def exercisetypeColumnType: BaseColumnType[ExerciseState] =
    MappedColumnType.base[ExerciseState, String](_.name, str => Option(ExerciseState.valueOf(str)).getOrElse(ExerciseState.CREATED))

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def username = column[String]("username", O.PrimaryKey)

    def pwHash = column[String]("pw_hash")

    def role = column[Role]("std_role")

    def todo = column[ShowHideAggregate]("todo")

    def * = (username, pwHash, role, todo) <> (User.tupled, User.unapply)

  }

  class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def courseName = column[String]("course_name")

    def * = (id, courseName) <> (Course.tupled, Course.unapply)

  }

  class TippsTable(tag: Tag) extends Table[Tipp](tag, "tipps") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def str = column[String]("str")

    def * = (id, str) <> (Tipp.tupled, Tipp.unapply)

  }

  abstract class HasBaseValuesTable[E <: HasBaseValues](tag: Tag, name: String) extends Table[E](tag, name) {

    def id = column[Int]("id", O.PrimaryKey)

    def title = column[String]("title")

    def author = column[String]("author")

    def text = column[String]("ex_text")

    def state = column[ExerciseState]("ex_state")

  }

}
