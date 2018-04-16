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

  override def readSolutionFromPostRequest(user: User, id: Int, part: UmlExPart)(implicit request: Request[AnyContent]): Option[UmlSolution] = {

    val onFormError: Form[StringSolutionFormHelper] => Option[UmlSolution] = _ => None

    val onRead: StringSolutionFormHelper => Option[UmlSolution] = { sol =>
      UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(sol.learnerSolution)) match {
        case JsSuccess(ucd, _) => Some(UmlSolution(user.username, id, part, ucd))

        case JsError(errors) =>
          errors.foreach(error => Logger.error("Json Error: " + error))
          None
      }
    }

    SolutionFormHelper.stringSolForm.bindFromRequest.fold(onFormError, onRead)
  }

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: UmlExPart): Option[UmlSolution] = {
    println(Json.prettyPrint(jsValue))

    UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(jsValue) match {
      case JsSuccess(classDiag, _) => Some(UmlSolution(user.username, id, part, classDiag))
      case JsError(errors)         =>
        errors.foreach(error => Logger.error("Json Error: " + error._1 + " --> " + error._2))
        None
    }
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
    case ClassSelection     => views.html.uml.classSelection(user, exercise.ex)
    case DiagramDrawing     => views.html.uml.classDiagdrawing(user, exercise, part, getsHelp = false, this)
    case DiagramDrawingHelp => views.html.uml.classDiagdrawing(user, exercise, part, getsHelp = true, this)
    case MemberAllocation   => views.html.uml.memberAllocation(user, exercise)
  }

  //  private def renderResult(user: User, corResult: UmlCompleteResult): Html = {
  //
  //    val resultsRender: String = corResult.results map (_.describe) mkString "\n"
  //
  //    val nextPartLink: String = corResult.nextPart match {
  //      case Some(np) => s"""<a href="${controllers.routes.ExerciseController.exercise(this.urlPart, corResult.exercise.ex.id, np.urlName).url}" class="btn btn-primary btn-block">Zum nächsten Aufgabenteil</a>"""
  //      case None     => s"""<a href=${controllers.routes.ExerciseController.index(this.urlPart).url}" class="btn btn-primary btn-block">Zurück zur Startseite</a>"""
  //    }
  //
  //    Html(resultsRender + "<hr>" + nextPartLink)
  //  }

  override def renderEditRest(exercise: UmlCompleteEx): Html = views.html.uml.editUmlExRest(exercise)

  // Correction

  override def correctEx(user: User, sol: UmlSolution, exercise: UmlCompleteEx, solutionSaved: Boolean): Future[Try[UmlCompleteResult]] =
    Future(Try(new UmlCompleteResult(exercise, sol.classDiagram, solutionSaved, sol.part)))

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: UmlCompleteResult): Html = result.part match {
    case ClassSelection   => result.classResult match {
      case Some(classRes) =>
        views.html.uml.classSelectionResult(user, result.exercise.id, result.learnerSolution.classes, classRes, this)
      case None           => Html("Es gab einen Fehler bei der Korrektur!")
    }
    case MemberAllocation => views.html.uml.memberAllocationResult(user, result, this)
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