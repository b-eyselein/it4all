package controllers.programming

import javax.inject.Inject

import controllers.excontrollers.{AExerciseAdminController, IdExController}
import controllers.programming.ProgController._
import model._
import model.exercise.ExerciseOptions
import model.result.CompleteResult
import model.testdata.CommitedTestData
import model.user.User
import play.api.data.Form
import play.api.mvc.{AnyContent, ControllerComponents, Request, Session}
import play.libs.Json
import play.twirl.api.Html

import scala.collection.JavaConverters._
import scala.util.Try

class ProgAdmin @Inject()(cc: ControllerComponents)
  extends AExerciseAdminController[ProgExercise](cc, ProgToolObject, ProgExercise.finder, ProgExerciseReader)

object ProgController {

  private val EX_OPTIONS = ExerciseOptions("Programmierung", AvailableLanguages.stdLang.getAceName, 15, 30, updatePrev = false)

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

class ProgController @Inject()(cc: ControllerComponents) extends IdExController[ProgExercise, ProgEvaluationResult](cc, ProgExercise.finder, ProgToolObject) {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  override def getUser(implicit request: Request[AnyContent]): User = {
    val user = super.getUser

    Option(ProgUser.finder.byId(user.name)) match {
      case None => new ProgUser(user.name).save()
      case Some(_) => Unit
    }
    user
  }


  // private static ProgLangCorrector getCorrector(AvailableLanguages language)
  // {
  // switch(language) {
  // case JAVA_8:
  // return new JavaCorrector()
  // case PYTHON_3:
  // default:
  // return new PythonCorrector()
  // }
  // }


  override def correctEx(sol: StringSolution, exercise: ProgExercise, user: User): Try[CompleteResult[ProgEvaluationResult]] = ???

  def getDeclaration(lang: String) = Action { implicit request => Ok(AvailableLanguages.valueOf(lang).getDeclaration) }

  override def renderExercise(user: User, exercise: ProgExercise): Html = {
    views.html.exercise2Rows.render(user, toolObject, EX_OPTIONS, exercise, views.html.progExRest.render(exercise), AvailableLanguages.stdLang.getDeclaration)
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

  def testData(id: Int) = Action { implicit request =>
    val user: User = getUser
    val oldTestData = Option(CommitedTestData.forUserAndExercise(user, id).asScala.toList).getOrElse(List.empty)

    Ok(views.html.testData.render(user, finder.byId(id), oldTestData))
  }

  def validateTestData(id: Int) = Action { implicit request =>
    val user: User = getUser
    val exercise: ProgExercise = finder.byId(id)
    // val language: AvailableLanguages
    // AvailableLanguages.valueOf(form.get(StringConsts.LANGUAGE_NAME))

    val testData: List[CommitedTestData] = List.empty //extractTestData(factory.form().bindFromRequest(), user.name, exercise)
    testData.foreach(_.save)

    val validatedTestData: List[ProgEvaluationResult] = List.empty

    Ok(views.html.validatedTestData.render(user, exercise, validatedTestData))
  }

  def validateTestDataLive(id: Int) = Action { implicit request =>
    // val exercise: ProgExercise finder.byId(id)
    // val form: DynamicForm factory.form().bindFromRequest()
    // val language: AvailableLanguages
    // AvailableLanguages.valueOf(form.get(StringConsts.LANGUAGE_NAME))

    // val testData: List[CommitedTestData] extractTestData(form,
    // getUser().name, exercise)

    val validatedTestData = List.empty

    Ok("TODO" /*Json.toJson(validatedTestData)*/)
  }
}
