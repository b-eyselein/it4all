package controllers.exes

import javax.inject._

import controllers.Secured
import controllers.core.AIdExController
import controllers.exes.ProgController._
import model.User
import model.core._
import model.core.tools.ExerciseOptions
import model.programming.ProgLanguage._
import model.programming._
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object ProgController {

  private val EX_OPTIONS = ExerciseOptions("Programmierung", ProgLanguage.STANDARD_LANG.aceName, 15, 30, updatePrev = false)

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

  // Yaml

  override type CompEx = ProgCompleteEx

  override implicit val yamlFormat: YamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  // db

  override type TQ = repo.ProgExercisesTable

  override def tq: repo.ExerciseTableQuery[ProgExercise, ProgCompleteEx, repo.ProgExercisesTable] = repo.progExercises


  override def completeExes: Future[Seq[ProgCompleteEx]] = repo.progExercises.completeExes

  override def completeExById(id: Int): Future[Option[ProgCompleteEx]] = repo.progExercises.completeById(id)

  override def saveRead(read: Seq[ProgCompleteEx]): Future[Seq[Int]] = Future.sequence(read map (repo.progExercises.saveCompleteEx(_)))

  // Helper methods, values ...

  val correctors = Map(
    JAVA_8 -> new JavaCorrector(),
    PYTHON_3 -> new PythonCorrector()
  )

  def getDeclaration(lang: String): EssentialAction = withUser { _ =>
    implicit request =>
      Ok(Try(ProgLanguage.valueOf(lang)).getOrElse(ProgLanguage.STANDARD_LANG).declaration)
  }

  override def correctEx(sol: StringSolution, exercise: ProgCompleteEx, user: User): Try[CompleteResult[ProgEvaluationResult]] = ???

  override def renderExercise(user: User, exercise: ProgCompleteEx): Html =
    views.html.core.exercise2Rows.render(user, ProgToolObject, EX_OPTIONS,
      exercise.ex, views.html.programming.progExRest.render(exercise.ex), ProgLanguage.STANDARD_LANG.declaration)

  override def renderExesListRest: Html = Html("")

  // private CompleteResult[ProgEvaluationResult] correct(User user,
  // ProgExercise exercise, String learnerSolution,
  // ProgLanguage lang) throws CorrectionException {
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
  // val solDir: Path checkAndCreateSolDir(user.languageName, exercise)
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
      //      val oldTestData = Option(CommitedTestDataHelper.forUserAndExercise(user, id)).getOrElse(List.empty)

      //      Ok(views.html.programming.testData.render(user, finder.byId(id).get, oldTestData))
      Ok("TODO")
  }

  def validateTestData(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      //      finder.byId(id) match {
      //        case None           => BadRequest("TODO!")
      //        case Some(exercise) =>
      //          // val language: ProgLanguage
      //          // ProgLanguage.valueOf(form.get(StringConsts.LANGUAGE_NAME))
      //
      //          val testData: List[CommitedTestData] = List.empty //extractTestData(factory.form().bindFromRequest(), user.languageName, exercise)
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
      // val language: ProgLanguage
      // ProgLanguage.valueOf(form.get(StringConsts.LANGUAGE_NAME))

      // val testData: List[CommitedTestData] extractTestData(form,
      // user().languageName, exercise)

      val validatedTestData = List.empty

      Ok("TODO" /*Json.toJson(validatedTestData)*/)
  }

}
