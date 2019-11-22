package model.tools.collectionTools

import better.files.File
import model._
import model.core.result.{CompleteResult, EvaluationResult}
import model.points._
import model.tools.{AToolMain, ToolConsts}
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(consts: ToolConsts) extends AToolMain(consts) {

  private val logger = Logger(classOf[CollectionToolMain])

  // Abstract types

  type ExContentType <: ExerciseContent

  type PartType <: ExPart

  type SolType

  type SampleSolType <: SampleSolution[SolType]

  type UserSolType <: UserSolution[PartType, SolType]

  type ResultType <: EvaluationResult

  type CompResultType <: CompleteResult[ResultType]

  // Values

  val exParts: Seq[PartType]

  // Yaml, Html forms, Json

  protected val toolJsonProtocol: ToolJsonProtocol[PartType, ExContentType, SolType, SampleSolType, UserSolType, CompResultType]

  protected val exerciseContentYamlFormat: YamlFormat[ExContentType]

  def exerciseContentFormat: Format[ExContentType] = toolJsonProtocol.exerciseContentFormat

  def exerciseFormat: Format[Exercise] = ToolJsonProtocol.exerciseFormat

  def sampleSolutionJsonFormat: Format[SampleSolType] = toolJsonProtocol.sampleSolutionFormat

  // Other helper methods

  protected def exerciseHasPart(exercise: ExContentType, partType: PartType): Boolean = true

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  def updateSolSaved(compResult: CompResultType, solSaved: Boolean): CompResultType

  // Correction

  def correctAbstract(user: User, collection: ExerciseCollection, exercise: Exercise, part: PartType)
                     (implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[CompResultType]] = {

    val onError: String => Future[Try[CompResultType]] = { errorMsg =>
      logger.error(errorMsg)
      Future.successful(Failure(new Exception("Es gab einen Fehler bei der Übertragung ihrer Lösung!")))
    }

    val onRead: SolType => Future[Try[CompResultType]] = { solution =>

      val content: ExContentType = ???

      correctEx(user, solution, collection, exercise, content, part).flatMap {
        case Failure(error) => Future.successful(Failure(error))
        case Success(res)   =>

          // FIXME: points != 0? maxPoints != 0?
          val dbSol = instantiateSolution(id = -1, exercise, part, solution, res.points, res.maxPoints)

          //FIXME: save solution!

          //          tables.futureSaveUserSolution(exercise.id, exercise.semanticVersion, collection.id, user.username, dbSol).map {
          //            solSaved => Success(updateSolSaved(res, solSaved))
          //          }
          Future.successful(dbSol)
          ???
      }
    }

    readSolution(request, part).fold(onError, onRead)
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

  protected def readSolution(request: Request[AnyContent], part: PartType): Either[String, SolType]

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

  // Files ?!? TODO!

  def futureFilesForExercise(user: User, collId: Int, exercise: ExContentType, part: PartType): Future[LoadExerciseFilesMessage] =
    ??? // Future.successful(Seq.empty)

}
