package model

import model.Enums.{ExerciseState, Role, ShowHideAggregate}
import model.core.{ExTag, HasBaseValues}
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

case class User(username: String, pwHash: String, stdRole: Role = Role.RoleUser, todo: ShowHideAggregate = ShowHideAggregate.SHOW) {
  val isAdmin: Boolean = stdRole ne Role.RoleUser
}

case class Course(id: Int, courseName: String)

case class Tipp(id: Int, str: String)

abstract class Exercise(i: Int, ti: String, a: String, te: String, s: ExerciseState) extends HasBaseValues(i, ti, a, te, s) {
  def getTags: List[ExTag] = List.empty

  def renderEditRest(isCreation: Boolean) = new Html("")

}

trait TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val users = TableQuery[UsersTable]

  val courses = TableQuery[CoursesTable]

  val tipps = TableQuery[TippsTable]

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

    def courseName = column[String]("courseName")

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
