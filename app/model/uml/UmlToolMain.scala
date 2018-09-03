package model.uml

import javax.inject._
import model._
import model.core.result.EvaluationResult
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.yaml.MyYamlFormat
import play.api.Logger
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class UmlToolMain @Inject()(val tables: UmlTableDefs)(implicit ec: ExecutionContext)
  extends IdExerciseToolMain("Uml", "uml") {

  // Result types

  override type ExType = UmlExercise

  override type CompExType = UmlCompleteEx

  override type Tables = UmlTableDefs

  override type PartType = UmlExPart

  override type SolType = UmlClassDiagram

  override type DBSolType = UmlSolution

  override type R = EvaluationResult

  override type CompResult = UmlCompleteResult

  // Other members

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = UmlConsts

  override val exParts: Seq[UmlExPart] = UmlExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[UmlExercise] = null

  // Reading solution

  override def readSolution(user: User, exercise: UmlCompleteEx, part: UmlExPart)(implicit request: Request[AnyContent]): Option[UmlClassDiagram] =
    request.body.asJson flatMap { jsValue =>
      Json.fromJson[UmlClassDiagram](jsValue)(UmlClassDiagramJsonFormat.umlSolutionJsonFormat) match {
        case JsSuccess(ucd, _) =>
          Some(ucd)
        case JsError(errors)   =>
          errors.foreach(error => Logger.error("Json Error: " + error))
          None
      }
    }

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): UmlCompleteEx = UmlCompleteEx(
    UmlExercise(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state,
      solution = UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]()),
      markedText = "", toIgnore = ""),
    mappings = Seq[UmlMapping]()
  )

  override def instantiateSolution(username: String, exercise: UmlCompleteEx, part: UmlExPart, solution: UmlClassDiagram, points: Points, maxPoints: Points): UmlSolution =
    UmlSolution(username, exercise.ex.id, exercise.ex.semanticVersion, part, solution, points, maxPoints)

  // Yaml

  override val yamlFormat: MyYamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: UmlCompleteEx, part: UmlExPart, maybeOldSolution: Option[UmlSolution]): Html = part match {
    case UmlExParts.ClassSelection     => views.html.idExercises.uml.classSelection(user, exercise.ex, this)
    case UmlExParts.DiagramDrawing     => views.html.idExercises.uml.classDiagdrawing(user, exercise, part, getsHelp = false, this)
    case UmlExParts.DiagramDrawingHelp => views.html.idExercises.uml.classDiagdrawing(user, exercise, part, getsHelp = true, this)
    case UmlExParts.MemberAllocation   => views.html.idExercises.uml.memberAllocation(user, exercise)
  }

  override def renderEditRest(exercise: UmlCompleteEx): Html = views.html.idExercises.uml.editUmlExRest(exercise)

  // Correction

  override def correctEx(user: User, classDiagram: UmlClassDiagram, exercise: UmlCompleteEx, part: UmlExPart): Future[Try[UmlCompleteResult]] =
    Future(Try(new UmlCompleteResult(exercise, classDiagram, part)))

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: UmlExPart): Future[String] = ???

}