package model.tools

import better.files.File
import model._
import model.core.result.AbstractCorrectionResult
import model.persistence._
import play.api.libs.json.{JsArray, JsString, JsValue}

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

  private def dbExerciseToExercise(dbExercise: DbExercise): Option[Exercise[SolType, ExContentType]] =
    dbExercise match {
      case DbExercise(
          id,
          collectionId,
          toolId,
          title,
          authors,
          text,
          difficulty,
          topicAbbreviationsJson,
          contentJson
          ) =>
        for {
          content            <- toolJsonProtocol.exerciseContentFormat.reads(contentJson).asOpt
          topicAbbreviations <- topicAbbreviationsJson.validate[Seq[String]].asOpt
        } yield Exercise(id, collectionId, toolId, title, authors, text, topicAbbreviations, difficulty, content)
    }

  private def exerciseToDbExercise(exercise: Exercise[SolType, ExContentType]): DbExercise =
    exercise match {
      case Exercise(id, collectionId, toolId, title, authors, text, topics, difficulty, content) =>
        val contentJson = toolJsonProtocol.exerciseContentFormat.writes(content)

        val topicAbbreviationsJson: JsValue = JsArray(topics.map(JsString))

        DbExercise(id, collectionId, toolId, title, authors, text, difficulty, topicAbbreviationsJson, contentJson)
    }

  def futureAllExercises(
    tableDefs: ExerciseTableDefs
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[SolType, ExContentType]]] =
    for {
      dbExercises <- tableDefs.futureAllExercisesForTool(this.id)
    } yield dbExercises.flatMap(dbExerciseToExercise)

  def futureExercisesInCollection(
    tableDefs: ExerciseTableDefs,
    collId: Int
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[_, _ <: ExerciseContent[_]]]] =
    for {
      dbExercises <- tableDefs.futureAllExercisesInColl(this.id, collId)
    } yield dbExercises.flatMap(dbExerciseToExercise)

  def futureExerciseById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[Exercise[_, _ <: ExerciseContent[_]]]] =
    futureExerciseTypeById(tableDefs, collId, exId)

  def futureExerciseTypeById(
    tableDefs: ExerciseTableDefs,
    collId: Int,
    exId: Int
  )(implicit ec: ExecutionContext): Future[Option[Exercise[SolType, ExContentType]]] =
    for {
      maybeExercise <- tableDefs.futureExerciseById(this.id, collId, exId)
    } yield maybeExercise.flatMap(dbExerciseToExercise)

  def futureUpsertExercise(
    tableDefs: ExerciseTableDefs,
    exercise: Exercise[SolType, ExContentType]
  )(implicit ec: ExecutionContext): Future[Boolean] =
    for {
      exInserted <- tableDefs.futureUpsertExercise(exerciseToDbExercise(exercise))
    } yield exInserted

  // Other helper methods

  def correctAbstract(
    user: User,
    solution: SolType,
    exercise: Exercise[SolType, ExContentType],
    part: PartType,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[CompResultType]]

  val allTopics: Seq[Topic] = Seq.empty

}
