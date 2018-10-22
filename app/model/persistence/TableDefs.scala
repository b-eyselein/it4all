package model.persistence

import com.github.t3hnar.bcrypt._
import model._
import model.core.CoreConsts._
import model.Mark
import model.feedback.{Feedback, FeedbackTableHelper}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  implicit val executionContext: ExecutionContext

  // Table queries

  val users = TableQuery[UsersTable]

  val pwHashes = TableQuery[PwHashesTable]


  val courses = TableQuery[CoursesTable]

  val usersInCourses = TableQuery[UsersInCoursesTable]


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
    save(_) transform {
      case Success(_) => Success(true)
      case Failure(e) =>
        Logger.error("Could not perform save option", e)
        Success(false)
    }
  }) map (_ forall identity)

  protected def saveSingle(performSave: => Future[Any]): Future[Boolean] = performSave transform {
    case Success(_) => Success(true)
    case Failure(e) =>
      Logger.error("Could not perform save option", e)
      Success(false)
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
    MappedColumnType.base[SemanticVersion, String](_.asString, SemanticVersionHelper.parseFromString(_).getOrElse(SemanticVersionHelper.DEFAULT))

  // Tables

  // Users

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {

    def userType: Rep[Int] = column[Int]("user_type")

    def username: Rep[String] = column[String]("username", O.PrimaryKey)

    def role: Rep[Role] = column[Role]("std_role")

    def showHideAgg: Rep[ShowHideAggregate] = column[ShowHideAggregate]("showHideAgg")


    override def * : ProvenShape[User] = (userType, username, role, showHideAgg) <> (tupled, unapplied)

    def tupled(values: (Int, String, Role, ShowHideAggregate)): User = values._1 match {
      case 1 => LtiUser(values._2, values._3, values._4)
      case _ => RegisteredUser(values._2, values._3, values._4)
    }

    def unapplied(user: User): Option[(Int, String, Role, ShowHideAggregate)] = user match {
      case LtiUser(username, role, showHideAggregate)        => Some((1, username, role, showHideAggregate))
      case RegisteredUser(username, role, showHideAggregate) => Some((0, username, role, showHideAggregate))
    }

  }

  class PwHashesTable(tag: Tag) extends Table[PwHash](tag, "pw_hashes") {

    def username: Rep[String] = column[String]("username", O.PrimaryKey)

    def pwHash: Rep[String] = column[String]("pw_hash")


    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)


    override def * : ProvenShape[PwHash] = (username, pwHash) <> (PwHash.tupled, PwHash.unapply)

  }

  // Courses

  class CoursesTable(tag: Tag) extends Table[Course](tag, "courses") {

    def id: Rep[String] = column[String](idName, O.PrimaryKey)

    def courseName: Rep[String] = column[String]("course_name")


    override def * : ProvenShape[Course] = (id, courseName) <> (Course.tupled, Course.unapply)

  }

  class UsersInCoursesTable(tag: Tag) extends Table[UserInCourse](tag, "users_in_courses") {

    def username: Rep[String] = column[String]("username")

    def courseId: Rep[String] = column[String]("course_id")

    def role: Rep[Role] = column[Role]("role")


    def pk: PrimaryKey = primaryKey("pk", (username, courseId))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

    def courseFk: ForeignKeyQuery[CoursesTable, Course] = foreignKey("course_fk", courseId, courses)(_.id)


    override def * : ProvenShape[UserInCourse] = (username, courseId, role) <> (UserInCourse.tupled, UserInCourse.unapply)

  }

  // Feedback

  class FeedbackTable(tag: Tag) extends Table[Feedback](tag, "feedback") {

    def username: Rep[String] = column[String](usernameName)

    def toolUrlPart: Rep[String] = column[String]("tool_url")

    def sense: Rep[Mark] = column[Mark]("sense")

    def used: Rep[Mark] = column[Mark]("used")

    def usability: Rep[Mark] = column[Mark]("usability")

    def feedback: Rep[Mark] = column[Mark]("feedback")

    def fairness: Rep[Mark] = column[Mark]("fairness")

    def comment: Rep[String] = column[String]("comment")


    def pk: PrimaryKey = primaryKey("pk", (username, toolUrlPart))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)


    override def * : ProvenShape[Feedback] = (username, toolUrlPart, sense, used, usability, feedback, fairness, comment) <> (FeedbackTableHelper.fromTableTupled, FeedbackTableHelper.forTableUnapplied)

  }

  // Base table for exercises and exerciseCollections

  abstract class HasBaseValuesTable[E <: HasBaseValues](tag: Tag, name: String) extends Table[E](tag, name) {

    def id: Rep[Int] = column[Int](idName)

    def title: Rep[String] = column[String]("title")

    def author: Rep[String] = column[String]("author")

    def text: Rep[String] = column[String]("ex_text")

    def state: Rep[ExerciseState] = column[ExerciseState]("ex_state")

    def semanticVersion: Rep[SemanticVersion] = column[SemanticVersion]("semantic_version")

  }

}
