package controllers.exes

import javax.inject._

import controllers.core.AIdExController
import controllers.exes.ProgController._
import model.User
import model.core._
import model.core.result.CompleteResult
import model.core.tools.ExerciseOptions
import model.programming._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import scala.util.Try

object ProgController {

  private val EX_OPTIONS = ExerciseOptions("Programmierung", AvailableLanguages.stdLang.aceName, 15, 30, updatePrev = false)

  val STD_TEST_DATA_COUNT = 2

  //  private def extractTestData(form: DynamicForm, username: String, exercise: ProgExercise): List[CommitedTestData] =
  //    (0 until Integer.parseInt(form.get(TEST_COUNT_NAME))).map(testCounter => readTestDataFromForm(form, username, testCounter, exercise)).toList

  //  private def readTestDataFromForm(form: DynamicForm, username: String, testId: Int, exercise: ProgExercise): CommitedTestData = {
  //    val key: CommitedTestDataKey = new CommitedTestDataKey(username, exercise.id, testId)
  //    val testdata: CommitedTestData = Option(CommitedTestData.finder.byId(key)).getOrElse(new CommitedTestData(key))
  //
  //    testdata.exercise = exercise
  //    testdata.inputs = (0 until exercise.getInputCount).map(inputCounter => form.get(s"inp_${inputCounter}_$testId")).mkString(ITestData.VALUES_SPLIT_CHAR)
  //
  //    testdata.output = form.get(s"outp_$testId")
  //    testdata.approvalState = ApprovalState.CREATED
  //
  //    testdata
  //  }

}


@Singleton
class ProgController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                              (implicit ec: ExecutionContext)
  extends AIdExController[ProgExercise, ProgEvaluationResult](cc, dbcp, r, ProgToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = ???

  // db

  override type DbType = ProgExercise

  override implicit val yamlFormat: YamlFormat[ProgExercise] = null

  override type TQ = repo.ProgExercisesTable

  override def tq = repo.progExercises

  val correctors = Map(
    JAVA_8 -> new JavaCorrector(),
    PYTHON_3 -> new PythonCorrector()
  )

  override def correctEx(sol: StringSolution, exercise: Option[ProgExercise], user: User): Try[CompleteResult[ProgEvaluationResult]] = ???

  def getDeclaration(lang: String): EssentialAction = withUser { _ => implicit request => Ok(AvailableLanguages.byName(lang).get.declaration) }

  override def renderExercise(user: User, exercise: ProgExercise): Html = {
    views.html.core.exercise2Rows.render(user, ProgToolObject, EX_OPTIONS, exercise, views.html.programming.progExRest.render(exercise), AvailableLanguages.stdLang.declaration)
  }

  override def renderExesListRest: Html = Html("")

  // private CompleteResult[ProgEvaluationResult] correct(User user,
  // ProgExercise exercise, String learnerSolution,
  // AvailableLanguages lang) throws CorrectionException {
  // // FIXME: Time out der Ausführung
  // try {
  // Logger.debug("Solution: " + learnerSolution)
  //
  // val completeTestData: List[ITestData]
  // Stream.concat(exercise.sampleTestData.stream(),
  // CommitedTestData.forUserAndExercise(user,
  // exercise.getId()).stream()).collect(Collectors.toList())
  //
  // val corrector: ProgLangCorrector getCorrector(lang)
  //
  // val solDir: Path checkAndCreateSolDir(user.name, exercise)
  //
  // return new CompleteResult[](learnerSolution,
  // corrector.evaluate(learnerSolution, completeTestData, solDir))
  // } catch (final Exception e) {
  // throw new CorrectionException(learnerSolution, "Error while correcting
  // files", e)
  // }
  // }

  override def renderResult(correctionResult: CompleteResult[ProgEvaluationResult]): Html = ???

  def testData(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      val oldTestData = Option(CommitedTestDataHelper.forUserAndExercise(user, id)).getOrElse(List.empty)

      //      Ok(views.html.programming.testData.render(user, finder.byId(id).get, oldTestData))
      Ok("TODO")
  }

  def validateTestData(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      //      finder.byId(id) match {
      //        case None           => BadRequest("TODO!")
      //        case Some(exercise) =>
      //          // val language: AvailableLanguages
      //          // AvailableLanguages.valueOf(form.get(StringConsts.LANGUAGE_NAME))
      //
      //          val testData: List[CommitedTestData] = List.empty //extractTestData(factory.form().bindFromRequest(), user.name, exercise)
      //          //          testData.foreach(_.save)
      //
      //          val validatedTestData: List[ProgEvaluationResult] = List.empty
      //
      //          Ok(views.html.programming.validatedTestData.render(user, exercise, validatedTestData))
      //      }
      Ok("TODO")
  }

  def validateTestDataLive(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      // val exercise: ProgExercise finder.byId(id)
      // val form: DynamicForm factory.form().bindFromRequest()
      // val language: AvailableLanguages
      // AvailableLanguages.valueOf(form.get(StringConsts.LANGUAGE_NAME))

      // val testData: List[CommitedTestData] extractTestData(form,
      // user().name, exercise)

      val validatedTestData = List.empty

      Ok("TODO" /*Json.toJson(validatedTestData)*/)
  }

}
