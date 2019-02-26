package model.persistence

import model.core.CoreConsts._
import model.learningPath.LearningPathTableDefs
import model.{ExPart, Exercise, HasBaseValues, Points, SampleSolution, SemanticVersion, Solution, User, UserSolution}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ForeignKeyQuery

import scala.concurrent.Future

trait ExerciseTableDefs[CompEx <: Exercise, PartType <: ExPart, SolType, SampleSolType <: SampleSolution[SolType], UserSolType <: UserSolution[PartType, SolType]] extends LearningPathTableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  protected type ExDbValues <: HasBaseValues

  protected type ExTableDef <: HasBaseValuesTable[ExDbValues]

  protected type SolTableDef <: AUserSolutionsTable

  // Table Queries

  protected val exTable: TableQuery[ExTableDef]

  protected val solTable: TableQuery[SolTableDef]

  // Helper methods

  protected def exDbValuesFromExercise(compEx: CompEx): ExDbValues

  // Numbers

  def futureNumOfExes: Future[Int] = db.run(exTable.distinctOn(_.semanticVersion).length.result)

  // Reading

  def futureAllExes: Future[Seq[CompEx]] = db.run(exTable.result) flatMap (exes => Future.sequence(exes map completeExForEx))

  def futureExerciseById(id: Int): Future[Option[CompEx]] = db.run {
    exTable.filter(_.id === id).sortBy(_.semanticVersion.desc).result.headOption
  } flatMap {
    case Some(ex) => completeExForEx(ex) map Some.apply
    case None     => Future.successful(None)
  }

  def futureExerciseByIdAndVersion(id: Int, semVer: SemanticVersion): Future[Option[CompEx]] = db.run {
    exTable.filter(e => e.id === id && e.semanticVersion === semVer).result.headOption
  } flatMap {
    case Some(ex) => completeExForEx(ex) map Some.apply
    case None     => Future.successful(None)
  }

  protected def completeExForEx(ex: ExDbValues): Future[CompEx]

  // Saving

  def futureInsertExercise(compEx: CompEx): Future[Boolean]

  //  = {
  //    val deleteOldExQuery = exTable.filter{
  //      dbEx: ExTableDef => dbEx.id === compEx.id && dbEx.semanticVersion === compEx.semanticVersion
  //    }.delete
  //    val insertNewExQuery = exTable += exDbValuesFromExercise(compEx)
  //
  //    db.run(deleteOldExQuery) flatMap { _ =>
  //      db.run(insertNewExQuery) flatMap {
  //        insertCount: Int => saveExerciseRest(compEx)
  //      }
  //    }
  //  }

  protected def saveExerciseRest(compEx: CompEx): Future[Boolean]

  // Implicit column types

  protected implicit val partTypeColumnType: BaseColumnType[PartType]

  // Abstract table classes

  abstract class ExForeignKeyTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")


    def exerciseFk: ForeignKeyQuery[ExTableDef, ExDbValues] = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))

  }

  protected abstract class ASolutionsTable[S <: Solution[SolType]](tag: Tag, name: String) extends ExForeignKeyTable[S](tag, name) {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  }

  protected abstract class ASampleSolutionsTable(tag: Tag, name: String) extends ASolutionsTable[SampleSolType](tag, name) {

    def sample: Rep[String] = column[String](sampleName)

  }

  protected abstract class AUserSolutionsTable(tag: Tag, name: String) extends ASolutionsTable[UserSolType](tag, name) {

    def username: Rep[String] = column[String]("username")

    def part: Rep[PartType] = column[PartType]("part")

    def points = column[Points]("points")

    def maxPoints: Rep[Points] = column[Points]("max_points")


    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

  }

}
