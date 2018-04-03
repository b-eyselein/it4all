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
import scalatags.Text.all._

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

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: UmlExPart): Option[UmlSolution] = ???

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): UmlCompleteEx = UmlCompleteEx(
    UmlExercise(id, title = "", author = "", text = "", state, solution = UmlClassDiagram(Seq.empty, Seq.empty, Seq.empty),
      classSelText = "", diagDrawText = "", toIgnore = ""),
    mappings = Seq.empty
  )

  // Yaml

  override val yamlFormat: MyYamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: UmlCompleteEx, part: UmlExPart, maybeOldSolution: Option[UmlSolution]): Html = part match {
    case ClassSelection     => views.html.uml.classSelection(user, exercise.ex)
    case DiagramDrawing     => views.html.uml.diagdrawing(user, exercise, getsHelp = false)
    case DiagramDrawingHelp => views.html.uml.diagdrawing(user, exercise, getsHelp = true)
    case MemberAllocation   => views.html.uml.allocation(user, exercise)
  }

  private def renderResult(corResult: UmlCompleteResult): Html = {

    val resultsRender: String = corResult.notEmptyMatchingResults map (_.describe) mkString "\n"

    val nextPartLink: String = corResult.nextPart match {
      case Some(np) =>
        a(href := controllers.routes.ExerciseController.exercise(this.urlPart, corResult.exercise.ex.id, np.urlName).url, cls := "btn btn-primary btn-block")("Zum nächsten Aufgabenteil").toString
      case None     =>
        a(href := controllers.routes.ExerciseController.index(this.urlPart).url, cls := "btn btn-primary btn-block")("Zurück zur Startseite").toString
    }

    Html(resultsRender + "<hr>" + nextPartLink)
  }

  override def renderEditRest(exercise: UmlCompleteEx): Html = views.html.uml.editUmlExRest(exercise)

  // Correction

  override def correctEx(user: User, sol: UmlSolution, exercise: UmlCompleteEx, solutionSaved: Boolean): Future[Try[UmlCompleteResult]] =
    Future(UmlCorrector.correct(exercise, sol, solutionSaved))

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: UmlCompleteResult): Html =
    views.html.core.correction.render(result, renderResult(result), user, toolMainObject = this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = error match {
    case NoSuchExerciseException(_) => Html(error.getMessage)
    case NoSuchPartException(_)     => Html(error.getMessage)
    case SolutionTransferException  => Html(error.getMessage)
    case err                        => views.html.core.correctionError.render(user, OtherCorrectionException(err))
  }

  override def onLiveCorrectionResult(result: UmlCompleteResult): JsValue = ??? // Ok(renderResult(result))

}