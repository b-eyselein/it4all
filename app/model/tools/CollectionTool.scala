package model.tools

import better.files.File
import model._
import model.core.result.AbstractCorrectionResult
import model.persistence._

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
  type ExContentType <: ExerciseContent[SolType]
  type PartType <: ExPart
  type CompResultType <: AbstractCorrectionResult

  // Yaml, Html forms, Json, GraphQL

  val toolJsonProtocol: ToolJsonProtocol[SolType, ExContentType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[SolType, ExContentType, PartType]

  // Db

  private def dbExerciseToExercise(
    dbExercise: DbExercise,
    topics: Seq[Topic],
    dbSampleSolutions: Seq[DbSampleSolution]
  ): Option[Exercise[SolType, ExContentType]] = dbExercise match {
    case DbExercise(id, collectionId, toolId, title, authors, text, difficulty, contentJson) =>
      val sampleSolutions: Seq[SampleSolution[SolType]] = dbSampleSolutions.flatMap {
        case DbSampleSolution(id, _, _, _, solutionJson) =>
          toolJsonProtocol.solutionFormat
            .reads(solutionJson)
            .asOpt
            .map(solution => SampleSolution(id, solution))
      }

      for {
        content <- toolJsonProtocol.exerciseContentFormat.reads(contentJson).asOpt
      } yield Exercise(id, collectionId, toolId, title, authors, text, topics, difficulty, content)
  }

  private def exerciseToDbExercise(
    exercise: Exercise[SolType, ExContentType]
  ): (DbExercise, Seq[DbExerciseTopic], Seq[DbSampleSolution]) =
    exercise match {
      case Exercise(id, collectionId, toolId, title, authors, text, topics, difficulty, content) =>
        val contentJson = toolJsonProtocol.exerciseContentFormat.writes(content)
        val dbEx        = DbExercise(id, collectionId, toolId, title, authors, text, difficulty, contentJson)

        val dbTopics = topics.map(t => DbExerciseTopic(t.id, id, collectionId, toolId))

        val dbSampleSolutions = content.sampleSolutions.map { ss =>
          DbSampleSolution(ss.id, id, collectionId, toolId, toolJsonProtocol.solutionFormat.writes(ss.sample))
        }

        (dbEx, dbTopics, dbSampleSolutions)
    }

  private def matchExercisesAndTopics(
    exercises: Seq[DbExercise],
    topicsForExercises: Seq[(DbExerciseTopic, Topic)],
    sampleSolutions: Seq[DbSampleSolution]
  ): Seq[Exercise[SolType, ExContentType]] = exercises.flatMap { ex =>
    dbExerciseToExercise(
      ex,
      topicsForExercises
        .filter { case (et, _) => et.exerciseId == ex.id && et.collectionId == ex.collectionId }
        .map(_._2),
      sampleSolutions
        .filter(ss => ss.exerciseId == ex.id && ss.collectionId == ex.collectionId)
    )
  }

  def futureAllExercises(
    tableDefs: ExerciseTableDefs
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[SolType, ExContentType]]] =
    for {
      dbExercises          <- tableDefs.futureAllExercisesForTool(this.id)
      topicsForDbExercises <- tableDefs.futureTopicsForAllExercisesForTool(this.id)
      sampleSolutions      <- tableDefs.futureSampleSolutionsForAllExercisesForTool(this.id)
    } yield matchExercisesAndTopics(dbExercises, topicsForDbExercises, sampleSolutions)

  def futureExercisesInCollection(
    tableDefs: ExerciseTableDefs,
    collId: Int
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[_, _]]] =
    for {
      exercises            <- tableDefs.futureAllExercisesInColl(this.id, collId)
      topicsForDbExercises <- tableDefs.futureTopicsForAllExercisesInColl(this.id, collId)
      sampleSolutions      <- tableDefs.futureSampleSolutionsForAllExercisesInColl(this.id, collId)
    } yield matchExercisesAndTopics(exercises, topicsForDbExercises, sampleSolutions)

  def futureExerciseById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[Exercise[_, _]]] = futureExerciseTypeById(tableDefs, collId, exId)

  def futureExerciseTypeById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[Exercise[SolType, ExContentType]]] =
    for {
      maybeExercise   <- tableDefs.futureExerciseById(this.id, collId, exId)
      topics          <- tableDefs.futureTopicsForExerciseById(this.id, collId, exId)
      sampleSolutions <- tableDefs.futureSampleSolutionsForExerciseById(this.id, collId, exId)
    } yield maybeExercise.flatMap(ex => dbExerciseToExercise(ex, topics, sampleSolutions))

  def futureUpsertExercise(
    tableDefs: ExerciseTableDefs,
    exercise: Exercise[SolType, ExContentType]
  )(implicit ec: ExecutionContext): Future[Boolean] = {
    val (dbExercise, dbExerciseTopics, dbSampleSolution) = exerciseToDbExercise(exercise)

    for {
      exInserted     <- tableDefs.futureUpsertExercise(dbExercise)
      topicsInserted <- tableDefs.futureUpsertTopicsForExercise(dbExerciseTopics)
      samplesSaved   <- tableDefs.futureUpsertSampleSolutionsForExercise(dbSampleSolution)
    } yield exInserted && topicsInserted && samplesSaved
  }

  // Other helper methods

  def correctAbstract(
    user: User,
    sol: SolType,
    coll: ExerciseCollection,
    exercise: Exercise[SolType, ExContentType],
    part: PartType,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[CompResultType]]

}
