package model.tools.programming

import javax.inject._
import model._
import model.core.result.CompleteResultJsonProtocol
import model.points.Points
import model.toolMains.{CollectionToolMain, ToolState}
import model.tools.programming.persistence.ProgTableDefs
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

  override val sampleSolutionJsonFormat: Format[ProgSampleSolution] = ProgJsonProtocols.sampleSolutionJsonFormat

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] = ProgJsonProtocols

  // Other helper methods

  override def exerciseHasPart(exercise: ProgExercise, part: ProgExPart): Boolean = part match {
    case ProgExParts.TestCreation => exercise.unitTestType == UnitTestTypes.Normal
    case _                        => true
  }

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): ProgCollection =
    ProgCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): ProgExercise = ProgExercise(
    id, SemanticVersion(0, 1, 0), title = "", author, text = "", state,
    functionName = "",
    inputTypes = Seq[ProgInput](), outputType = ProgDataTypes.STRING,
    baseData = None, unitTestType = UnitTestTypes.Simplified,
    sampleSolutions = Seq[ProgSampleSolution](),
    sampleTestData = Seq[ProgSampleTestData](),
    unitTestsDescription = "",
    unitTestFiles = Seq.empty,
    foldername = "",
    filename = "",
    unitTestTestConfigs = Seq.empty,
    maybeClassDiagramPart = None
  )

  override def instantiateSolution(id: Int, exercise: ProgExercise, part: ProgExPart, solution: ProgSolution,
                                   points: Points, maxPoints: Points): ProgUserSolution =
    ProgUserSolution(id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: ProgCompleteResult, solSaved: Boolean): ProgCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Db

  override def futureSampleSolutions(collId: Int, id: Int, part: ProgExPart): Future[Seq[ProgSampleSolution]] =
    tables.futureSampleSolutionsForExPart(collId, id, part)

  // Correction

  override def futureFilesForExercise(user: User, collId: Int, exercise: ProgExercise, part: ProgExPart): Future[Seq[ExerciseFile]] =
    tables.futureMaybeOldSolution(user.username, collId, exercise.id, part).map {
      case None         => exercise.filesForExercisePart(part)
      case Some(oldSol) =>
        exercise.filesForExercisePart(part)
          .map { f => if (f.name == "test.py") f.copy(content = oldSol.solution.unitTest.content) else f }
    }

  private def readUnitTestSolution(jsValue: JsValue): Either[String, ProgSolution] = ExerciseFileJsonProtocol.exerciseFileWorkspaceReads.reads(jsValue) match {
    case JsSuccess(solution, _) =>
      // FIXME: hack for testing / development
      //      println(solution)
      solution.files.find(_.name == "test.py") match {
        case None                       => Left("The file could not be found!")
        case Some(unitTestExerciseFile) => Right(ProgSolution("", Seq.empty, unitTestExerciseFile))
      }
    case JsError(errors)        =>
      errors.foreach(jsErr => logger.error(jsErr.toString()))
      Left(errors.toString())
  }

  private def readImplementationSolution(jsValue: JsValue): Either[String, ProgSolution] = ProgJsonProtocols.progSolutionReads.reads(jsValue) match {
    case JsSuccess(solution, _) => Right(solution)
    case JsError(errors)        =>
      errors.foreach(jsErr => logger.error(jsErr.toString()))
      Left(errors.toString())
  }

  override protected def readSolution(request: Request[AnyContent], part: ProgExPart): Either[String, ProgSolution] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) =>
      part match {
        case ProgExParts.TestCreation => readUnitTestSolution(jsValue)
        case _                        => readImplementationSolution(jsValue)
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
      case ProgExParts.TestCreation =>
        exercise.unitTestType match {
          case UnitTestTypes.Simplified =>
            val oldTestData: Seq[ProgUserTestData] = maybeOldSolution.map(_.commitedTestData).getOrElse(Seq[ProgUserTestData]())
            views.html.toolViews.programming.testDataCreation(user, collection, exercise, oldTestData, this)

          case UnitTestTypes.Normal => views.html.toolViews.programming.unittestCreation(user, collection, exercise, exercise.filesForExercisePart(part).map(_.name), this)
        }

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

  override def renderEditRest(exercise: ProgExercise): Html = ???

}
