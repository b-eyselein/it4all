package model.uml

import javax.inject._
import model.core._
import model.core.result.EvaluationResult
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.yaml.MyYamlFormat
import model.{Consts, ExerciseState, JsonFormat, User}
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

    val onFormError: Form[StringSolutionFormHelper] => Option[UmlSolution] = { formWithErrors =>
      formWithErrors.errors.foreach(error => Logger.error("Form Error: " + error))
      None
    }

    val onRead: StringSolutionFormHelper => Option[UmlSolution] = { sol =>
      Json.fromJson[UmlClassDiagram](Json.parse(sol.learnerSolution))(UmlClassDiagramJsonFormat.umlSolutionJsonFormat) match {
        case JsSuccess(ucd, _) => Some(UmlSolution(user.username, id, part, ucd))

        case JsError(errors) =>
          errors.foreach(error => Logger.error("Json Error: " + error))
          None
      }

    }

    SolutionFormHelper.stringSolForm.bindFromRequest.fold(onFormError, onRead)
  }

  override def readSolutionFromPutRequest(user: User, id: Int, part: UmlExPart)(implicit request: Request[AnyContent]): Option[UmlSolution] =
    request.body.asJson flatMap { jsValue =>
      Json.fromJson[UmlClassDiagram](jsValue)(UmlClassDiagramJsonFormat.umlSolutionJsonFormat) match {
        case JsSuccess(ucd, _) =>
          Some(UmlSolution(user.username, id, part, ucd))
        case JsError(errors)   =>
          errors.foreach(error => Logger.error("Json Error: " + error))
          None
      }
    }

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: UmlExPart): Option[UmlSolution] = None

  //    UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(jsValue) match {
  //      case JsSuccess(classDiag, _) => Some(UmlSolution(user.username, id, part, classDiag))
  //      case JsError(errors)         =>
  //        errors.foreach(error => Logger.error("Json Error: " + error._1 + " --> " + error._2))
  //        None
  //    }

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): UmlCompleteEx = UmlCompleteEx(
    UmlExercise(id, title = "", author = "", text = "", state, solution = UmlClassDiagram(Seq.empty, Seq.empty, Seq.empty), markedText = "", toIgnore = ""),
    mappings = Seq.empty
  )

  // Yaml

  override val yamlFormat: MyYamlFormat[UmlCompleteEx] = UmlExYamlProtocol.UmlExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: UmlCompleteEx, part: UmlExPart, maybeOldSolution: Option[UmlSolution]): Html = part match {
    case ClassSelection     => views.html.idExercises.uml.classSelection(user, exercise.ex, this)
    case DiagramDrawing     => views.html.idExercises.uml.classDiagdrawing(user, exercise, part, getsHelp = false, this)
    case DiagramDrawingHelp => views.html.idExercises.uml.classDiagdrawing(user, exercise, part, getsHelp = true, this)
    case MemberAllocation   => views.html.idExercises.uml.memberAllocation(user, exercise)
  }

  override def renderEditRest(exercise: UmlCompleteEx): Html = views.html.idExercises.uml.editUmlExRest(exercise)

  // Correction

  override def correctEx(user: User, sol: UmlSolution, exercise: UmlCompleteEx, solutionSaved: Boolean): Future[Try[UmlCompleteResult]] =
    Future(Try(new UmlCompleteResult(exercise, sol.classDiagram, solutionSaved, sol.part)))

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: UmlExPart): Future[String] = ???

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, pointsSaved: Boolean, result: UmlCompleteResult): Html = result.part match {
    case MemberAllocation =>

      println(result.learnerSolution)

      result.classResult foreach { classRes => classRes.allMatches.map(_.attributesResult) foreach println }

      views.html.idExercises.uml.memberAllocationResult(user, result, this)
    case _                => ??? // Correction is only live!
  }

  override def onLiveCorrectionResult(pointsSaved: Boolean, result: UmlCompleteResult): JsValue = result.toJson

}