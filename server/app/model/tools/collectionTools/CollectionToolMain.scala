package model.tools.collectionTools

import better.files.File
import model._
import model.core.result.{CompleteResult, EvaluationResult}
import model.tools.{AToolMain, ToolConsts}
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.libs.json.{JsPath, JsResult, JsValue, JsonValidationError}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(consts: ToolConsts) extends AToolMain(consts) {

  private val logger = Logger(classOf[CollectionToolMain])

  protected type JsErrorType = (JsPath, scala.collection.Seq[JsonValidationError])

  // Abstract types

  type ExContentType <: ExerciseContent

  type PartType <: ExPart

  type SolType

  type CompResultType <: CompleteResult[_ <: EvaluationResult]

  // Values

  val exParts: Seq[PartType]

  // Yaml, Html forms, Json

  protected val toolJsonProtocol: ToolJsonProtocol[ExContentType, SolType, CompResultType]

  protected val exerciseContentYamlFormat: YamlFormat[ExContentType]

  // Other helper methods

  protected def exerciseHasPart(exercise: ExContentType, partType: PartType): Boolean = true

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  // Correction

  private def logErrors(errors: scala.collection.Seq[JsErrorType], exceptionMsg: String): Future[Try[CompResultType]] = {
    errors.foreach(errorMsg => logger.error(errorMsg.toString))

    Future.successful(Failure(new Exception(exceptionMsg)))
  }

  def readSolution(jsValue: JsValue): JsResult[SolType] = toolJsonProtocol.solutionFormat.reads(jsValue)

  def correctAbstract(
    user: User,
    solution: SolType,
    collection: ExerciseCollection,
    exercise: Exercise,
    part: PartType,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[CompResultType]] = toolJsonProtocol.exerciseContentFormat.reads(exercise.content).fold(
    errorMsgs => logErrors(errorMsgs, "internal error..."),
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

  def onLiveCorrectionResult(result: CompResultType): JsValue = toolJsonProtocol.completeResultWrites.writes(result)

  // Helper methods for admin

  private def readYamlFile[T](fileToRead: File, format: YamlFormat[T]): Seq[Try[T]] = Try(fileToRead.contentAsString.parseYaml) match {
    case Failure(error)     => Seq(Failure(error))
    case Success(yamlValue) => yamlValue match {
      case YamlArray(yamlObjects) => yamlObjects.map(x => Try(format.read(x)))
      case _                      => ???
    }
  }

  def readCollectionsFromYaml: Seq[Try[ExerciseCollection]] =
    readYamlFile(exerciseResourcesFolder / "collections.yaml", ToolYamlProtocol.exerciseCollectionYamlFormat)

  def readExercisesFromYaml(collection: ExerciseCollection): Seq[Try[Exercise]] = {
    val filePath = exerciseResourcesFolder / s"${collection.id}-${collection.shortName}.yaml"

    val exerciseYamlFormat = ToolYamlProtocol.exerciseYamlFormat(
      exerciseContentYamlFormat,
      toolJsonProtocol.exerciseContentFormat
    )

    readYamlFile(filePath, exerciseYamlFormat)
  }

}
