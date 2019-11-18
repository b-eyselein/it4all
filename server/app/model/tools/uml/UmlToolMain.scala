package model.tools.uml

import javax.inject.{Inject, Singleton}
import model.core.result.EvaluationResult
import model.points.Points
import model.toolMains.{CollectionToolMain, ToolState}
import model.tools.ToolJsonProtocol
import model.tools.uml.persistence.UmlTableDefs
import model.{ExerciseCollection, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsError, JsSuccess}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

@Singleton
class UmlToolMain @Inject()(val tables: UmlTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Uml - Klassendiagramme", "uml") {

  private val logger = Logger(classOf[UmlToolMain])

  // Result types

  override type PartType = UmlExPart
  override type ExType = UmlExercise


  override type SolType = UmlClassDiagram
  override type SampleSolType = UmlSampleSolution
  override type UserSolType = UmlUserSolution

  override type ReviewType = UmlExerciseReview

  override type ResultType = EvaluationResult
  override type CompResultType = UmlCompleteResult

  override type Tables = UmlTableDefs

  // Other members

  override val toolState: ToolState = ToolState.BETA

  override val exParts: Seq[UmlExPart] = UmlExParts.values


  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[UmlExercise, UmlSampleSolution, UmlCompleteResult] =
    UmlToolJsonProtocol

  override protected val exerciseYamlFormat: YamlFormat[UmlExercise] = UmlExYamlProtocol.umlExerciseYamlFormat

  override val exerciseReviewForm: Form[UmlExerciseReview] = UmlToolForms.exerciseReviewForm

  // Other helper methods

  override protected def exerciseHasPart(exercise: UmlExercise, partType: UmlExPart): Boolean = partType match {
    case UmlExParts.ClassSelection | UmlExParts.DiagramDrawing => true // TODO: Currently deactivated...
    case _                                                     => false
  }

  override def instantiateSolution(id: Int, exercise: UmlExercise, part: UmlExPart, solution: UmlClassDiagram, points: Points, maxPoints: Points): UmlUserSolution =
    UmlUserSolution(id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: UmlCompleteResult, solSaved: Boolean): UmlCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Correction

  override def readSolution(request: Request[AnyContent], part: UmlExPart): Either[String, UmlClassDiagram] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) =>

      UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat.reads(jsValue) match {
        case JsSuccess(ucd, _) => Right(ucd)
        case JsError(errors)   =>
          errors.foreach(error => logger.error(s"Json Error: $error"))
          Left(errors.toString())
      }
  }

  override def correctEx(
    user: User, classDiagram: UmlClassDiagram, collection: ExerciseCollection, exercise: UmlExercise, part: UmlExPart
  ): Future[Try[UmlCompleteResult]] = Future.successful {
    exercise.sampleSolutions.headOption match {
      case None                 => Failure(new Exception("There is no sample solution!"))
      case Some(sampleSolution) => Success(UmlCorrector.correct(classDiagram, sampleSolution.sample, part))
    }
  }

}
