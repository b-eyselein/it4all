package model

import com.github.t3hnar.bcrypt._
import model.Enums.{ExerciseState, Role, ShowHideAggregate}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import model.core.CoreConsts.ID_NAME

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

case class User(username: String, pwHash: String, stdRole: Role = Role.RoleUser, todo: ShowHideAggregate = ShowHideAggregate.SHOW) {
  val isAdmin: Boolean = stdRole ne Role.RoleUser
}

case class Course(id: Int, courseName: String)

object TippHelper {

  val ran = new Random

  val StdTipp = "Hier werden in Zukunft Tipps & Tricks zur Benutzung von it4all präsentiert."

  def getRandom: Tipp = Tipp(-1, StdTipp)

}


case class Tipp(id: Int, str: String)

trait ExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex]] extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  type ExTableDef <: HasBaseValuesTable[Ex]

  val exTable: TableQuery[ExTableDef]

  // Numbers

  def futureNumOfExes: Future[Int] = db.run(exTable.length.result)

  // Reading

  def futureCompleteExes(implicit ec: ExecutionContext): Future[Seq[CompEx]] = db.run(exTable.result) flatMap (exes => Future.sequence(exes map completeExForEx))

  def futureCompleteExById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompEx]] = db.run(exTable.filter(_.id === id).result.headOption) flatMap {
    case Some(ex) => completeExForEx(ex) map Some.apply
    case None     => Future(None)
  }

  protected def completeExForEx(ex: Ex)(implicit ec: ExecutionContext): Future[CompEx]

  // Saving

  def saveCompleteEx(compEx: CompEx)(implicit ec: ExecutionContext): Future[Boolean] = db.run(exTable.filter(_.id === compEx.id).delete) flatMap { _ =>
    db.run(exTable += compEx.ex) flatMap { _ => saveExerciseRest(compEx) } recover { case _: Exception => false }
  }

  protected def saveExerciseRest(compEx: CompEx)(implicit ec: ExecutionContext): Future[Boolean]

  // Deletion

  def deleteExercise(id: Int): Future[Int] = db.run(exTable.filter(_.id === id).delete)

}

trait ExerciseCollectionTableDefs[Ex <: ExerciseInCollection, CompEx <: CompleteEx[Ex], Coll <: ExerciseCollection[Ex, CompEx], CompColl <: CompleteCollection[Ex, CompEx, Coll]] extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  type ExTableDef <: ExerciseInCollectionTable[Ex]

  type CollTableDef <: HasBaseValuesTable[Coll]

  val exTable: TableQuery[ExTableDef]

  val collTable: TableQuery[CollTableDef]

  // Numbers

  def futureNumOfExes: Future[Int] = db.run(exTable.length.result)

  def futureNumOfCollections: Future[Int] = db.run(collTable.length.result)

  // Reading

  def futureCompleteExes(implicit ec: ExecutionContext): Future[Seq[CompEx]] = db.run(exTable.result) flatMap (exes => Future.sequence(exes map completeExForEx))

  def futureCompleteExById(collId: Int, id: Int)(implicit ec: ExecutionContext): Future[Option[CompEx]] =
    db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).result.headOption) flatMap {
      case Some(ex) => completeExForEx(ex) map Some.apply
      case None     => Future(None)
    }

  def futureColls: Future[Seq[Coll]] = db.run(collTable.result)

  def futureCollById(id: Int): Future[Option[Coll]] = db.run(collTable.filter(_.id === id).result.headOption)

  def futureCompleteColls(implicit ec: ExecutionContext): Future[Seq[CompColl]] = futureColls flatMap (colls => Future.sequence(colls map completeCollForColl))

  def futureCompleteCollById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompColl]] = futureCollById(id) flatMap {
    case Some(coll) => completeCollForColl(coll) map Some.apply
    case None       => Future(None)
  }

  protected def completeExForEx(ex: Ex)(implicit ec: ExecutionContext): Future[CompEx]

  protected def completeCollForColl(coll: Coll)(implicit ec: ExecutionContext): Future[CompColl]

  // Saving

  // Deletion

  def deleteExercise(collId: Int, id: Int): Future[Int] = db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).delete)

  def deleteColl(id: Int): Future[Int] = db.run(collTable.filter(_.id === id).delete)

}

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
    toSave => save(toSave) map (_ => true) recover { case e: Exception => false }
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

  abstract class ExerciseInCollectionTable[E <: ExerciseInCollection](tag: Tag, name: String) extends HasBaseValuesTable[E](tag, name) {

    def collectionId = column[Int]("collection_id")

  }

}
