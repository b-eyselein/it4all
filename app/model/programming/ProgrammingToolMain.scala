package model.programming

import controllers.ExerciseOptions
import controllers.exes.AExerciseToolMain
import javax.inject._
import model.Enums.{ExerciseState, SuccessType}
import model.programming.ProgConsts._
import model.programming.ProgrammingToolMain._
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

  private val ProgExOptions = ExerciseOptions("Programmierung", ProgLanguage.STANDARD_LANG.aceName, 15, 30, updatePrev = false)

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

  override def futureSaveSolution(sol: ProgSolution): Future[Boolean] = ??? //      tables.saveSolution(sol.user, exercise, is.implementation)

  override def futureReadOldSolution(user: User, exerciseId: Int, part: ProgExPart): Future[Option[ProgSolution]] = part match {
    case Implementation   => ??? //tables.loadSolution(user, exerciseId)
    case TestdataCreation => ???
    case ActivityDiagram  => ???
  }

  override def readSolutionFromPostRequest(user: User, id: Int, part: ProgExPart)(implicit request: Request[AnyContent]): Option[ProgSolution] = None

  //    SolutionFormHelper.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(ImplementationSolution(ProgLanguage.STANDARD_LANG, sol.learnerSolution)))

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: ProgExPart): Option[ProgSolution] = {
    val maybeLanguageAndSolution: Option[(ProgLanguage, String)] =
      part match {
        case TestdataCreation => jsValue.asArray(_.asObj flatMap (jsValue => readTestData(id, jsValue, user))) map (x => (PYTHON_3, x.toString))

        case Implementation => jsValue.asObj flatMap { jsObj =>
          for {
            language <- jsObj.enumField("languague", str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)
            implementation <- jsObj.stringField("implementation")
          } yield (language, implementation)
        }

        case ActivityDiagram => jsValue.asStr map ((PYTHON_3, _))
      }

    maybeLanguageAndSolution map {
      case (language, solution) => ProgSolution(user.username, id, part, language, solution)
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
    // FIXME: remove null!
    ProgExercise(id, title = "", author = "", text = "", state, functionName = "", outputType = null),
    inputTypes = Seq.empty, sampleSolution = null, sampleTestData = Seq.empty
  )

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  // Correction

  override def correctEx(user: User, sol: ProgSolution, exercise: ProgCompleteEx): Future[Try[ProgCompleteResult]] = tables.futureSaveSolution(sol) flatMap { solutionSaved =>
    sol.part match {
      case TestdataCreation =>
        ProgrammingCorrector.validateTestdata(user, exercise, sol,
          solutionDirForExercise(user.username, exercise.ex.id), exerciseResourcesFolder)

      case Implementation =>
        ProgrammingCorrector.correctImplementation(user, exercise, sol.solution, sol.language,
          solutionDirForExercise(user.username, exercise.ex.id), exerciseResourcesFolder)

      case ActivityDiagram =>
        ProgrammingCorrector.correctImplementation(user, exercise, sol.solution, sol.language,
          solutionDirForExercise(user.username, exercise.ex.id), exerciseResourcesFolder)
    }

  } map (x => Success(x))

  // Views

  override def renderExercise(user: User, exercise: ProgCompleteEx, part: ProgExPart): Future[Html] = tables.futureOldSolution(user.username, exercise.ex.id, part) map { oldSolution =>
    part match {
      case TestdataCreation =>
        val oldTestData: Seq[CommitedTestData] = Seq.empty // FIXME: Option(CommitedTestDataHelper.forUserAndExercise(user, id)).getOrElse(List.empty)
        views.html.programming.testDataCreation(user, exercise, oldTestData)

      case Implementation =>
        val declaration: String = oldSolution map (_.solution) getOrElse ProgLanguage.STANDARD_LANG.buildFunction(exercise)

        views.html.core.exercise2Rows.render(user, this, ProgExOptions, exercise, exRest, exScript, declaration, Implementation)


      case ActivityDiagram => views.html.umlActivity.activityDrawing.render(user, exercise, this)
    }
  }

  override def renderEditRest(exercise: ProgCompleteEx): Html = ???

  //  override def renderExesListRest: Html = Html("")

  private def renderResult(correctionResult: ProgCompleteResult): Html = correctionResult.render

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
    views.html.core.correction.render(result, renderResult(result), user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: ProgCompleteResult): JsValue = result match {
    case ir: ProgImplementationCompleteResult => ??? //renderResult(ir)
    case vr: ProgValidationCompleteResult     => JsArray(vr.results.map {
      case se: SyntaxError      => ???
      case aer: ExecutionResult => Json.obj(ID_NAME -> aer.completeTestData.testData.id, "correct" -> JsBoolean(aer.success == SuccessType.COMPLETE))
    })
  }

}
