package model.tools

import better.files.File
import model._
import model.core.result.AbstractCorrectionResult
import model.persistence.{DbExercise, ExerciseTableDefs}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

abstract class CollectionTool(
  val id: String,
  val name: String,
  val toolState: ToolState = ToolState.LIVE
) {

  // Folders

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / id / "solutions" / username / s"$collId" / s"$exId"

  // Abstract types

  type SolType

  type ExContentType

  type ExerciseType <: Exercise[SolType, ExContentType]

  type PartType <: ExPart

  type CompResultType <: AbstractCorrectionResult

  // Yaml, Html forms, Json, GraphQL

  val toolJsonProtocol: ToolJsonProtocol[SolType, ExContentType, ExerciseType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[SolType, ExContentType, ExerciseType, PartType]

  // Db

  def futureAllExercises(tableDefs: ExerciseTableDefs)(implicit ec: ExecutionContext): Future[Seq[Exercise[_, _]]] =
    tableDefs
      .futureExercisesForTool(this.id)
      .map(_.flatMap(convertExerciseFromDb))

  def futureExercisesInCollection(
    tableDefs: ExerciseTableDefs,
    collId: Int
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[_, _]]] =
    tableDefs
      .futureExercisesInColl(this.id, collId)
      .map(_.flatMap(convertExerciseFromDb))

  def futureExerciseById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[Exercise[_, _]]] =
    futureExerciseTypeById(tableDefs, collId, exId)

  def futureExerciseTypeById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[ExerciseType]] =
    tableDefs
      .futureExerciseById(this.id, collId, exId)
      .map(_.flatMap(convertExerciseFromDb))

  def futureUpsertExercise(
    tableDefs: ExerciseTableDefs,
    exercise: ExerciseType
  )(implicit ec: ExecutionContext): Future[Boolean] = tableDefs.futureUpsertExercise(convertExerciseToDb(exercise))

  protected def convertExerciseFromDb(dbExercise: DbExercise): Option[ExerciseType]

  protected def convertExerciseToDb(exercise: ExerciseType): DbExercise

  // Other helper methods

  def correctAbstract(
    user: User,
    sol: SolType,
    coll: ExerciseCollection,
    exercise: ExerciseType,
    part: PartType,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[CompResultType]]

}
