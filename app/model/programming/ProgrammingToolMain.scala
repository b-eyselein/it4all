package model.programming

import controllers.ExerciseOptions
import javax.inject._
import model.Enums.{ExerciseState, ToolState}
import model.programming.ProgConsts._
import model.programming.ProgrammingToolMain._
import model.toolMains.IdExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, JsonFormat, User}
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, duration}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object ProgrammingToolMain {

  private val ProgExOptions = ExerciseOptions(ProgLanguage.STANDARD_LANG.aceName, 15, 30)

  val STD_TEST_DATA_COUNT = 2

  val MaxWaitTimeInSeconds = 5

  val MaxDuration = Duration(MaxWaitTimeInSeconds, duration.SECONDS)

}

@Singleton
class ProgrammingToolMain @Inject()(override val tables: ProgTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("programming") with JsonFormat {

  // Abstract types

  override type ExType = ProgExercise

  override type CompExType = ProgCompleteEx

  override type Tables = ProgTableDefs

  override type PartType = ProgrammingExPart

  override type SolType = ProgSolution

  override type R = ProgEvalResult

  override type CompResult = ProgCompleteResult

  // Other members

  override val toolname: String = "Programmierung"

  override val toolState: ToolState = ToolState.BETA

  override val consts: Consts = ProgConsts

  override val exParts: Seq[ProgrammingExPart] = ProgrammingExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[ProgExercise] = null

  private val implExtractorRegex = "# \\{12?\\}([\\S\\s]*)# \\{12?\\}".r

  private val actDiagExtractorRegex = "# \\{1?2\\}([\\S\\s]*)# \\{1?2\\}".r

  // Reading solution from requests

  override def futureSaveSolution(sol: ProgSolution): Future[Boolean] = tables.futureSaveSolution(sol)

  override def readSolutionFromPostRequest(user: User, id: Int, part: ProgrammingExPart)(implicit request: Request[AnyContent]): Option[ProgSolution] = None

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: ProgrammingExPart): Option[ProgSolution] = {
    val language = PYTHON_3

    part match {
      case TestdataCreation =>
        val maybeCompleteCommitedTestData: Option[Seq[CompleteCommitedTestData]] = jsValue.asArray(_.asObj flatMap (jsValue => readTestData(id, jsValue, user)))
        maybeCompleteCommitedTestData map (completeCommitedTestData => TestDataSolution(user.username, id, language, completeCommitedTestData))

      case Implementation => jsValue.asObj flatMap { jsObj =>
        for {
          language <- jsObj.enumField(languageName, str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)
          implementation <- jsObj.stringField(implementationName)
        } yield ImplementationSolution(user.username, id, language, implementation)
      }

      case ActivityDiagram => jsValue.asStr map (str => ActivityDiagramSolution(user.username, id, language, str))
    }
  }

  private def readTestData(id: Int, tdJsObj: JsObject, user: User): Option[CompleteCommitedTestData] = for {
    testId <- tdJsObj.intField(idName)
    inputs <- tdJsObj.arrayField(inputName, inpJsValue => inpJsValue.asObj flatMap (inpJsObj => readInput(inpJsObj, testId, user)))
    output <- tdJsObj.stringField(outputName)
  } yield CompleteCommitedTestData(CommitedTestData(testId, id, user.username, output, ExerciseState.RESERVED), inputs)


  private def readInput(inpJsObj: JsObject, testId: Int, user: User): Option[CommitedTestDataInput] = for {
    id <- inpJsObj.intField(idName)
    input <- inpJsObj.stringField(inputName)
  } yield CommitedTestDataInput(id, testId, id, input, user.username)

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): ProgCompleteEx = ProgCompleteEx(
    ProgExercise(id, title = "", author = "", text = "", state, folderIdentifier = "", base = "", maybeClassName = None, functionname = "", indentLevel = 0, outputType = ProgDataTypes.STRING),
    inputTypes = Seq.empty, sampleSolutions = Seq.empty, sampleTestData = Seq.empty
  )

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  // Correction

  override def correctEx(user: User, sol: ProgSolution, exercise: ProgCompleteEx): Future[Try[ProgCompleteResult]] = futureSaveSolution(sol) flatMap { solutionSaved =>

    val (language, implementation, testData) = sol match {
      case tds: TestDataSolution =>
        (ProgLanguage.STANDARD_LANG, exercise.sampleSolutions.head.solution, tds.completeCommitedTestData)

      case is: ImplementationSolution =>
        (sol.language, implExtractorRegex.replaceFirstIn(exercise.ex.base, is.solution), exercise.sampleTestData)

      case ads: ActivityDiagramSolution =>
        (sol.language, actDiagExtractorRegex.replaceFirstIn(exercise.ex.base, exercise.addIndent(ads.solution)), exercise.sampleTestData)
    }

    val correctionResult: Try[Future[Try[ProgCompleteResult]]] =
      ProgrammingCorrector.correct(user, exercise, language, implementation, solutionSaved, testData, toolMain = this)

    correctionResult match {
      case Success(futureRes) => futureRes
      case Failure(error)     => Future(Failure(error))
    }
  }

  // Views

  override def renderExercise(user: User, exercise: ProgCompleteEx, part: ProgrammingExPart, maybeOldSolution: Option[ProgSolution]): Html = part match {
    case TestdataCreation =>
      val oldTestData: Seq[CompleteCommitedTestData] = maybeOldSolution match {
        case Some(tds: TestDataSolution) => tds.completeCommitedTestData
        case _                           => Seq.empty
      }
      views.html.programming.testDataCreation(user, exercise, oldTestData, this)

    case Implementation =>
      val declaration: String = maybeOldSolution map (_.solution) getOrElse {
        implExtractorRegex.findFirstMatchIn(exercise.ex.base) map (_.group(1).trim()) getOrElse exercise.ex.base
        // FIXME: remove comments like '# {2}'!
      }

      val exScript: Html = Html(s"""<script src="${controllers.routes.Assets.versioned(file = "javascripts/programming/progExercise.js")}"></script>""")

      val exRest: Html = views.html.programming.progExerciseRest(exercise)

      views.html.core.exercise2Rows(user, this, ProgExOptions, exercise, exRest, exScript, declaration, Implementation)

    case ActivityDiagram =>
      // TODO: use old soluton!
      views.html.umlActivity.activityDrawing.render(user, exercise, language = ProgLanguage.STANDARD_LANG, toolObject = this)
  }

  override def renderEditRest(exercise: ProgCompleteEx): Html = ???

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: ProgCompleteResult): Html =
    views.html.core.correction(result, result.render, user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: ProgCompleteResult): JsValue = result.toJson

}
