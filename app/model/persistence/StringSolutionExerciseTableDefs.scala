package model.persistence

import model._
import model.core.CoreConsts.{sampleName, solutionName}
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.Future

trait StringSolutionExerciseTableDefs[PartType <: ExPart, ExType <: Exercise, CollType <: ExerciseCollection, ReviewType <: ExerciseReview]
  extends ExerciseTableDefs[PartType, ExType, CollType, String, StringSampleSolution, StringUserSolution[PartType], ReviewType] {

  import profile.api._

  // Abstract types

  override protected type DbSampleSolType = DbStringSampleSolution

  override protected type DbSampleSolTable <: AStringSampleSolutionsTable


  override protected type DbUserSolType = DbStringUserSolution[PartType]

  override protected type DbUserSolTable <: AUserSolutionsTable

  // Table Queries

  override protected val userSolutionsTableQuery: TableQuery[DbUserSolTable]

  // Helpers

  override protected val solutionDbModels = new StringSolutionDbModels[PartType]()

  override protected def copyDbUserSolType(oldSol: DbStringUserSolution[PartType], newId: Int): DbStringUserSolution[PartType] = oldSol.copy(id = newId)

  // Queries

  protected def futureSamplesForExercise(collId: Int, exId: Int): Future[Seq[StringSampleSolution]] =
    db.run(sampleSolutionsTableQuery
      .filter { sample => sample.collectionId === collId && sample.exerciseId === exId }
      .result
      .map(_ map solutionDbModels.sampleSolFromDbSampleSol))

  override def futureSampleSolutionsForExPart(scenarioId: Int, exerciseId: Int, exPart: PartType): Future[Seq[String]] =
    db.run(sampleSolutionsTableQuery
      .filter { e => e.collectionId === scenarioId && e.exerciseId === exerciseId }
      .map(_.sample)
      .result)

  // Abstract Tables

  abstract class AStringSampleSolutionsTable(tag: Tag, tableName: String) extends ASampleSolutionsTable(tag, tableName) {

    def sample: Rep[String] = column[String](sampleName)


    override def * : ProvenShape[DbStringSampleSolution] = (id, exerciseId, exSemVer, collectionId, sample) <> (DbStringSampleSolution.tupled, DbStringSampleSolution.unapply)

  }

  abstract class AStringUserSolutionsTable(tag: Tag, tableName: String) extends AUserSolutionsTable(tag, "regex_solutions") {

    def solution: Rep[String] = column[String](solutionName)


    def pk: PrimaryKey = primaryKey("pk", id)


    override def * : ProvenShape[DbStringUserSolution[PartType]] = (id, exerciseId, exSemVer, collectionId, username, part,
      solution, points, maxPoints) <> (DbStringUserSolution.tupledWithPart[PartType], DbStringUserSolution.unapplyWithPart[PartType])

  }

}
