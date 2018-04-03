package model.persistence

import com.github.t3hnar.bcrypt._
import model.Enums.{ExerciseState, Role, ShowHideAggregate}
import model.core.CoreConsts._
import model.{HasBaseValues, _}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


trait TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Table queries

  val users = TableQuery[UsersTable]

  val courses = TableQuery[CoursesTable]

  val usersInCourses = TableQuery[UsersInCoursesTable]

  val tipps = TableQuery[TippsTable]

  // Numbers

  def numOfUsers: Future[Int] = db.run(users.size.result)

  def numOfCourses: Future[Int] = db.run(courses.size.result)


  // Reading

  def allUsers: Future[Seq[User]] = db.run(users.result)

  def userByName(username: String): Future[Option[User]] = db.run(users.findBy(_.username).apply(username).result.headOption)


  def allCourses: Future[Seq[Course]] = db.run(courses.result)


  def courseById(courseId: String)(implicit ec: ExecutionContext): Future[Option[Course]] =
    db.run(courses.filter(_.id === courseId).result.headOption)


  def coursesForUser(user: User)(implicit ec: ExecutionContext): Future[Seq[Course]] = db.run(courses.join(usersInCourses).on {
    case (course, userInCourse) => course.id === userInCourse.courseId
  }.filter {
    case (_, userInCourse) => userInCourse.username === user.username
  }.map(_._1).result)

  def userInCourse(user: User, course: Course)(implicit ec: ExecutionContext): Future[Option[UserInCourse]] =
    db.run(usersInCourses.filter(uInC => uInC.username === user.username && uInC.courseId === course.id).result.headOption)

  // Update

  def updateUserRole(userToChangeName: String, newRole: Role)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(users.filter(_.username === userToChangeName).map(_.role).update(newRole)) map (_ => true) recover { case e: Throwable =>
      Logger.error(s"Could not update std role of user $userToChangeName to ${newRole.name}", e)
      false
    }

  def updateShowHideAggregate(user: User, newPref: ShowHideAggregate): Future[Int] =
    db.run(users filter (_.username === user.username) map (_.todo) update newPref)

  def updateUserPassword(user: User, newPW: String): Future[Int] =
    db.run(users filter (_.username === user.username) map (_.pwHash) update newPW.bcrypt)

  // Insert

  def saveUser(user: User)(implicit ec: ExecutionContext): Future[Boolean] = db.run(users += user) map (_ => true) recover { case _: Throwable => false }

  def saveCourse(course: Course)(implicit ec: ExecutionContext): Future[Boolean] = db.run(courses += course) map (_ => true) recover {
    case e: Throwable =>
      Logger.error("Could not save course", e)
      false
  }

  def addUserToCourse(userInCourse: UserInCourse)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(usersInCourses += userInCourse) map (_ => true) recover {
      case e: Throwable =>
        Logger.error("Could not add user to course", e)
        false
    }

  // Abstract queries

  protected def saveSeq[T](seqToSave: Seq[T], save: T => Future[Any])(implicit ec: ExecutionContext): Future[Boolean] = Future.sequence(seqToSave map {
    toSave =>
      save(toSave) map (_ => true) recover { case e: Exception =>
        Logger.error("Could not perform save option", e)
        false
      }
  }) map (_ forall identity)


  // Column types

  implicit val roleColumnType: BaseColumnType[Role] =
    MappedColumnType.base[Role, String](_.name, str => Role.byString(str) getOrElse Role.RoleUser)

  implicit val showhideaggrColumnType: BaseColumnType[ShowHideAggregate] =
    MappedColumnType.base[ShowHideAggregate, String](_.name, str => ShowHideAggregate.byString(str) getOrElse ShowHideAggregate.SHOW)

  implicit val exercisetypeColumnType: BaseColumnType[ExerciseState] =
    MappedColumnType.base[ExerciseState, String](_.name, str => ExerciseState.byString(str) getOrElse ExerciseState.CREATED)

  // Tables

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def username = column[String]("username", O.PrimaryKey)

    def pwHash = column[String]("pw_hash")

    def role = column[Role]("std_role")

    def todo = column[ShowHideAggregate]("todo")

    override def * = (username, pwHash, role, todo) <> (User.tupled, User.unapply)

  }

  class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") {

    def id = column[String](idName, O.PrimaryKey)

    def courseName = column[String]("course_name")

    override def * = (id, courseName) <> (Course.tupled, Course.unapply)

  }

  //  case class UserInCourse(username: String, courseId: String, role: Role = Role.RoleUser)

  class UsersInCoursesTable(tag: Tag) extends Table[UserInCourse](tag, "users_in_courses") {

    def username = column[String]("username")

    def courseId = column[String]("course_id")

    def role = column[Role]("role")


    def pk = primaryKey("pk", (username, courseId))

    def userFk = foreignKey("user_fk", username, users)(_.username)

    def courseFk = foreignKey("course_fk", courseId, courses)(_.id)


    override def * = (username, courseId, role) <> (UserInCourse.tupled, UserInCourse.unapply)

  }

  class TippsTable(tag: Tag) extends Table[Tipp](tag, "tipps") {

    def id = column[Int](idName, O.PrimaryKey, O.AutoInc)

    def str = column[String]("str")

    override def * = (id, str) <> (Tipp.tupled, Tipp.unapply)

  }

  abstract class HasBaseValuesTable[E <: HasBaseValues](tag: Tag, name: String) extends Table[E](tag, name) {

    def id = column[Int](idName)

    def title = column[String]("title")

    def author = column[String]("author")

    def text = column[String]("ex_text")

    def state = column[ExerciseState]("ex_state")

  }

}
