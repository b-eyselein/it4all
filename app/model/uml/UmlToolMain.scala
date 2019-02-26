package model.uml

import javax.inject._
import model._
import model.core.result.EvaluationResult
import model.toolMains.{ASingleExerciseToolMain, ToolState}
import model.uml.UmlConsts.{difficultyName, durationName}
import model.uml.persistence.UmlTableDefs
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

@Singleton
class UmlToolMain @Inject()(val tables: UmlTableDefs)(implicit ec: ExecutionContext)
  extends ASingleExerciseToolMain("Uml", "uml") {

  // Result types

  override type PartType = UmlExPart

  override type ExType = UmlExercise


  override type SolType = UmlClassDiagram

  override type SampleSolType = UmlSampleSolution

  override type UserSolType = UmlSolution


  override type Tables = UmlTableDefs

  override type ResultType = EvaluationResult

  override type CompResultType = UmlCompleteResult

  override type ReviewType = UmlExerciseReview

  // Other members

  override val toolState: ToolState = ToolState.LIVE

  override protected val exParts: Seq[UmlExPart] = UmlExParts.values

  override protected val completeResultJsonProtocol: UmlCompleteResultJsonProtocol.type = UmlCompleteResultJsonProtocol

  // Forms

  override def exerciseForm: Form[UmlExercise] = UmlExerciseForm.format

  override def exerciseReviewForm(username: String, exercise: UmlExercise, exercisePart: UmlExPart): Form[UmlExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )
    (UmlExerciseReview(username, exercise.id, exercise.semanticVersion, exercisePart, _, _))
    (uer => Some((uer.difficulty, uer.maybeDuration)))
  )

  // Reading solution

  override def readSolution(user: User, exercise: UmlExercise, part: UmlExPart)(implicit request: Request[AnyContent]): Try[UmlClassDiagram] =
    request.body.asJson match {
      case None          => Failure(new Exception("Request body does not contain json!"))
      case Some(jsValue) =>
        UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(jsValue) match {
          case JsSuccess(ucd, _) => Success(ucd)
          case JsError(errors)   =>
            errors.foreach(error => Logger.error(s"Json Error: $error"))
            Failure(new Exception(errors.map(_.toString).mkString("\n")))
        }
    }

  // Other helper methods

  override protected def exerciseHasPart(exercise: UmlExercise, partType: UmlExPart): Boolean = partType match {
    case UmlExParts.ClassSelection | UmlExParts.DiagramDrawing => true // TODO: Currently deactivated...
    case _                                                     => false
  }

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): UmlExercise = UmlExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author, text = "", state, markedText = "",
    toIgnore = Seq[String](),
    mappings = Map[String, String](),
    sampleSolutions = Seq[UmlSampleSolution](
      UmlSampleSolution(1, id, SemanticVersionHelper.DEFAULT, sample = UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]()))
    )
  )

  override def instantiateSolution(id: Int, username: String, exercise: UmlExercise, part: UmlExPart, solution: UmlClassDiagram, points: Points, maxPoints: Points): UmlSolution =
    UmlSolution(id, username, exercise.id, exercise.semanticVersion, part, solution, points, maxPoints)

  // Yaml

  override val yamlFormat: MyYamlFormat[UmlExercise] = UmlExYamlProtocol.UmlExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: UmlExercise, part: UmlExPart, maybeOldSolution: Option[UmlSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = part match {
    case UmlExParts.ClassSelection     => views.html.idExercises.uml.classSelection(user, exercise, this)
    case UmlExParts.DiagramDrawing     => views.html.idExercises.uml.classDiagdrawing(user, exercise, part, getsHelp = false, this)
    case UmlExParts.DiagramDrawingHelp => views.html.idExercises.uml.classDiagdrawing(user, exercise, part, getsHelp = true, this)
    case UmlExParts.MemberAllocation   => views.html.idExercises.uml.memberAllocation(user, exercise, this)
  }

  override def renderEditRest(exercise: UmlExercise): Html = views.html.idExercises.uml.editUmlExRest(exercise)

  override def renderUserExerciseEditForm(user: User, newExForm: Form[UmlExercise], isCreation: Boolean)
                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.uml.editUmlExerciseForm(user, newExForm, isCreation, this)

  // Correction

  override def correctEx(user: User, classDiagram: UmlClassDiagram, exercise: UmlExercise, part: UmlExPart): Future[Try[UmlCompleteResult]] =
    Future(Try(new UmlCompleteResult(exercise, classDiagram, part)))

}
