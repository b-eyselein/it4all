package model.uml

import javax.inject._
import model.Enums.ToolState
import model.core._
import model.toolMains.IdExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, Enums, JsonFormat, User}
import play.api.Logger
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class UmlToolMain @Inject()(val tables: UmlTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("uml") with JsonFormat {

  // Result types

  override type ExType = UmlExercise

  override type CompExType = UmlCompleteEx

  override type Tables = UmlTableDefs

  override type PartType = UmlExPart

  override type SolType = UmlSolution

  override type R = EvaluationResult

  override type CompResult = UmlCompleteResult

  // Other members

  override val toolname: String = "Uml"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = UmlConsts

  override val exParts: Seq[UmlExPart] = UmlExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[UmlExercise] = null

  // Reading solution

  private implicit val umlSolutionJsonFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlSolutionJsonFormat

  override def readSolutionFromPostRequest(user: User, id: Int, part: UmlExPart)(implicit request: Request[AnyContent]): Option[UmlSolution] = {

    val onFormError: Form[StringSolutionFormHelper] => Option[UmlSolution] = _ => None

    val onRead: StringSolutionFormHelper => Option[UmlSolution] = { sol =>
      Json.fromJson[UmlClassDiagram](Json.parse(sol.learnerSolution)) match {
        case JsSuccess(ucd, _) => Some(UmlSolution(user.username, id, part, ucd))

        case JsError(errors) =>
          errors.foreach(error => Logger.error("Json Error: " + error))
          None
      }

    }

    SolutionFormHelper.stringSolForm.bindFromRequest.fold(onFormError, onRead)
  }

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: UmlExPart): Option[UmlSolution] = UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(jsValue) match {
    case JsSuccess(classDiag, _) => Some(UmlSolution(user.username, id, part, classDiag))
    case JsError(errors)         =>
      errors.foreach(error => Logger.error("Json Error: " + error._1 + " --> " + error._2))
      None
  }

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): UmlCompleteEx = UmlCompleteEx(
    UmlExercise(id, title = "", author = "", text = "", state, solution = UmlClassDiagram(Seq.empty, Seq.empty, Seq.empty), classSelText = "", diagDrawText = "", toIgnore = ""),
    mappings = Seq.empty
  )

  // Yaml

  override val yamlFormat: MyYamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: UmlCompleteEx, part: UmlExPart, maybeOldSolution: Option[UmlSolution]): Html = part match {
    case ClassSelection     => views.html.idExercises.uml.classSelection(user, exercise.ex)
    case DiagramDrawing     => views.html.idExercises.uml.classDiagdrawing(user, exercise, part, getsHelp = false, this)
    case DiagramDrawingHelp => views.html.idExercises.uml.classDiagdrawing(user, exercise, part, getsHelp = true, this)
    case MemberAllocation   => views.html.idExercises.uml.memberAllocation(user, exercise)
  }

  override def renderEditRest(exercise: UmlCompleteEx): Html = views.html.idExercises.uml.editUmlExRest(exercise)

  // Correction

  override def correctEx(user: User, sol: UmlSolution, exercise: UmlCompleteEx, solutionSaved: Boolean): Future[Try[UmlCompleteResult]] =
    Future(Try(new UmlCompleteResult(exercise, sol.classDiagram, solutionSaved, sol.part)))

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: UmlCompleteResult): Html = result.part match {
    case ClassSelection   => result.classResult match {
      case Some(classRes) =>
        views.html.idExercises.uml.classSelectionResult(user, result.exercise.id, result.learnerSolution.classes, classRes, this)
      case None           => Html("Es gab einen Fehler bei der Korrektur!")
    }
    case MemberAllocation => views.html.idExercises.uml.memberAllocationResult(user, result, this)
    case _                => ??? // Correction is only live!
  }

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = error match {
    case NoSuchExerciseException(_) => Html(error.getMessage)
    case NoSuchPartException(_)     => Html(error.getMessage)
    case SolutionTransferException  => Html(error.getMessage)
    case err                        =>
      Logger.error("Error while submit correction: " + err)
      err.printStackTrace()
      views.html.core.correctionError(user, OtherCorrectionException(err))
  }

  override def onLiveCorrectionResult(result: UmlCompleteResult): JsValue = result.toJson

}