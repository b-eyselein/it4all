package controllers.idExes

import javax.inject._

import controllers.Secured
import controllers.idExes.ProgController._
import model.Enums.ExerciseState
import model.User
import model.core._
import model.core.tools.ExerciseOptions
import model.programming.ProgConsts._
import model.programming._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

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
class ProgController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                              (implicit ec: ExecutionContext)
  extends AIdExController[ProgExercise, ProgEvaluationResult](cc, dbcp, r, ProgToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = Solution.stringSolForm

  // Yaml

  override type CompEx = ProgCompleteEx

  override implicit val yamlFormat: YamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  // db

  override type TQ = repo.ProgExercisesTable

  override def tq: repo.ExerciseTableQuery[ProgExercise, ProgCompleteEx, repo.ProgExercisesTable] = repo.progExercises

  override def completeExes: Future[Seq[ProgCompleteEx]] = repo.progExercises.completeExes

  override def completeExById(id: Int): Future[Option[ProgCompleteEx]] = repo.progExercises.completeById(id)

  override def saveRead(read: Seq[ProgCompleteEx]): Future[Seq[Int]] = Future.sequence(read map (repo.progExercises.saveCompleteEx(_)))

  // Other routes

  def testData(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeExById(id) map {
        case Some(ex) =>
          //          val oldTestData = Option(CommitedTestDataHelper.forUserAndExercise(user, id)).getOrElse(List.empty)

          Ok(views.html.programming.testData.render(user, ex, Seq.empty /* oldTestData*/))
        case None     => Redirect(controllers.idExes.routes.ProgController.index())
      }
  }

  def validateTestData(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      readAndValidateTestdata(id, user, request) map {
        case Some((ex, validatedTestData)) => Ok(views.html.programming.validatedTestData.render(user, ex.ex, Seq.empty /*validatedTestData*/))
        case None                          => BadRequest("")
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

  override def correctEx(sol: StringSolution, exercise: ProgCompleteEx, user: User): Try[CompleteResult[ProgEvaluationResult]] = Try({
    // FIXME: Time out der Ausführung
    println(sol.learnerSolution)

    val language = ProgLanguage.STANDARD_LANG
    val corrector = correctors(language)

    corrector.correct(user, exercise, sol.learnerSolution, language)
  })

  // Views

  override def renderExercise(user: User, exercise: ProgCompleteEx): Html = {
    val declaration = ProgLanguage.STANDARD_LANG.buildFunction(exercise.ex.functionName, exercise.ex.inputCount)

    views.html.core.exercise2Rows.render(user, ProgToolObject, EX_OPTIONS, exercise.ex, views.html.programming.progExRest.render(exercise.ex), declaration)
  }

  override def renderExesListRest: Html = Html("")

  override def renderResult(correctionResult: CompleteResult[ProgEvaluationResult]): Html = ???

  // Helper methods

  private def readAndValidateTestdata(exerciseId: Int, user: User, request: Request[AnyContent]): Future[Option[(ProgCompleteEx, Seq[CompleteCommitedTestData])]] = {
    completeExById(exerciseId) map { exOpt =>
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

  private def readCommitedTestDataFromJson(jv: JsValue, exId: Int, username: String): Option[CompleteCommitedTestData] = jv match {
    case JsObject(values) =>

      val idOpt: Option[Int] = jsIntValue(values, ID_NAME)
      val outputOpt: Option[String] = jsStringValue(values, OUTPUT_NAME)

      (idOpt zip outputOpt).headOption map {
        case (id, output) =>
          CompleteCommitedTestData(
            CommitedTestData(id, exId, username, output, ExerciseState.CREATED),
            readInputsFromJson(values get INPUTS_NAME, id, exId, username) getOrElse Seq.empty
          )
      }
    case _                => None
  }

  private def readInputsFromJson(jsValue: Option[JsValue], testId: Int, exId: Int, username: String): Option[Seq[CommitedTestDataInput]] = jsValue map {
    case JsArray(objects) => objects flatMap (value => readInputFromJson(value, testId, exId, username))
    case _                => Seq.empty
  }

  private def readInputFromJson(iObj: JsValue, exId: Int, testId: Int, username: String): Option[CommitedTestDataInput] = iObj match {
    case JsObject(values) =>

      val idOpt: Option[Int] = jsIntValue(values, ID_NAME)
      val inputOpt: Option[String] = jsStringValue(values, INPUT_NAME)

      (idOpt zip inputOpt).headOption map { case (id, input) => CommitedTestDataInput(id, testId, exId, input, username) }
    case _                => None
  }

  private def jsIntValue(values: scala.collection.Map[String, JsValue], fieldName: String) = values get fieldName flatMap {
    case JsNumber(value) => Some(value.intValue)
    case _               => None
  }

  private def jsStringValue(values: scala.collection.Map[String, JsValue], fieldName: String) = values get fieldName flatMap {
    case JsString(str) => Some(str)
    case _             => None
  }

}
