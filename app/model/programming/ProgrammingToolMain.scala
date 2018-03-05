package model.programming

import controllers.ExerciseOptions
import javax.inject._
import model.Enums.ExerciseState
import model.programming.ProgConsts._
import model.programming.ProgrammingToolMain._
import model.toolMains.AExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, JsonFormat, User}
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, duration}
import scala.language.implicitConversions
import scala.util.{Success, Try}

object ProgrammingToolMain {

  private val ProgExOptions = ExerciseOptions(ProgLanguage.STANDARD_LANG.aceName, 15, 30)

  val STD_TEST_DATA_COUNT = 2

  val MaxWaitTimeInSeconds = 5

  val MaxDuration = Duration(MaxWaitTimeInSeconds, duration.SECONDS)

}

@Singleton
class ProgrammingToolMain @Inject()(override val tables: ProgTableDefs)(implicit ec: ExecutionContext) extends AExerciseToolMain("programming") with JsonFormat {

  // Abstract types

  override type ExType = ProgExercise

  override type CompExType = ProgCompleteEx

  override type Tables = ProgTableDefs

  override type PartType = ProgExPart

  override type SolType = ProgSolution

  override type R = ProgEvalResult

  override type CompResult = ProgCompleteResult

  // Other members

  override val toolname: String = "Programmierung"

  override val consts: Consts = ProgConsts

  override val exParts: Seq[ProgExPart] = ProgExParts.values

  // Reading solution from requests

  override def futureSaveSolution(sol: ProgSolution): Future[Boolean] = tables.futureSaveSolution(sol)

  override def futureReadOldSolution(user: User, exerciseId: Int, part: ProgExPart): Future[Option[ProgSolution]] = tables.futureOldSolution(user.username, exerciseId, part)

  override def readSolutionFromPostRequest(user: User, id: Int, part: ProgExPart)(implicit request: Request[AnyContent]): Option[ProgSolution] = None

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: ProgExPart): Option[ProgSolution] = {
    val language = PYTHON_3

    part match {
      case TestdataCreation =>
        val maybeCompleteCommitedTestData: Option[Seq[CompleteCommitedTestData]] = jsValue.asArray(_.asObj flatMap (jsValue => readTestData(id, jsValue, user)))
        maybeCompleteCommitedTestData map (completeCommitedTestData => TestDataSolution(user.username, id, language, completeCommitedTestData))

      case Implementation =>
        jsValue.asObj flatMap { jsObj =>
          for {
            language <- jsObj.enumField(LANGUAGE_NAME, str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)
            implementation <- jsObj.stringField(ImplementationName)
          } yield ImplementationSolution(user.username, id, language, implementation)
        }

      case ActivityDiagram => jsValue.asStr map (str => ActivityDiagramSolution(user.username, id, language, str))
    }
  }

  private def readTestData(id: Int, tdJsObj: JsObject, user: User): Option[CompleteCommitedTestData] = for {
    testId <- tdJsObj.intField(ID_NAME)
    inputs <- readInputs(tdJsObj, testId, user)
    output <- tdJsObj.stringField(OUTPUT_NAME)
  } yield CompleteCommitedTestData(CommitedTestData(testId, id, user.username, output, ExerciseState.RESERVED), inputs)


  private def readInputs(tdJsObj: JsObject, testId: Int, user: User) = tdJsObj.arrayField(INPUTS_NAME, _.asObj flatMap {
    inpJsObj =>
      for {
        id <- inpJsObj.intField(ID_NAME)
        input <- inpJsObj.stringField(INPUT_NAME)
      } yield CommitedTestDataInput(id, testId, id, input, user.username)
  })

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): ProgCompleteEx = ProgCompleteEx(
    ProgExercise(id, title = "", author = "", text = "", state, functionName = "", outputType = ProgDataTypes.STRING),
    inputTypes = Seq.empty, sampleSolution = ProgSampleSolution(id, PYTHON_3, ""), sampleTestData = Seq.empty
  )

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  // Correction

  override def correctEx(user: User, sol: ProgSolution, exercise: ProgCompleteEx): Future[Try[ProgCompleteResult]] = futureSaveSolution(sol) flatMap { solutionSaved =>
    sol match {
      case tds: TestDataSolution =>
        ProgrammingCorrector.validateTestdata(user, exercise, tds, solutionSaved,
          solutionTargetDir = solutionDirForExercise(user.username, exercise.ex.id), exerciseResourcesFolder)

      case is: ImplementationSolution =>
        ProgrammingCorrector.correctImplementation(user, exercise, is.solution, solutionSaved, sol.language,
          solutionTargetDir = solutionDirForExercise(user.username, exercise.ex.id), exerciseResourcesFolder)

      case ads: ActivityDiagramSolution =>
        ProgrammingCorrector.correctImplementation(user, exercise, ads.solution, solutionSaved, sol.language,
          solutionTargetDir = solutionDirForExercise(user.username, exercise.ex.id), exerciseResourcesFolder)
    }

  } map (x => Success(x))

  // Views

  override def renderExercise(user: User, exercise: ProgCompleteEx, part: ProgExPart): Future[Html] = tables.futureOldSolution(user.username, exercise.ex.id, part) map { oldSolution =>
    part match {
      case TestdataCreation =>
        val oldTestData: Seq[CompleteCommitedTestData] = oldSolution match {
          case Some(tds: TestDataSolution) => tds.completeCommitedTestData
          case _                           => Seq.empty
        }
        views.html.programming.testDataCreation(user, exercise, oldTestData, this)

      case Implementation =>
        val declaration: String = oldSolution map (_.solution) getOrElse ProgLanguage.STANDARD_LANG.buildFunction(exercise)
        views.html.core.exercise2Rows.render(user, this, ProgExOptions, exercise, exRest, exScript, declaration, Implementation)

      case ActivityDiagram =>
        // FIXME: use old soluton!
        views.html.umlActivity.activityDrawing.render(user, exercise, this)
    }
  }

  override def renderEditRest(exercise: ProgCompleteEx): Html = ???

  private def exScript: Html = Html(s"""<script src="${controllers.routes.Assets.versioned("javascripts/programming/progExercise.js")}"></script>""")

  private val exRest: Html = Html(
    s"""<div class="input-group">
       |  <span class="input-group-addon">Sprache</span>
       |  <select class="form-control" id="langSelect" onchange="changeProgLanguage('${controllers.exes.routes.ExerciseController.progGetDeclaration("")}');">
       |    ${ProgLanguage.values map (lang => s"""<option value="${lang.name}" ${lang.isSelected(ProgLanguage.STANDARD_LANG)}>${lang.languageName} </option>)""") mkString "\n"}
       |  </select>
       |</div>""".stripMargin)

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: ProgCompleteResult): Html =
    views.html.core.correction.render(result, result.render, user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: ProgCompleteResult): JsValue = result.toJson

}
