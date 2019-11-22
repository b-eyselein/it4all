package model.tools.collectionTools.programming

import model._
import model.points.Points
import model.tools.collectionTools._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object ProgToolMain extends CollectionToolMain(ProgConsts) {

  // Abstract types

  override type PartType = ProgExPart
  override type ExContentType = ProgExerciseContent

  override type SolType = ProgSolution
  override type SampleSolType = ProgSampleSolution
  override type UserSolType = ProgUserSolution

  override type ResultType = ProgEvalResult
  override type CompResultType = ProgCompleteResult

  // Other members

  override val exParts: Seq[ProgExPart] = ProgExParts.values

  // Yaml, Html Forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[ProgExPart, ProgExerciseContent, ProgSolution, ProgSampleSolution, ProgUserSolution, ProgCompleteResult] =
    ProgrammingToolJsonProtocol

  override protected val exerciseContentYamlFormat: YamlFormat[ProgExerciseContent] = ProgExYamlProtocol.programmingExerciseYamlFormat

  // Other helper methods

  override def exerciseHasPart(exercise: ProgExerciseContent, part: ProgExPart): Boolean = part match {
    case ProgExParts.ActivityDiagram => false
    case ProgExParts.TestCreation    => exercise.unitTestPart.unitTestType == UnitTestTypes.Normal
    case _                           => true
  }

  override def instantiateSolution(
    id: Int, exercise: Exercise, part: ProgExPart, solution: ProgSolution, points: Points, maxPoints: Points
  ): ProgUserSolution = ProgUserSolution(id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: ProgCompleteResult, solSaved: Boolean): ProgCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Db

  override protected def readSolution(request: Request[AnyContent], part: ProgExPart): Either[String, ProgSolution] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) =>

      ExerciseFileJsonProtocol.exerciseFileWorkspaceReads.reads(jsValue) match {
        case JsError(errors)        => Left(errors.toString())
        case JsSuccess(solution, _) => Right(ProgSolution(solution.files, Seq.empty))
      }
  }

  override protected def correctEx(
    user: User, sol: ProgSolution, collection: ExerciseCollection, exercise: Exercise, content: ProgExerciseContent, part: ProgExPart
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] =
    ProgCorrector.correct(user, sol, collection, exercise, content, part, exerciseResourcesFolder)

}
