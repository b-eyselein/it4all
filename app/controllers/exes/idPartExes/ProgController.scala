package controllers.exes.idPartExes

import controllers.exes.ExerciseOptions
import controllers.exes.idPartExes.ProgController._
import javax.inject._
import model.Enums.{ExerciseState, SuccessType}
import model.core._
import model.programming.ProgConsts._
import model.programming.{ProgCompleteResult, _}
import model.yaml.MyYamlFormat
import model.{Consts, JsonFormat, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html
import views.html.programming._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, duration}
import scala.language.implicitConversions
import scala.util.Try

object ProgController {

  private val ProgExOptions = ExerciseOptions("Programmierung", ProgLanguage.STANDARD_LANG.aceName, 15, 30, updatePrev = false)

  val STD_TEST_DATA_COUNT = 2

  val MaxWaitTimeInSeconds = 5

  val MaxDuration = Duration(MaxWaitTimeInSeconds, duration.SECONDS)

}

@Singleton
class ProgController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, override val tables: ProgTableDefs)(implicit ec: ExecutionContext)
  extends JsonFormat with AIdPartExToolMain[ProgExPart, ProgSolutionType, ProgExercise, ProgCompleteEx] {

  override val urlPart : String = "programming"
  override val toolname: String = "prog"
  override val exType  : String = "prog"
  override val consts  : Consts = ProgConsts

  override type R = ProgEvalResult

  override type CompResult = ProgCompleteResult


  override def partTypeFromUrl(urlName: String): Option[ProgExPart] = ProgExParts.values.find(_.urlName == urlName)

  // Result types

  override type Tables = ProgTableDefs

  // Reading solution from requests

  //  override type SolType = ProgSolutionType

  override def saveSolution(sol: ProgSolutionType): Future[Boolean] = ??? //      tables.saveSolution(sol.user, exercise, is.implementation)

  override def readOldSolution(user: User, exerciseId: Int, part: ProgExPart): Future[Option[ProgSolutionType]] = ???

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[ProgSolutionType] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(ImplementationSolution(ProgLanguage.STANDARD_LANG, sol.learnerSolution)))

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: ProgExPart): Option[ProgSolutionType] = part match {
    case TestdataCreation => jsValue.asArray(_.asObj flatMap (jsValue => readTestData(id, jsValue, user))) map TestdataSolution

    case Implementation => jsValue.asObj flatMap { jsObj =>
      for {
        language <- jsObj.enumField("languague", str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)
        implementation <- jsObj.stringField("implementation")
      } yield ImplementationSolution(language, implementation)
    }

    case ActivityDiagram => jsValue.asStr map UmlActivitySolution
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

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  // Correction

  override def correctEx(user: User, sol: ProgSolutionType, exercise: ProgCompleteEx): Future[Try[ProgCompleteResult]] = Future(Try {
    val language = ProgLanguage.STANDARD_LANG

    val futureResult = sol match {
      case tds: TestdataSolution =>

        tables.saveCompleteCommitedTestData(tds.completeCommitedTestData)

        ProgrammingCorrector.validateTestdata(user, exercise, tds)

      case is: ImplementationSolution =>

        tables.saveSolution(user, exercise, is.implementation)

        ProgrammingCorrector.correctImplementation(user, exercise, is.implementation, language)

      case uas: UmlActivitySolution => ProgrammingCorrector.correctImplementation(user, exercise, uas.implementation, language)
    }

    Await.result(futureResult, MaxDuration)
  })

  // Views

  override def renderExercise(user: User, exercise: ProgCompleteEx, maybePart: Option[ProgExPart]): Future[Html] = maybePart match {
    case Some(TestdataCreation) =>
      val oldTestData: Seq[CommitedTestData] = Seq.empty // FIXME: Option(CommitedTestDataHelper.forUserAndExercise(user, id)).getOrElse(List.empty)
      Future(testDataCreation.render(user, exercise, oldTestData))

    case Some(Implementation) => tables.loadSolution(user, exercise) map { oldSol =>
      val declaration: String = oldSol map (_.solution) getOrElse ProgLanguage.STANDARD_LANG.buildFunction(exercise)

      views.html.core.exercise2Rows.render(user, this, ProgExOptions, exercise, exRest, exScript, declaration, Implementation)
    }

    case Some(ActivityDiagram) => Future(views.html.umlActivity.activityDrawing.render(user, exercise, this))

    case None => ???
  }

  //  override def renderExesListRest: Html = Html("")

  private def renderResult(correctionResult: ProgCompleteResult): Html = correctionResult.render

  private def exScript: Html = Html(s"""<script src="${controllers.routes.Assets.versioned("javascripts/programming/progExercise.js")}"></script>""")

  private val exRest: Html = Html(
    s"""<div class="input-group">
       |  <span class="input-group-addon">Sprache</span>
       |  <select class="form-control" id="langSelect" onchange="changeProgLanguage('${controllers.exes.idPartExes.routes.AIdPartExController.progGetDeclaration("")}');">
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
