package model.tools

import better.files.File
import model._
import model.core.result.AbstractCorrectionResult
import model.persistence.{ADbModels, DbExercise, DbExerciseTopic, ExerciseTableDefs}

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
  type PartType <: ExPart
  type CompResultType <: AbstractCorrectionResult

  // Yaml, Html forms, Json, GraphQL

  val toolJsonProtocol: ToolJsonProtocol[SolType, ExContentType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[SolType, ExContentType, PartType]

  // Db

  private def matchExercisesAndTopics(
    exercises: Seq[DbExercise],
    topicsForExercises: Seq[(DbExerciseTopic, Topic)]
  ): Seq[Exercise] = exercises.map { ex =>
    ADbModels.dbExercisetoExercise(
      ex,
      topicsForExercises
        .filter { case (et, _) => et.exerciseId == ex.id && et.collectionId == ex.collectionId }
        .map(_._2)
    )
  }

  def futureAllExercises(
    tableDefs: ExerciseTableDefs
  )(implicit ec: ExecutionContext): Future[Seq[Exercise]] =
    for {
      dbExercises          <- tableDefs.futureAllExercisesForTool(this.id)
      topicsForDbExercises <- tableDefs.futureTopicsForAllExercisesForTool(this.id)
    } yield matchExercisesAndTopics(dbExercises, topicsForDbExercises)

  def futureExercisesInCollection(
    tableDefs: ExerciseTableDefs,
    collId: Int
  )(implicit ec: ExecutionContext): Future[Seq[Exercise]] =
    for {
      exercises            <- tableDefs.futureAllExercisesInColl(this.id, collId)
      topicsForDbExercises <- tableDefs.futureTopicsForAllExercisesInColl(this.id, collId)
    } yield matchExercisesAndTopics(exercises, topicsForDbExercises)

  def futureExerciseById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[Exercise]] =
    for {
      maybeExercise <- tableDefs.futureExerciseById(this.id, collId, exId)
      topics        <- tableDefs.futureTopicsForExerciseById(this.id, collId, exId)
    } yield maybeExercise.map(ex => ADbModels.dbExercisetoExercise(ex, topics))

  def futureUpsertExercise(
    tableDefs: ExerciseTableDefs,
    exercise: Exercise
  )(implicit ec: ExecutionContext): Future[Boolean] = {
    val (dbExercise, dbExerciseTopics) = ADbModels.exerciseToDbExercise(exercise)

    for {
      exInserted     <- tableDefs.futureUpsertExercise(dbExercise)
      topicsInserted <- tableDefs.futureUpsertTopicsForExercise(dbExerciseTopics)
    } yield exInserted && topicsInserted
  }

  // Other helper methods

  def correctAbstract(
    user: User,
    sol: SolType,
    coll: ExerciseCollection,
    exercise: Exercise,
    exerciseContent: ExContentType,
    sampleSolutions: Seq[SampleSolution[SolType]],
    part: PartType,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[CompResultType]]

}
