package model.tools

import better.files.File
import model._
import model.core.result.AbstractCorrectionResult
import model.persistence.{DbExercise, DbExerciseTopic, ExerciseTableDefs}
import play.api.libs.json.Writes

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

  private def matchExercisesAndTopics(
    dbExercises: Seq[DbExercise],
    topicsForDbExercises: Seq[(DbExerciseTopic, Topic)]
  ): Seq[Exercise[_, _]] = dbExercises.flatMap { ex =>
    val topicsForExercise = topicsForDbExercises
      .filter { case (et, _) => et.exerciseId == ex.id && et.collectionId == ex.collectionId }
      .map(_._2)

    convertExerciseFromDb(ex, topicsForExercise)
  }

  def futureAllExercises(tableDefs: ExerciseTableDefs)(implicit ec: ExecutionContext): Future[Seq[Exercise[_, _]]] =
    for {
      dbExercises          <- tableDefs.futureAllExercisesForTool(this.id)
      topicsForDbExercises <- tableDefs.futureTopicsForAllExercisesForTool(this.id)
    } yield matchExercisesAndTopics(dbExercises, topicsForDbExercises)

  def futureExercisesInCollection(
    tableDefs: ExerciseTableDefs,
    collId: Int
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[_, _]]] =
    for {
      dbExercises          <- tableDefs.futureAllExercisesInColl(this.id, collId)
      topicsForDbExercises <- tableDefs.futureTopicsForAllExercisesInColl(this.id, collId)
    } yield matchExercisesAndTopics(dbExercises, topicsForDbExercises)

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
    for {
      maybeDbExercise     <- tableDefs.futureExerciseById(this.id, collId, exId)
      topicsForDbExercise <- tableDefs.futureTopicsForExerciseById(this.id, collId, exId)
    } yield maybeDbExercise.flatMap(dbExercise => convertExerciseFromDb(dbExercise, topicsForDbExercise))

  def futureUpsertExercise(
    tableDefs: ExerciseTableDefs,
    exercise: ExerciseType
  )(implicit ec: ExecutionContext): Future[Boolean] = {
    val (dbExercise, dbExerciseTopics) = convertExerciseToDb(exercise)

    for {
      exInserted     <- tableDefs.futureUpsertExercise(dbExercise)
      topicsInserted <- tableDefs.futureUpsertTopicsForExercise(dbExerciseTopics)
    } yield exInserted && topicsInserted
  }

  protected def convertExerciseFromDb(dbExercise: DbExercise, topics: Seq[Topic]): Option[ExerciseType]

  private def convertExerciseToDb(ex: ExerciseType): (DbExercise, Seq[DbExerciseTopic]) = {
    val dbEx = DbExercise(
      ex.id,
      ex.collectionId,
      ex.toolId,
      ex.title,
      ex.authors,
      ex.text,
      ex.difficulty,
      Writes.seq(toolJsonProtocol.sampleSolutionFormat).writes(ex.sampleSolutions),
      toolJsonProtocol.exerciseContentFormat.writes(ex.content)
    )

    val dbExerciseTopics = ex.topics.map { topic =>
      DbExerciseTopic(topic.id, ex.id, ex.collectionId, ex.toolId)
    }

    (dbEx, dbExerciseTopics)
  }

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
