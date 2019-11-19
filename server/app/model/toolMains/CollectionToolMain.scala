package model.toolMains

import better.files.File
import model._
import model.core.CoreConsts.stdStep
import model.core._
import model.core.overviewHelpers.{SolvedStates, SolvedStatesForExerciseParts}
import model.core.result.CompleteResult
import model.persistence.ExerciseTableDefs
import model.points._
import model.tools.ToolJsonProtocol
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(tn: String, up: String)(implicit ec: ExecutionContext)
  extends AToolMain(tn, up) with CollectionToolMainDbQueries {

  private val logger = Logger(classOf[CollectionToolMain])

  // Abstract types

  type ExType <: Exercise

  type PartType <: ExPart

  type SolType

  type SampleSolType <: SampleSolution[SolType]

  type UserSolType <: UserSolution[PartType, SolType]

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

  private def takeSlice[T](collection: Seq[T], page: Int, step: Int = stdStep): Seq[T] = {
    val start = Math.max(0, (page - 1) * step)
    val end   = Math.min(page * step, collection.size)

    collection slice(start, end)
  }

  def futureUserCanSolveExPart(username: String, collId: Int, exId: Int, part: PartType): Future[Boolean] = Future.successful(true)

  def futureNumOfExesInColl(collection: ExerciseCollection): Future[Int] = tables.futureNumOfExesInColl(collection.id)

  def futureExesAndSolvedStatesForParts(user: User, collection: ExerciseCollection, page: Int, step: Int): Future[Seq[SolvedStatesForExerciseParts[PartType]]] =

    futureExercisesInColl(collection.id).flatMap { exercises =>

      val approvedExercises: Seq[ExType] = exercises.filter(_.state == ExerciseState.APPROVED)

      val exesToDisplay = takeSlice(approvedExercises, page, step)

      Future.sequence(exesToDisplay.map { ex: ExType =>

        val exPartsForExercise = exParts.filter(exerciseHasPart(ex, _))

        val futureSolvedStatesForExerciseParts = Future.sequence(exPartsForExercise.map { exPart =>
          futureUserCanSolveExPart(user.username, collection.id, ex.id, exPart).flatMap {
            case true  =>
              futureSolveStateForExercisePart(user, collection.id, ex.id, exPart).map {
                // FIXME: query solved state!
                maybeSolvedState => (exPart, maybeSolvedState getOrElse SolvedStates.NotStarted)
              }
            case false => Future.successful((exPart, SolvedStates.Locked))
          }
        }).map(_.toMap)

        futureSolvedStatesForExerciseParts.map {
          solvedStatesForExerciseParts => SolvedStatesForExerciseParts(ex, solvedStatesForExerciseParts)
        }

      })

    }

  // Correction

  def correctAbstract(user: User, collection: ExerciseCollection, exercise: ExType, part: PartType)
                     (implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[CompResultType]] = readSolution(request, part) match {
    case Left(errorMsg)  =>
      logger.error(errorMsg)
      Future.successful(Failure(SolutionTransferException))
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


  //  futureCompleteColls .map {
  //    exes => ??? // FIXME: "%YAML 1.2\n---\n" + (exes .map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  //  }

  protected def instantiateSolution(id: Int, exercise: ExType, part: PartType, solution: SolType, points: Points, maxPoints: Points): UserSolType

  // Calls

  override def indexCall: Call = ??? // controllers.coll.routes.CollectionController.index(this.urlPart)

  // Files ?!? TODO!

  def futureFilesForExercise(user: User, collId: Int, exercise: ExType, part: PartType): Future[LoadExerciseFilesMessage] =
    ??? // Future.successful(Seq.empty)

}
