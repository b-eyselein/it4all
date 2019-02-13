package model.persistence

import model.learningPath.LearningPathTableDefs
import model.{CompleteEx, Exercise, HasBaseValues, SemanticVersion}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ForeignKeyQuery

import scala.concurrent.Future

trait ExerciseTableDefs[CompEx <: CompleteEx[_ <: Exercise]] extends LearningPathTableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  protected type ExDbValues <: HasBaseValues

  protected type ExTableDef <: HasBaseValuesTable[ExDbValues]

  // Table Queries

  protected val exTable: TableQuery[ExTableDef]

  // Helper methods

  protected def exDbValuesFromCompleteEx(compEx: CompEx): ExDbValues

  // Numbers

  def futureNumOfExes: Future[Int] = db.run(exTable.distinctOn(_.semanticVersion).length.result)

  // Reading

  def futureCompleteExes: Future[Seq[CompEx]] = db.run(exTable.result) flatMap (exes => Future.sequence(exes map completeExForEx))

  def futureCompleteExById(id: Int): Future[Option[CompEx]] = db.run {
    exTable.filter(_.id === id).sortBy(_.semanticVersion.desc).result.headOption
  } flatMap {
    case Some(ex) => completeExForEx(ex) map Some.apply
    case None     => Future.successful(None)
  }

  def futureCompleteExByIdAndVersion(id: Int, semVer: SemanticVersion): Future[Option[CompEx]] = db.run {
    exTable.filter(e => e.id === id && e.semanticVersion === semVer).result.headOption
  } flatMap {
    case Some(ex) => completeExForEx(ex) map Some.apply
    case None     => Future.successful(None)
  }

  protected def completeExForEx(ex: ExDbValues): Future[CompEx]

  // Saving

  def futureInsertCompleteEx(compEx: CompEx): Future[Boolean] = db.run(exTable += exDbValuesFromCompleteEx(compEx)) flatMap {
    insertCount => saveExerciseRest(compEx)
  }

  protected def saveExerciseRest(compEx: CompEx): Future[Boolean]

  // Abstract table classes

  abstract class ExForeignKeyTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")


    def exerciseFk: ForeignKeyQuery[ExTableDef, ExDbValues] = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))

  }

}