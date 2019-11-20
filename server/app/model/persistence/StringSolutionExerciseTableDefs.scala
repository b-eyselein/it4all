package model.persistence

import model._
import model.core.CoreConsts.solutionName
import model.tools.collectionTools.{ExPart, Exercise}
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.Future

trait StringSolutionExerciseTableDefs[PartType <: ExPart, ExType <: Exercise, ReviewType <: ExerciseReview]
  extends ExerciseTableDefs[PartType, ExType, String, StringSampleSolution, StringUserSolution[PartType], ReviewType] {

  import profile.api._

  // Abstract types

  override protected type DbUserSolType = DbStringUserSolution[PartType]

  override protected type DbUserSolTable <: AUserSolutionsTable

  // Table Queries

  override protected val userSolutionsTableQuery: TableQuery[DbUserSolTable]

  // Helpers

  override protected def copyDbUserSolType(oldSol: DbStringUserSolution[PartType], newId: Int): DbStringUserSolution[PartType] = oldSol.copy(id = newId)

  // Queries

  override def futureMaybeOldSolution(username: String, collId: Int, exerciseId: Int, part: PartType): Future[Option[StringUserSolution[PartType]]] =
    db.run(userSolutionsTableQuery
      .filter { us => us.username === username && us.collectionId === collId && us.exerciseId === exerciseId && us.part === part }
      .sortBy(_.id.desc)
      .result.headOption)
      .map(_.map(StringSolutionDbModels.userSolFromDbUserSol))

  override def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: StringUserSolution[PartType]): Future[Boolean] =
    nextUserSolutionId(exId, collId, username, sol.part).flatMap { nextUserSolId =>
      val dbUserSolution = StringSolutionDbModels.dbUserSolFromUserSol(exId, exSemVer, collId, username, sol).copy(id = nextUserSolId)
      db.run(userSolutionsTableQuery += dbUserSolution).transform(_ == 1, identity)
    }

  // Column Types

  protected val stringSampleSolutionColumnType: BaseColumnType[Seq[StringSampleSolution]] =
    jsonSeqColumnType(StringSampleSolutionJsonProtocol.stringSampleSolutionJsonFormat)

  // Abstract Tables

  abstract class AStringUserSolutionsTable(tag: Tag, tableName: String) extends AUserSolutionsTable(tag, tableName) {

    def solution: Rep[String] = column[String](solutionName)


    def pk: PrimaryKey = primaryKey("pk", id)


    override def * : ProvenShape[DbStringUserSolution[PartType]] = (id, exerciseId, exSemVer, collectionId, username, part,
      solution, points, maxPoints) <> (DbStringUserSolution.tupledWithPart[PartType], DbStringUserSolution.unapplyWithPart[PartType])

  }

}
