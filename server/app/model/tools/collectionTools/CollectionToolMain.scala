package model.tools.collectionTools

import better.files.File
import model._
import model.core.result.{CompleteResult, EvaluationResult}
import model.points._
import model.tools.{AToolMain, ToolConsts}
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(consts: ToolConsts) extends AToolMain(consts) {

  private val logger = Logger(classOf[CollectionToolMain])

  protected type JsErrorType = scala.collection.Seq[(JsPath, scala.collection.Seq[JsonValidationError])]

  // Abstract types

  type ExContentType <: ExerciseContent

  type PartType <: ExPart

  type SolType

  type UserSolType <: UserSolution[PartType, SolType]

  type CompResultType <: CompleteResult[_ <: EvaluationResult]

  // Values

  val exParts: Seq[PartType]

  // Yaml, Html forms, Json

  protected val toolJsonProtocol: ToolJsonProtocol[PartType, ExContentType, SolType, UserSolType, CompResultType]

  protected val exerciseContentYamlFormat: YamlFormat[ExContentType]

  def exerciseContentFormat: Format[ExContentType] = toolJsonProtocol.exerciseContentFormat

  def exerciseFormat: Format[Exercise] = ToolJsonProtocol.exerciseFormat

  // Other helper methods

  protected def exerciseHasPart(exercise: ExContentType, partType: PartType): Boolean = true

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  def updateSolSaved(compResult: CompResultType, solSaved: Boolean): CompResultType

  // Correction

  def correctAbstract(
    user: User,
    collection: ExerciseCollection,
    exercise: Exercise,
    part: PartType
  )(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[CompResultType]] = request.body.asJson match {
    case None          => ???
    case Some(jsValue) =>

      exerciseContentFormat.reads(exercise.content).fold(
        {
          errorMsgs: JsErrorType =>
            errorMsgs.foreach(errorMsg => logger.error(errorMsg.toString()))
            Future.successful(Failure(new Exception("TODO: internal error...")))
        },
        {
          content: ExContentType =>
            readSolution(jsValue, part)
              .fold(
                { errorMsg =>
                  logger.error(errorMsg)
                  Future.successful(Failure(new Exception("Es gab einen Fehler bei der Übertragung ihrer Lösung!")))
                },
                solution => correctEx(user, solution, collection, exercise, content, part)
              )
        }
      )
  }

  protected def correctEx(
    user: User,
    sol: SolType,
    coll: ExerciseCollection,
    exercise: Exercise,
    content: ExContentType,
    part: PartType
  )(implicit executionContext: ExecutionContext): Future[Try[CompResultType]]

  // Reading from requests

  protected def readSolution(jsValue: JsValue, part: PartType): Either[String, SolType]

  // Result handlers

  def onLiveCorrectionResult(result: CompResultType): JsValue = toolJsonProtocol.completeResultWrites.writes(result)

  def onLiveCorrectionError(error: Throwable): JsValue = Json.obj("msg" -> "Es gab einen internen Fehler bei der Korrektur!")

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

    val exerciseYamlFormat = ToolYamlProtocol.exerciseYamlFormat(exerciseContentYamlFormat, exerciseContentFormat)

    readYamlFile(filePath, exerciseYamlFormat)
  }

  protected def instantiateSolution(id: Int, exercise: Exercise, part: PartType, solution: SolType, points: Points, maxPoints: Points): UserSolType

}
