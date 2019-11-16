package model.tools.programming

import javax.inject._
import model._
import model.core.result.CompleteResultJsonProtocol
import model.points.Points
import model.toolMains.{CollectionToolMain, ToolState}
import model.tools.programming.persistence.ProgTableDefs
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object ProgToolMain {

  val standardTestCount: Int = 2

}

@Singleton
class ProgToolMain @Inject()(override val tables: ProgTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Programmierung", "programming") {

  //  private val logger = Logger(classOf[ProgToolMain])

  // Abstract types

  override type PartType = ProgExPart
  override type ExType = ProgExercise

  override type SolType = ProgSolution
  override type SampleSolType = ProgSampleSolution
  override type UserSolType = ProgUserSolution

  override type ReviewType = ProgExerciseReview

  override type ResultType = ProgEvalResult
  override type CompResultType = ProgCompleteResult

  override type Tables = ProgTableDefs

  // Other members

  override val toolState: ToolState = ToolState.ALPHA

  override val exParts: Seq[ProgExPart] = ProgExParts.values

  // Yaml, Html Forms, Json

  override protected def exerciseYamlFormat: YamlFormat[ProgExercise] = ProgExYamlProtocol.programmingExerciseYamlFormat

  override val exerciseJsonFormat: Format[ProgExercise] = ProgrammingJsonProtocols.exerciseFormat

  override val exerciseReviewForm: Form[ProgExerciseReview] = ProgToolForms.exerciseReviewForm

  override val sampleSolutionJsonFormat: Format[ProgSampleSolution] = ProgrammingJsonProtocols.sampleSolutionJsonFormat

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] = ProgrammingJsonProtocols

  // Other helper methods

  override def exerciseHasPart(exercise: ProgExercise, part: ProgExPart): Boolean = part match {
    case ProgExParts.ActivityDiagram => false
    case ProgExParts.TestCreation    => exercise.unitTestPart.unitTestType == UnitTestTypes.Normal
    case _                           => true
  }

  override def instantiateSolution(id: Int, exercise: ProgExercise, part: ProgExPart, solution: ProgSolution,
                                   points: Points, maxPoints: Points): ProgUserSolution =
    ProgUserSolution(id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: ProgCompleteResult, solSaved: Boolean): ProgCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Db

  override def futureSampleSolutions(collId: Int, id: Int, part: ProgExPart): Future[Seq[ProgSampleSolution]] =
    tables.futureSampleSolutionsForExPart(collId, id, part)

  // Correction

  override def futureFilesForExercise(user: User, collId: Int, exercise: ProgExercise, part: ProgExPart): Future[LoadExerciseFilesMessage] =
    tables.futureMaybeOldSolution(user.username, collId, exercise.id, part).map {
      case None         => exercise.filesForExercisePart(part)
      case Some(oldSol) =>

        val oldMessages = exercise.filesForExercisePart(part)

        val newFiles = oldMessages.files.map { f => if (f.name == "test.py") f.copy(content = oldSol.solution.unitTest.content) else f }

        LoadExerciseFilesMessage(newFiles, oldMessages.activeFileName)
    }

  override protected def readSolution(request: Request[AnyContent], part: ProgExPart): Either[String, ProgSolution] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) =>

      ExerciseFileJsonProtocol.exerciseFileWorkspaceReads.reads(jsValue) match {
        case JsError(errors)        => Left(errors.toString())
        case JsSuccess(solution, _) => Right(ProgSolution(solution.files, Seq.empty))
      }
  }

  override def correctEx(user: User, sol: ProgSolution, collection: ExerciseCollection, exercise: ProgExercise, part: ProgExPart): Future[Try[ProgCompleteResult]] =
    ProgCorrector.correct(user, sol, collection, exercise, part, toolMain = this)

}
