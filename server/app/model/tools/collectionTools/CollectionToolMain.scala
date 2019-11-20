package model.toolMains

import better.files.File
import model._
import model.core.result.{CompleteResult, EvaluationResult}
import model.persistence.ExerciseTableDefs
import model.points._
import model.tools.ToolJsonProtocol
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(consts: ToolConsts)
  extends AToolMain(consts)
    with CollectionToolMainDbQueries {

  private val logger = Logger(classOf[CollectionToolMain])

  // Abstract types

  type ExType <: Exercise

  type PartType <: ExPart

  type SolType

  type SampleSolType <: SampleSolution[SolType]

  type UserSolType <: UserSolution[PartType, SolType]

  type ResultType <: EvaluationResult

  type CompResultType <: CompleteResult[ResultType]

  type ReviewType <: ExerciseReview

  override type Tables <: ExerciseTableDefs[PartType, ExType, SolType, SampleSolType, UserSolType, ReviewType]

  // Values

  val exParts: Seq[PartType]

  // Yaml, Html forms, Json

  protected val toolJsonProtocol: ToolJsonProtocol[ExType, SampleSolType, CompResultType]

  protected val collectionYamlFormat: YamlFormat[ExerciseCollection] =
    ExerciseCollectionYamlProtocol.exerciseCollectionYamlFormat

  protected val exerciseYamlFormat: YamlFormat[ExType]

  def exerciseJsonFormat: Format[ExType] = toolJsonProtocol.exerciseFormat

  def sampleSolutionJsonFormat: Format[SampleSolType] = toolJsonProtocol.sampleSolutionFormat

  val exerciseReviewForm: Form[ReviewType]

  // Other helper methods

  protected def exerciseHasPart(exercise: ExType, partType: PartType): Boolean = true

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  def updateSolSaved(compResult: CompResultType, solSaved: Boolean): CompResultType

  // Db

  def futureUserCanSolveExPart(username: String, collId: Int, exId: Int, part: PartType): Future[Boolean] = Future.successful(true)

  def futureNumOfExesInColl(collection: ExerciseCollection): Future[Int] = tables.futureNumOfExesInColl(collection.id)

  // Correction

  def correctAbstract(user: User, collection: ExerciseCollection, exercise: ExType, part: PartType)
                     (implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[CompResultType]] = readSolution(request, part) match {
    case Left(errorMsg)  =>
      logger.error(errorMsg)
      Future.successful(Failure(new Exception("Es gab einen Fehler bei der Übertragung ihrer Lösung!")))
    case Right(solution) =>

      correctEx(user, solution, collection, exercise, part).flatMap {
        case Failure(error) => Future.successful(Failure(error))
        case Success(res)   =>

          // FIXME: points != 0? maxPoints != 0?
          val dbSol = instantiateSolution(id = -1, exercise, part, solution, res.points, res.maxPoints)
          tables.futureSaveUserSolution(exercise.id, exercise.semanticVersion, collection.id, user.username, dbSol).map {
            solSaved => Success(updateSolSaved(res, solSaved))
          }
      }
  }

  protected def correctEx(user: User, sol: SolType, coll: ExerciseCollection, exercise: ExType, part: PartType): Future[Try[CompResultType]]

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
    readYamlFile(exerciseResourcesFolder / "collections.yaml", ExerciseCollectionYamlProtocol.exerciseCollectionYamlFormat)

  def readExercisesFromYaml(collection: ExerciseCollection): Seq[Try[ExType]] =
    readYamlFile(exerciseResourcesFolder / s"${collection.id}-${collection.shortName}.yaml", exerciseYamlFormat)

  protected def instantiateSolution(id: Int, exercise: ExType, part: PartType, solution: SolType, points: Points, maxPoints: Points): UserSolType

  // Files ?!? TODO!

  def futureFilesForExercise(user: User, collId: Int, exercise: ExType, part: PartType): Future[LoadExerciseFilesMessage] =
    ??? // Future.successful(Seq.empty)

}
