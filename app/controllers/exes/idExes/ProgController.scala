package controllers.exes.idExes

import javax.inject._

import controllers.Secured
import controllers.exes.IntExIdentifier
import controllers.exes.idExes.ProgController._
import model.Enums.ExerciseState
import model.core._
import model.core.tools.ExerciseOptions
import model.programming.ProgConsts._
import model.programming._
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html
import views.html.programming._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object ProgController {

  private val EX_OPTIONS = ExerciseOptions("Programmierung", ProgLanguage.STANDARD_LANG.aceName, 15, 30, updatePrev = false)

  val STD_TEST_DATA_COUNT = 2

  val correctors: Map[ProgLanguage, ProgLangCorrector] = Map(
    JAVA_8 -> JavaCorrector,
    PYTHON_3 -> PythonCorrector
  )

}

@Singleton
class ProgController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: ProgTableDefs)(implicit ec: ExecutionContext)
  extends AIdExController[ProgExercise, ProgCompleteEx, ProgEvaluationResult, GenericCompleteResult[ProgEvaluationResult], ProgTableDefs](cc, dbcp, t, ProgToolObject) with Secured with JsonFormat {

  // Reading solution from requests

  override type SolType = String

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution))

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution))

  // Yaml

  override implicit val yamlFormat: YamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  // db

  override def saveRead(read: Seq[ProgCompleteEx]): Future[Seq[Boolean]] = Future.sequence(read map tables.saveCompleteEx)

  // Other routes

  def testData(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteExById(id) map {
        case Some(ex) =>
          //          val oldTestData = Option(CommitedTestDataHelper.forUserAndExercise(user, id)).getOrElse(List.empty)

          Ok(testDataCreation(user, ex, Seq.empty /* oldTestData*/))
        case None     => Redirect(controllers.exes.idExes.routes.ProgController.index())
      }
  }

  def validateTestData(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      readAndValidateTestdata(id, user, request) map {
        case Some((ex, validTestData)) => Ok(validatedTestData(user, ex.ex, Seq.empty /*validatedTestData*/))
        case None                      => BadRequest("")
      }
  }

  def validateTestDataLive(exerciseId: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      readAndValidateTestdata(exerciseId, user, request) map {
        case Some((ex, validatedTestData)) => Ok("TODO" /*Json.toJson(validatedTestData)*/)
        case None                          => BadRequest
      }
  }

  def getDeclaration(lang: String): EssentialAction = withUser { _ =>
    implicit request => Ok(ProgLanguage.valueOf(lang).getOrElse(ProgLanguage.STANDARD_LANG).declaration)
  }

  // Correction

  override def correctEx(user: User, sol: String, exercise: ProgCompleteEx, identifier: IntExIdentifier): Try[GenericCompleteResult[ProgEvaluationResult]] = Try({
    // FIXME: Time out der Ausführung
    println(sol)

    val language = ProgLanguage.STANDARD_LANG
    val corrector = correctors(language)

    corrector.correct(user, exercise, sol, language)
  })

  // Views

  override def renderExercise(user: User, exercise: ProgCompleteEx): Future[Html] = Future {
    val declaration = ProgLanguage.STANDARD_LANG.buildFunction(exercise.ex.functionName, exercise.ex.inputCount)

    views.html.core.exercise2Rows.render(user, ProgToolObject, EX_OPTIONS, exercise.ex, progExRest(exercise.ex), declaration)
  }

  override def renderExesListRest: Html = Html("")

  override def renderResult(correctionResult: GenericCompleteResult[ProgEvaluationResult]): Html = new Html(correctionResult.toString) // FIXME : implement!

  override protected def onLiveCorrectionSuccess(correctionResult: GenericCompleteResult[ProgEvaluationResult]): Result = Ok(renderResult(correctionResult))

  // Helper methods

  private def readAndValidateTestdata(exerciseId: Int, user: User, request: Request[AnyContent]): Future[Option[(ProgCompleteEx, Seq[CompleteCommitedTestData])]] = {
    futureCompleteExById(exerciseId) map { exOpt =>
      (exOpt zip request.body.asJson).headOption map {
        case (ex, jsValue) =>
          val testData = readAllCommitedTestDataFromJson(jsValue, ex.ex.id, user.username) getOrElse Seq.empty
          // FIXME: save and validate test data...
          testData.foreach(println)

          val validatedTestData = List.empty

          //          Ok("TODO" /*Json.toJson(validatedTestData)*/)
          (ex, validatedTestData)
      }
    }
  }

  // FIXME: use JsonFormat!

  private def readAllCommitedTestDataFromJson(jsValue: JsValue, exId: Int, username: String): Option[Seq[CompleteCommitedTestData]] = jsValue match {
    case JsArray(completeTestData) => Some(completeTestData flatMap (jv => readCommitedTestDataFromJson(jv, exId, username)))
    case _                         => None
  }

  private def readCommitedTestDataFromJson(jv: JsValue, exId: Int, username: String): Option[CompleteCommitedTestData] = jv.asObj flatMap {
    jsObject =>

      val idOpt: Option[Int] = jsObject.intField(ID_NAME)
      val outputOpt: Option[String] = jsObject.stringField(OUTPUT_NAME)

      (idOpt zip outputOpt).headOption map {
        case (id, output) =>
          val testData = CommitedTestData(id, exId, username, output, ExerciseState.CREATED)
          val inputs = readInputsFromJson(jsObject.value get INPUTS_NAME, id, exId, username) getOrElse Seq.empty
          CompleteCommitedTestData(testData, inputs)
      }
  }

  private def readInputsFromJson(jsValue: Option[JsValue], testId: Int, exId: Int, username: String): Option[Seq[CommitedTestDataInput]] = jsValue map {
    case JsArray(objects) => objects flatMap (value => readInputFromJson(value, testId, exId, username))
    case _                => Seq.empty
  }

  private def readInputFromJson(inputJsValue: JsValue, exId: Int, testId: Int, username: String): Option[CommitedTestDataInput] = inputJsValue.asObj flatMap {
    inputJsObject =>
      val idOpt: Option[Int] = inputJsObject.intField(ID_NAME)
      val inputOpt: Option[String] = inputJsObject.stringField(INPUT_NAME)

      (idOpt zip inputOpt).headOption map { case (id, input) => CommitedTestDataInput(id, testId, exId, input, username) }
  }

}
