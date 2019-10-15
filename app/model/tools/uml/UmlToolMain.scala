package model.tools.uml

import javax.inject.{Inject, Singleton}
import model.core.result.{CompleteResultJsonProtocol, EvaluationResult}
import model.points.Points
import model.toolMains.{CollectionToolMain, ToolState}
import model.tools.uml.persistence.UmlTableDefs
import model.{ExerciseState, MyYamlFormat, SemanticVersionHelper, User}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.{Format, JsError, JsSuccess}
import play.api.mvc._
import play.twirl.api.Html

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
  override type CollType = UmlCollection


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

  override val collectionYamlFormat: MyYamlFormat[UmlCollection] = UmlExYamlProtocol.UmlCollectionYamlFormat
  override val exerciseYamlFormat  : MyYamlFormat[UmlExercise]   = UmlExYamlProtocol.UmlExYamlFormat

  override val collectionJsonFormat: Format[UmlCollection]                                     = UmlCompleteResultJsonProtocol.collectionFormat
  override val exerciseJsonFormat  : Format[UmlExercise] = UmlCompleteResultJsonProtocol.exerciseFormat

  override val collectionForm    : Form[UmlCollection]     = UmlToolForms.collectionFormat
  override val exerciseForm      : Form[UmlExercise]       = UmlToolForms.exerciseFormat
  override val exerciseReviewForm: Form[UmlExerciseReview] = UmlToolForms.exerciseReviewForm

  override val sampleSolutionJsonFormat: Format[UmlSampleSolution] = UmlCompleteResultJsonProtocol.umlSampleSolutionFormat

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[EvaluationResult, UmlCompleteResult] = UmlCompleteResultJsonProtocol

  // Other helper methods

  override protected def exerciseHasPart(exercise: UmlExercise, partType: UmlExPart): Boolean = partType match {
    case UmlExParts.ClassSelection | UmlExParts.DiagramDrawing => true // TODO: Currently deactivated...
    case _                                                     => false
  }

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): UmlCollection =
    UmlCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): UmlExercise = UmlExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author, text = "", state, markedText = "",
    toIgnore = Seq.empty[String],
    mappings = Map.empty[String, String],
    sampleSolutions = Seq[UmlSampleSolution](
      UmlSampleSolution(1, sample = UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]()))
    )
  )

  override def instantiateSolution(id: Int, exercise: UmlExercise, part: UmlExPart, solution: UmlClassDiagram, points: Points, maxPoints: Points): UmlUserSolution =
    UmlUserSolution(id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: UmlCompleteResult, solSaved: Boolean): UmlCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Views

  override def renderExercise(user: User, collection: UmlCollection, exercise: UmlExercise, part: UmlExPart, maybeOldSolution: Option[UmlUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = part match {
    case UmlExParts.ClassSelection     =>
      views.html.toolViews.uml.classSelection(user, collection, exercise, this)
    case UmlExParts.DiagramDrawing     =>
      views.html.toolViews.uml.classDiagdrawing(user, collection, exercise, part, getsHelp = false, this)
    case UmlExParts.DiagramDrawingHelp =>
      views.html.toolViews.uml.classDiagdrawing(user, collection, exercise, part, getsHelp = true, this)
    case UmlExParts.MemberAllocation   =>
      views.html.toolViews.uml.memberAllocation(user, collection, exercise, this)
  }

  override def renderEditRest(exercise: UmlExercise): Html = views.html.toolViews.uml.editUmlExRest(exercise)

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
    user: User, classDiagram: UmlClassDiagram, collection: UmlCollection, exercise: UmlExercise, part: UmlExPart
  ): Future[Try[UmlCompleteResult]] = Future.successful {
    exercise.sampleSolutions.headOption match {
      case None                 => Failure(new Exception("There is no sample solution!"))
      case Some(sampleSolution) => Success(UmlCorrector.correct(classDiagram, sampleSolution.sample, part))
    }
  }

}
