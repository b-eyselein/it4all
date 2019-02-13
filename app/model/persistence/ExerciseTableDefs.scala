package model.persistence

import model.learningPath.LearningPathTableDefs
import model.{CompleteEx, Exercise, SemanticVersion}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ForeignKeyQuery

import scala.concurrent.Future

trait ExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex]] extends LearningPathTableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected type ExTableDef <: HasBaseValuesTable[Ex]

  protected val exTable: TableQuery[ExTableDef]

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

  protected def completeExForEx(ex: Ex): Future[CompEx]

  // Saving

  def futureSaveCompleteEx(compEx: CompEx): Future[Boolean] = db.run(exTable.filter(_.id === compEx.ex.id).delete) flatMap { _ =>
    db.run(exTable += compEx.ex) flatMap { _ => saveExerciseRest(compEx) } recover { case e: Throwable =>
      Logger.error("Could not save exercise", e)
      false
    }
  }

  def futureInsertCompleteEx(compEx: CompEx): Future[Boolean] = db.run(exTable += compEx.ex) flatMap {
    insertCount => saveExerciseRest(compEx)
  }

  protected def saveExerciseRest(compEx: CompEx): Future[Boolean]

  // Abstract table classes

  abstract class ExForeignKeyTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")


    def exerciseFk: ForeignKeyQuery[ExTableDef, Ex] = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))

  }

}