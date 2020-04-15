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

  type ExerciseType <: Exercise

  type PartType <: ExPart

  type SolType

  type CompResultType <: AbstractCorrectionResult

  // Yaml, Html forms, Json, GraphQL

  val toolJsonProtocol: ToolJsonProtocol[ExerciseType, SolType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[ExerciseType, SolType, PartType]

  // Db

  def futureAllExercises(tableDefs: ExerciseTableDefs)(implicit ec: ExecutionContext): Future[Seq[Exercise]] =
    tableDefs
      .futureExercisesForTool(this.id)
      .map(_.map(convertExercise))

  def futureExercisesInCollection(
    tableDefs: ExerciseTableDefs,
    collId: Int
  )(implicit ec: ExecutionContext): Future[Seq[Exercise]] =
    tableDefs
      .futureExercisesInColl(this.id, collId)
      .map(_.map(convertExercise))

  def futureExerciseById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[Exercise]] =
    futureExerciseTypeById(tableDefs, collId, exId)

  def futureExerciseTypeById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[ExerciseType]] =
    tableDefs
      .futureExerciseById(this.id, collId, exId)
      .map(_.map(convertExercise))

  protected def convertExercise(dbExercise: DbExercise): ExerciseType = ???

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
