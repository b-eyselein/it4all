package model.uml

import javax.inject._
import model._
import model.core.result.{CompleteResultJsonProtocol, EvaluationResult}
import model.toolMains.{CollectionToolMain, ToolState}
import model.uml.persistence.UmlTableDefs
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

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

  override val toolState: ToolState = ToolState.LIVE

  override protected val exParts: Seq[UmlExPart] = UmlExParts.values


  // Yaml, Html forms, Json

  override val collectionYamlFormat: MyYamlFormat[UmlCollection] = UmlExYamlProtocol.UmlCollectionYamlFormat
  override val exerciseYamlFormat  : MyYamlFormat[UmlExercise]   = UmlExYamlProtocol.UmlExYamlFormat

  override val collectionForm    : Form[UmlCollection]     = UmlExerciseForm.collectionFormat
  override val exerciseForm      : Form[UmlExercise]       = UmlExerciseForm.exerciseFormat
  override val exerciseReviewForm: Form[UmlExerciseReview] = UmlExerciseForm.exerciseReviewForm

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
    toIgnore = Seq[String](),
    mappings = Map[String, String](),
    sampleSolutions = Seq[UmlSampleSolution](
      UmlSampleSolution(1, sample = UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]()))
    )
  )

  override def instantiateSolution(id: Int, exercise: UmlExercise, part: UmlExPart, solution: UmlClassDiagram, points: Points, maxPoints: Points): UmlUserSolution =
    UmlUserSolution(id, part, solution, points, maxPoints)

  // Views

  override def renderExercise(user: User, collection: UmlCollection, exercise: UmlExercise, part: UmlExPart, maybeOldSolution: Option[UmlUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = part match {
    case UmlExParts.ClassSelection     => views.html.idExercises.uml.classSelection(user, collection, exercise, this)
    case UmlExParts.DiagramDrawing     => views.html.idExercises.uml.classDiagdrawing(user, collection, exercise, part, getsHelp = false, this)
    case UmlExParts.DiagramDrawingHelp => views.html.idExercises.uml.classDiagdrawing(user, collection, exercise, part, getsHelp = true, this)
    case UmlExParts.MemberAllocation   => views.html.idExercises.uml.memberAllocation(user, collection, exercise, this)
  }

  override def renderEditRest(exercise: UmlExercise): Html = views.html.idExercises.uml.editUmlExRest(exercise)

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[UmlExercise], isCreation: Boolean)
  //                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.uml.editUmlExerciseForm(user, newExForm, isCreation, this)

  // Correction

  override def readSolution(user: User, collection: UmlCollection, exercise: UmlExercise, part: UmlExPart)
                           (implicit request: Request[AnyContent]): Option[UmlClassDiagram] =
    request.body.asJson match {
      case None          =>
        logger.error("Request body does not contain json!")
        None
      case Some(jsValue) =>
        UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(jsValue) match {
          case JsSuccess(ucd, _) => Some(ucd)
          case JsError(errors)   =>
            errors.foreach(error => logger.error(s"Json Error: $error"))
            None
        }
    }

  override def correctEx(user: User, classDiagram: UmlClassDiagram, collection: UmlCollection, exercise: UmlExercise, part: UmlExPart): Future[Try[UmlCompleteResult]] =
    Future.successful {
      Try(new UmlCompleteResult(exercise, classDiagram, part))
    }

}
