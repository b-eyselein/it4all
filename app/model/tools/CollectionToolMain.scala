package model.tools

import better.files.File
import model._
import model.core.result.AbstractCorrectionResult
import play.api.Logger
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

abstract class CollectionToolMain(val id: String, val name: String, val toolState: ToolState = ToolState.LIVE) {

  // Other members

  val hasTags: Boolean       = false
  val hasPlayground: Boolean = false

  // Folders

  // protected val exerciseResourcesFolder: File = File.currentWorkingDirectory / "conf" / "resources" / urlPart

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / id / "solutions" / username / String.valueOf(collId) / String.valueOf(
      exId
    )

  private val logger = Logger(classOf[CollectionToolMain])

  protected type JsErrorType = (JsPath, scala.collection.Seq[JsonValidationError])

  // Abstract types

  type ExContentType <: ExerciseContent

  type PartType <: ExPart

  type SolType

  type CompResultType <: AbstractCorrectionResult

  // Values

  val exParts: Seq[PartType]

  // Yaml, Html forms, Json, GraphQL

  val toolJsonProtocol: ToolJsonProtocol[ExContentType, SolType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[ExContentType, SolType, PartType]

  // Other helper methods

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  // Correction

  def solutionFormat: Format[SolType] = toolJsonProtocol.solutionFormat

  private def logErrors(
    errors: scala.collection.Seq[JsErrorType],
    exceptionMsg: String
  ): Future[Try[CompResultType]] = {
    errors.foreach(errorMsg => logger.error(errorMsg.toString))

    Future.successful(Failure(new Exception(exceptionMsg)))
  }

  def readExerciseContent(exercise: Exercise): JsResult[ExContentType] =
    toolJsonProtocol.exerciseContentFormat.reads(exercise.content)

  @deprecated
  def readSolution(jsValue: JsValue): JsResult[SolType] = toolJsonProtocol.solutionFormat.reads(jsValue)

  def correctAbstract(
    user: User,
    solution: SolType,
    collection: ExerciseCollection,
    exercise: Exercise,
    part: PartType,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[CompResultType]] = readExerciseContent(exercise).fold(
    errorMsgs => logErrors(errorMsgs, "Internal error: Could not read exercise content..."),
    exerciseContent => correctEx(user, solution, collection, exercise, exerciseContent, part, solutionSaved)
  )

  protected def correctEx(
    user: User,
    sol: SolType,
    coll: ExerciseCollection,
    exercise: Exercise,
    content: ExContentType,
    part: PartType,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[CompResultType]]

  // Result handlers

//  def onLiveCorrectionResult(result: CompResultType): JsValue = toolJsonProtocol.completeResultWrites.writes(result)

}
