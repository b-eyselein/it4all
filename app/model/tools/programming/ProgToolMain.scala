package model.tools.programming

import javax.inject._
import model.{ExerciseState, MyYamlFormat, SemanticVersion, User}
import model.points.Points
import model.core.result.CompleteResultJsonProtocol
import model.tools.programming.persistence.ProgTableDefs
import model.toolMains.{CollectionToolMain, ToolState}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object ProgToolMain {

  val standardTestCount: Int = 2

}

@Singleton
class ProgToolMain @Inject()(override val tables: ProgTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Programmierung", "programming") {

  private val logger = Logger(classOf[ProgToolMain])

  // Abstract types

  override type PartType = ProgExPart
  override type ExType = ProgExercise
  override type CollType = ProgCollection

  override type SolType = ProgSolution
  override type SampleSolType = ProgSampleSolution
  override type UserSolType = ProgUserSolution

  override type ReviewType = ProgExerciseReview

  override type ResultType = ProgEvalResult
  override type CompResultType = ProgCompleteResult

  override type Tables = ProgTableDefs

  // Other members

  override val toolState: ToolState = ToolState.ALPHA

  override val exParts: Seq[ProgExPart] = ProgExParts.values

  // Yaml, Html Forms, Json

  override protected val collectionYamlFormat: MyYamlFormat[ProgCollection] = ProgExYamlProtocol.ProgCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[ProgExercise]   = ProgExYamlProtocol.ProgExYamlFormat

  override val collectionForm    : Form[ProgCollection]     = ProgToolForms.collectionFormat
  override val exerciseForm      : Form[ProgExercise]       = ProgToolForms.exerciseFormat
  override val exerciseReviewForm: Form[ProgExerciseReview] = ProgToolForms.exerciseReviewForm

  override val sampleSolutionJsonFormat: Format[ProgSampleSolution] = ProgSolutionJsonFormat.sampleSolutionJsonFormat

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] = ProgCompleteResultJsonProtocol

  // Other helper methods

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): ProgCollection =
    ProgCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): ProgExercise = ProgExercise(
    id, SemanticVersion(0, 1, 0), title = "", author, text = "", state,
    folderIdentifier = "", functionName = "", outputType = ProgDataTypes.STRING, baseData = None,
    inputTypes = Seq[ProgInput](), sampleSolutions = Seq[ProgSampleSolution](), sampleTestData = Seq[ProgSampleTestData](), maybeClassDiagramPart = None
  )

  override def instantiateSolution(
    id: Int, exercise: ProgExercise, part: ProgExPart, solution: ProgSolution, points: Points, maxPoints: Points
  ): ProgUserSolution = ProgUserSolution(id, part, solution, points, maxPoints)

  // Db

  override def futureSampleSolutions(collId: Int, id: Int, part: ProgExPart): Future[Seq[ProgSampleSolution]] = part match {
    case ProgExParts.Implementation => tables.futureSampleSolutionsForExPart(collId, id, part)
    case _                          => Future(Seq.empty) // TODO!
  }

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: ProgExPart): Either[String, ProgSolution] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) =>

      ProgSolutionJsonFormat.progSolutionReads.reads(jsValue) match {
        case JsSuccess(solution, _) => Right(solution)
        case JsError(errors)        =>
          errors.foreach(jsErr => logger.error(jsErr.toString()))
          Left(errors.toString())
      }
  }

  override def correctEx(user: User, sol: ProgSolution, collection: ProgCollection, exercise: ProgExercise, part: ProgExPart): Future[Try[ProgCompleteResult]] =
    ProgCorrector.correct(user, sol, collection, exercise, part, toolMain = this)

  // Views

  override def renderExercise(user: User, collection: ProgCollection, exercise: ProgExercise, part: ProgExPart, maybeOldSolution: Option[ProgUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {

    // FIXME: how to get language? ==> GET param?
    val language: ProgLanguage = ProgLanguages.PYTHON_3

    part match {
      case ProgExParts.TestdataCreation =>
        val oldTestData: Seq[ProgUserTestData] = maybeOldSolution.map(_.commitedTestData).getOrElse(Seq[ProgUserTestData]())
        views.html.toolViews.programming.testDataCreation(user, collection, exercise, oldTestData, this)

      case ProgExParts.Implementation =>

        val declaration: String = maybeOldSolution.map(_.solution.implementation).getOrElse {
          exercise.sampleSolutions.find(_.language == language).map(_.base).getOrElse("")
        }

        views.html.toolViews.programming.progExercise(user, collection, exercise, declaration, ProgExParts.Implementation, this)

      case ProgExParts.ActivityDiagram =>
        // TODO: use old soluton!
        val definitionRest: String = exercise.sampleSolutions.find(_.language == language).map(_.base) match {
          case None       => ""
          case Some(base) => base.split("\n").zipWithIndex.map(si => (si._2 + 1).toString + "\t" + si._1).mkString("\n")
        }

        views.html.toolViews.umlActivity.activityDrawing(user, collection, exercise, language, definitionRest, this)
    }
  }

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[ProgExercise], isCreation: Boolean)
  //                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = ???

  override def renderEditRest(exercise: ProgExercise): Html = ???

}
