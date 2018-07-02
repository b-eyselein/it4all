package model.persistence

import com.github.t3hnar.bcrypt._
import model._
import model.core.CoreConsts._
import model.enums.Mark
import model.feedback.{Feedback, FeedbackTableHelper}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  implicit val executionContext: ExecutionContext

  // Table queries

  val users = TableQuery[UsersTable]

  val pwHashes = TableQuery[PwHashesTable]


  val courses = TableQuery[CoursesTable]

  val usersInCourses = TableQuery[UsersInCoursesTable]

  val tipps = TableQuery[TippsTable]


  val feedbacks = TableQuery[FeedbackTable]

  // Numbers

  def numOfUsers: Future[Int] = db.run(users.size.result)

  def numOfCourses: Future[Int] = db.run(courses.size.result)

  // Reading

  def allUsers: Future[Seq[User]] = db.run(users.result)

  def userByName(username: String): Future[Option[User]] = db.run(users.filter(_.username === username).result.headOption)

  def pwHashForUser(username: String): Future[Option[PwHash]] = db.run(pwHashes.filter(_.username === username).result.headOption)


  def allCourses: Future[Seq[Course]] = db.run(courses.result)

  def courseById(courseId: String): Future[Option[Course]] = db.run(courses.filter(_.id === courseId).result.headOption)


  def coursesForUser(user: User): Future[Seq[Course]] = db.run(courses.join(usersInCourses).on {
    case (course, userInCourse) => course.id === userInCourse.courseId
  }.filter {
    case (_, userInCourse) => userInCourse.username === user.username
  }.map(_._1).result)

  def userInCourse(user: User, course: Course): Future[Option[UserInCourse]] =
    db.run(usersInCourses.filter(uInC => uInC.username === user.username && uInC.courseId === course.id).result.headOption)

  def futureMaybeFeedback(user: User, toolUrlPart: String): Future[Option[Feedback]] =
    db.run(feedbacks.filter(fb => fb.username === user.username && fb.toolUrlPart === toolUrlPart).result.headOption)

  def futureEvaluationResultsForTools: Future[Seq[Feedback]] = db.run(feedbacks.result)

  // Update

  def updateUserRole(userToChangeName: String, newRole: Role): Future[Boolean] =
    db.run(users.filter(_.username === userToChangeName).map(_.role).update(newRole)) map (_ => true) recover { case e: Throwable =>
      Logger.error(s"Could not update std role of user $userToChangeName to ${newRole.entryName}", e)
      false
    }

  def updateShowHideAggregate(user: User, newPref: ShowHideAggregate): Future[Int] =
    db.run(users filter (_.username === user.username) map (_.showHideAgg) update newPref)

  def updateUserPassword(user: User, newPW: String): Future[Int] =
    db.run(pwHashes filter (_.username === user.username) map (_.pwHash) update newPW.bcrypt)

  // Insert

  def saveUser(user: User): Future[Boolean] = db.run(users += user) map (_ => true) recover {
    case e: Throwable =>
      Logger.error(s"Could not save user $user", e)
      false
  }

  def savePwHash(pwHash: PwHash): Future[Boolean] = db.run(pwHashes += pwHash) map (_ => true) recover {
    case e: Throwable =>
      Logger.error(s"Could not save pwHash $pwHash", e)
      false
  }

  def saveCourse(course: Course): Future[Boolean] = db.run(courses += course) map (_ => true) recover {
    case e: Throwable =>
      Logger.error("Could not save course", e)
      false
  }

  def addUserToCourse(userInCourse: UserInCourse): Future[Boolean] =
    db.run(usersInCourses += userInCourse) map (_ => true) recover {
      case e: Throwable =>
        Logger.error("Could not add user to course", e)
        false
    }

  def saveFeedback(feedback: Feedback): Future[Boolean] = saveSingle(db.run(feedbacks insertOrUpdate feedback))

  // Abstract queries

  protected def saveSeq[T](seqToSave: Seq[T], save: T => Future[Any]): Future[Boolean] = Future.sequence(seqToSave map {
    toSave =>
      save(toSave) map (_ => true) recover { case e: Exception =>
        Logger.error("Could not perform save option", e)
        false
      }
  }) map (_ forall identity)

  protected def saveSingle(performSave: => Future[Any]): Future[Boolean] = performSave map (_ => true) recover {
    case e: Throwable =>
      Logger.error("Could not perform save option", e)
      false
  }

  // Column types

  private implicit val roleColumnType: BaseColumnType[Role] =
    MappedColumnType.base[Role, String](_.entryName, str => Role.withNameInsensitiveOption(str) getOrElse Role.RoleUser)

  private implicit val showhideaggrColumnType: BaseColumnType[ShowHideAggregate] =
    MappedColumnType.base[ShowHideAggregate, String](_.entryName, str => ShowHideAggregate.withNameInsensitiveOption(str) getOrElse ShowHideAggregate.SHOW)

  protected implicit val exercisetypeColumnType: BaseColumnType[ExerciseState] =
    MappedColumnType.base[ExerciseState, String](_.entryName, str => ExerciseState.withNameInsensitiveOption(str) getOrElse ExerciseState.CREATED)

  private implicit val markColumnType: BaseColumnType[Mark] =
    MappedColumnType.base[Mark, String](_.entryName, str => Mark.withNameInsensitiveOption(str) getOrElse Mark.NO_MARK)

  protected implicit val semanticVersionColumnType: BaseColumnType[SemanticVersion] =
    MappedColumnType.base[SemanticVersion, String](_.asString, SemanticVersionHelper.fromString)

  // Tables

  // Users

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def userType = column[Int]("user_type")

    def username = column[String]("username", O.PrimaryKey)

    def role = column[Role]("std_role")

    def showHideAgg = column[ShowHideAggregate]("showHideAgg")


    override def * = (userType, username, role, showHideAgg) <> (tupled, unapplied)

    def tupled(values: (Int, String, Role, ShowHideAggregate)): User = values._1 match {
      case 1 => LtiUser(values._2, values._3, values._4)
      case _ => RegisteredUser(values._2, values._3, values._4)
    }

    def unapplied(user: User): Option[(Int, String, Role, ShowHideAggregate)] = user match {
      case LtiUser(username, role, showHideAggregate)        => Some(1, username, role, showHideAggregate)
      case RegisteredUser(username, role, showHideAggregate) => Some(0, username, role, showHideAggregate)
    }

  }

  class PwHashesTable(tag: Tag) extends Table[PwHash](tag, "pw_hashes") {

    def username = column[String]("username", O.PrimaryKey)

    def pwHash = column[String]("pw_hash")


    def userFk = foreignKey("user_fk", username, users)(_.username)


    def * = (username, pwHash) <> (PwHash.tupled, PwHash.unapply)

  }

  // Courses

  class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") {

    def id = column[String](idName, O.PrimaryKey)

    def courseName = column[String]("course_name")


    override def * = (id, courseName) <> (Course.tupled, Course.unapply)

  }

  class UsersInCoursesTable(tag: Tag) extends Table[UserInCourse](tag, "users_in_courses") {

    def username = column[String]("username")

    def courseId = column[String]("course_id")

    def role = column[Role]("role")


    def pk = primaryKey("pk", (username, courseId))

    def userFk = foreignKey("user_fk", username, users)(_.username)

    def courseFk = foreignKey("course_fk", courseId, courses)(_.id)


    override def * = (username, courseId, role) <> (UserInCourse.tupled, UserInCourse.unapply)

  }

  // Tipps

  class TippsTable(tag: Tag) extends Table[Tipp](tag, "tipps") {

    def id = column[Int](idName, O.PrimaryKey, O.AutoInc)

    def str = column[String]("str")

    override def * = (id, str) <> (Tipp.tupled, Tipp.unapply)

  }

  // Feedback

  class FeedbackTable(tag: Tag) extends Table[Feedback](tag, "feedback") {

    def username = column[String](usernameName)

    def toolUrlPart = column[String]("tool_url")

    def sense = column[Mark]("sense")

    def used = column[Mark]("used")

    def usability = column[Mark]("usability")

    def feedback = column[Mark]("feedback")

    def fairness = column[Mark]("fairness")

    def comment = column[String]("comment")


    def pk = primaryKey("pk", (username, toolUrlPart))

    def userFk = foreignKey("user_fk", username, users)(_.username)


    override def * = (username, toolUrlPart, sense, used, usability, feedback, fairness, comment) <> (FeedbackTableHelper.fromTableTupled, FeedbackTableHelper.forTableUnapplied)

  }

  // Base table for exercises and exerciseCollections

  abstract class HasBaseValuesTable[E <: HasBaseValues](tag: Tag, name: String) extends Table[E](tag, name) {

    def id = column[Int](idName)

    def title = column[String]("title")

    def author = column[String]("author")

    def text = column[String]("ex_text")

    def state = column[ExerciseState]("ex_state")

    def semanticVersion = column[SemanticVersion]("semantic_version")

  }

}
