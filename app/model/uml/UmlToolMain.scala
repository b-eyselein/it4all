package model.uml

import javax.inject._
import model.Enums.ToolState
import model.core._
import model.toolMains.IdExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, Enums, JsonFormat, User}
import play.api.data.Form
import play.api.libs.json.{JsValue, Json}
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

  override type CompResult = UmlResult

  // Other members

  override val toolname: String = "Uml"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = UmlConsts

  override val exParts: Seq[UmlExPart] = UmlExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[UmlExercise] = null

  // Reading solution

  override def futureSaveSolution(sol: UmlSolution): Future[Boolean] = ???

  override def readSolutionFromPostRequest(user: User, id: Int, part: UmlExPart)(implicit request: Request[AnyContent]): Option[UmlSolution] = {
    SolutionFormHelper.stringSolForm.bindFromRequest.fold(_ => None,
      sol => UmlSolutionJsonFormat.readUmlSolutionFromJson(user.username, id, part, Json.parse(sol.learnerSolution))
    )
  }

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: UmlExPart): Option[UmlSolution] = ???

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): UmlCompleteEx = UmlCompleteEx(
    UmlExercise(id, title = "", author = "", text = "", state, classSelText = "", diagDrawText = "", toIgnore = ""),
    mappings = Seq.empty, classes = Seq.empty, associations = Seq.empty, implementations = Seq.empty
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

  private def renderResult(corResult: UmlResult): Html = {

    val resultsRender: String = corResult.notEmptyMatchingResults map (_.describe) mkString "\n"

    val nextPartLink: String = corResult.nextPart match {
      case Some(np) =>
        a(href := controllers.routes.ExerciseController.exercise("uml", corResult.exercise.ex.id, np.urlName).url, cls := "btn btn-primary btn-block")("Zum nächsten Aufgabenteil").toString
      case None     =>
        a(href := controllers.routes.ExerciseController.index("uml").url, cls := "btn btn-primary btn-block")("Zurück zur Startseite").toString
    }

    Html(resultsRender + "<hr>" + nextPartLink)
  }

  override def renderEditRest(exercise: UmlCompleteEx): Html = views.html.uml.editUmlExRest(exercise)

  // Correction

  override def correctEx(user: User, sol: UmlSolution, exercise: UmlCompleteEx): Future[Try[UmlResult]] = Future {
    Try {
      sol.part match {
        case ClassSelection     => new ClassSelectionResult(exercise, sol)
        case DiagramDrawing     => new DiagramDrawingResult(exercise, sol)
        case DiagramDrawingHelp => new DiagramDrawingHelpResult(exercise, sol)
        case MemberAllocation   => new AllocationResult(exercise, sol)
      }
    }
  }

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: UmlResult): Html = views.html.core.correction.render(result, renderResult(result), user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = error match {
    case NoSuchExerciseException(_) => Html(error.getMessage)
    case NoSuchPartException(_)     => Html(error.getMessage)
    case SolutionTransferException  => Html(error.getMessage)
    case err                        => views.html.core.correctionError.render(user, OtherCorrectionException(err))
  }

  override def onLiveCorrectionResult(result: UmlResult): JsValue = ??? // Ok(renderResult(result))

}