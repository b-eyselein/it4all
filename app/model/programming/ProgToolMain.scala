package model.programming

import javax.inject._
import model._
import model.core.result.CompleteResultJsonProtocol
import model.programming.persistence.ProgTableDefs
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

  override val toolState: ToolState = ToolState.BETA

  override protected val exParts: Seq[ProgExPart] = ProgExParts.values

  // Yaml, Html Forms, Json

  override protected val collectionYamlFormat: MyYamlFormat[ProgCollection] = ProgExYamlProtocol.ProgCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[ProgExercise]   = ProgExYamlProtocol.ProgExYamlFormat

  override val collectionForm    : Form[ProgCollection]     = ProgExerciseForm.collectionFormat
  override val exerciseForm      : Form[ProgExercise]       = ProgExerciseForm.exerciseFormat
  override val exerciseReviewForm: Form[ProgExerciseReview] = ProgExerciseForm.exerciseReviewForm

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] = ProgCompleteResultJsonProtocol

  // Other helper methods

  override protected def exerciseHasPart(exercise: ProgExercise, partType: ProgExPart): Boolean = partType match {
    case ProgExParts.Implementation   => true
    case ProgExParts.ActivityDiagram  => true
    case ProgExParts.TestdataCreation =>
      // TODO: Creation of test data is currently disabled
      false
  }

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): ProgCollection =
    ProgCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): ProgExercise = ProgExercise(
    id, SemanticVersion(0, 1, 0), title = "", author, text = "", state,
    folderIdentifier = "", functionName = "", outputType = ProgDataTypes.STRING, baseData = None,
    inputTypes = Seq[ProgInput](), sampleSolutions = Seq[ProgSampleSolution](), sampleTestData = Seq[ProgSampleTestData](), maybeClassDiagramPart = None
  )

  override def instantiateSolution(id: Int, exercise: ProgExercise, part: ProgExPart,
                                   solution: ProgSolution, points: Points, maxPoints: Points): ProgUserSolution =
    ProgUserSolution(id, part, solution, solution.language, solution.extendedUnitTests, points, maxPoints)

  // Db

  override def futureSampleSolutions(collId: Int, id: Int, part: ProgExPart): Future[Seq[String]] = part match {
    case ProgExParts.Implementation => tables.futureSampleSolutionsForExPart(collId, id, part)
    case _                          => Future(Seq.empty) // TODO!
  }

  // Correction

  override protected def readSolution(user: User, collection: ProgCollection, exercise: ProgExercise, part: ProgExPart)
                                     (implicit request: Request[AnyContent]): Option[ProgSolution] =
    request.body.asJson match {
      case None          =>
        logger.error("Request does not contain json!")
        None
      case Some(jsValue) =>
        ProgSolutionJsonFormat(exercise, user).readProgSolutionFromJson(part, jsValue) match {
          case JsSuccess(solution, _) => Some(solution)
          case JsError(errors)        =>
            errors.foreach(jsErr => logger.error(jsErr.toString()))
            None
        }
    }

  override def correctEx(user: User, sol: ProgSolution, collection: ProgCollection, exercise: ProgExercise, part: ProgExPart): Future[Try[ProgCompleteResult]] =
    ProgCorrector.correct(user, sol, exercise, toolMain = this)

  // Views

  override def renderExercise(user: User, collection: ProgCollection, exercise: ProgExercise, part: ProgExPart, maybeOldSolution: Option[ProgUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {

    // FIXME: how to get language? ==> GET param?
    val language: ProgLanguage = ProgLanguages.PYTHON_3

    part match {
      case ProgExParts.TestdataCreation =>
        val oldTestData: Seq[ProgUserTestData] = maybeOldSolution.map(_.commitedTestData).getOrElse(Seq[ProgUserTestData]())
        views.html.idExercises.programming.testDataCreation(user, collection, exercise, oldTestData, this)

      case ProgExParts.Implementation =>

        val declaration: String = maybeOldSolution map (_.solution) map {
          case _: ProgTestDataSolution => ""
          case pss: ProgStringSolution => pss.solution
        } getOrElse {
          exercise.sampleSolutions find (_.language == language) map (_.base) getOrElse ""
        }

        views.html.idExercises.programming.progExercise(user, collection, exercise, declaration, ProgExParts.Implementation, this)

      case ProgExParts.ActivityDiagram =>
        // TODO: use old soluton!
        val definitionRest: String = exercise.sampleSolutions.find(_.language == language) map (_.base) match {
          case None       => ""
          case Some(base) => base.split("\n").zipWithIndex.map(si => (si._2 + 1).toString + "\t" + si._1).mkString("\n")
        }

        views.html.idExercises.umlActivity.activityDrawing(user, collection, exercise, language, definitionRest, this)
    }
  }

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[ProgExercise], isCreation: Boolean)
  //                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = ???

  override def renderEditRest(exercise: ProgExercise): Html = ???

}
