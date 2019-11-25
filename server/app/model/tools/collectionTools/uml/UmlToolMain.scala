package model.tools.collectionTools.uml

import model.User
import model.core.result.EvaluationResult
import model.points.Points
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, ToolJsonProtocol}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, JsValue}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object UmlToolMain extends CollectionToolMain(UmlConsts) {

  private val logger = Logger(UmlToolMain.getClass)

  // Result types

  override type PartType = UmlExPart
  override type ExContentType = UmlExerciseContent

  override type SolType = UmlClassDiagram
  override type UserSolType = UmlUserSolution

  override type CompResultType = UmlCompleteResult

  // Other members

  override val exParts: Seq[UmlExPart] = UmlExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[UmlExPart, UmlExerciseContent, UmlClassDiagram, UmlUserSolution, UmlCompleteResult] =
    UmlToolJsonProtocol

  override protected val exerciseContentYamlFormat: YamlFormat[UmlExerciseContent] = UmlExYamlProtocol.umlExerciseYamlFormat

  // Other helper methods

  override protected def exerciseHasPart(exercise: UmlExerciseContent, partType: UmlExPart): Boolean = partType match {
    case UmlExParts.ClassSelection | UmlExParts.DiagramDrawing => true // TODO: Currently deactivated...
    case _                                                     => false
  }

  override def instantiateSolution(id: Int, exercise: Exercise, part: UmlExPart, solution: UmlClassDiagram, points: Points, maxPoints: Points): UmlUserSolution =
    UmlUserSolution(id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: UmlCompleteResult, solSaved: Boolean): UmlCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Correction

  override def readSolution(jsValue: JsValue, part: UmlExPart): Either[String, UmlClassDiagram] =
    UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat.reads(jsValue) match {
      case JsSuccess(ucd, _) => Right(ucd)
      case JsError(errors)   =>
        errors.foreach(error => logger.error(s"Json Error: $error"))
        Left(errors.toString())
    }

  override def correctEx(
    user: User, classDiagram: UmlClassDiagram, collection: ExerciseCollection, exercise: Exercise, content: UmlExerciseContent, part: UmlExPart
  )(implicit executionContext: ExecutionContext): Future[Try[UmlCompleteResult]] = Future.successful {
    content.sampleSolutions.headOption match {
      case None                 => Failure(new Exception("There is no sample solution!"))
      case Some(sampleSolution) => Success(UmlCorrector.correct(classDiagram, sampleSolution.sample, part))
    }
  }

}
