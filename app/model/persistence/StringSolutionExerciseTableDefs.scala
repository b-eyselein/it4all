package model.persistence

import model._
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.Future

trait StringSolutionExerciseTableDefs[PartType <: ExPart, ExType <: Exercise, CollType <: ExerciseCollection, SampleSolType <: SampleSolution[String], UserSolType <: UserSolution[PartType, String], ReviewType <: ExerciseReview]
  extends HasDatabaseConfigProvider[JdbcProfile] {
  self: ExerciseTableDefs[PartType, ExType, CollType, String, SampleSolType, UserSolType, ReviewType] =>

  import profile.api._

  // Abstract types

  protected type DbSampleSolType = DbStringSampleSolution

  override protected type DbSampleSolTable <: AStringSampleSolutionsTable


  protected type DbUserSolType = DbStringUserSolution[PartType]

  override protected type DbUserSolTable <: AUserSolutionsTable

  // Table Queries

  protected val samplesTableQuery: TableQuery[DbSampleSolTable]

  // Helpers

  override protected val solutionDbModels = new StringSolutionDbModels[PartType]()

  override protected def copyDbUserSolType(oldSol: DbStringUserSolution[PartType], newId: Int): DbStringUserSolution[PartType] = oldSol.copy(id = newId)

  // Queries

  protected def futureSamplesForExercise(collId: Int, exId: Int): Future[Seq[StringSampleSolution]] =
    db.run(samplesTableQuery
      .filter { sample => sample.collectionId === collId && sample.exerciseId === exId }
      .result
      .map(_ map solutionDbModels.sampleSolFromDbSampleSol))

  override def futureSampleSolutionsForExPart(scenarioId: Int, exerciseId: Int, exPart: PartType): Future[Seq[String]] =
    db.run(samplesTableQuery
      .filter { e => e.collectionId === scenarioId && e.exerciseId === exerciseId }
      .map(_.sample)
      .result)

  // Abstract Tables

  abstract class AStringSampleSolutionsTable(tag: Tag, tableName: String) extends ASampleSolutionsTable(tag, tableName) {

    override def * : ProvenShape[DbStringSampleSolution] = (id, exerciseId, exSemVer, collectionId, sample) <> (DbStringSampleSolution.tupled, DbStringSampleSolution.unapply)

  }

  abstract class AStringUserSolutionsTable(tag: Tag, tableName: String) extends AUserSolutionsTable(tag, "regex_solutions") {

    def pk: PrimaryKey = primaryKey("pk", id)


    override def * : ProvenShape[DbStringUserSolution[PartType]] = (id, exerciseId, exSemVer, collectionId, username, part,
      solution, points, maxPoints) <> (DbStringUserSolution.tupledWithPart[PartType], DbStringUserSolution.unapplyWithPart[PartType])

  }

}
