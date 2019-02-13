package model.programming

import javax.inject._
import model._
import model.programming.ProgConsts.{difficultyName, durationName}
import model.toolMains.{ASingleExerciseToolMain, ToolState}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object ProgToolMain {

  val standardTestCount: Int = 2

}

@Singleton
class ProgToolMain @Inject()(override val tables: ProgTableDefs)(implicit ec: ExecutionContext)
  extends ASingleExerciseToolMain("Programmierung", "programming") {

  // Abstract types

  override type CompExType = ProgCompleteEx

  override type Tables = ProgTableDefs

  override type PartType = ProgExPart

  override type SolType = ProgSolution

  override type DBSolType = DBProgSolution

  override type R = ProgEvalResult

  override type CompResult = ProgCompleteResult

  override type ReviewType = ProgExerciseReview

  // Other members

  override val toolState: ToolState = ToolState.BETA

  override protected val exParts: Seq[ProgExPart] = ProgExParts.values

  override implicit val yamlFormat: MyYamlFormat[ProgCompleteEx] = ProgExYamlProtocol.ProgExYamlFormat

  override protected val completeResultJsonProtocol: ProgCompleteResultJsonProtocol.type = ProgCompleteResultJsonProtocol

  // Forms

  // TODO: create Form mapping ...
  override def compExForm: Form[ProgCompleteEx] = ???

  override def exerciseReviewForm(username: String, completeExercise: ProgCompleteEx, exercisePart: ProgExPart): Form[ProgExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )
    (ProgExerciseReview(username, completeExercise.id, completeExercise.semanticVersion, exercisePart, _, _))
    (per => Some((per.difficulty, per.maybeDuration)))
  )

  // Reading solution from requests

  override protected def readSolution(user: User, exercise: ProgCompleteEx, part: ProgExPart)(implicit request: Request[AnyContent]): Try[ProgSolution] =
    request.body.asJson match {
      case None          => Failure(new Exception("Request does not contain json!"))
      case Some(jsValue) =>
        ProgSolutionJsonFormat(exercise, user).readProgSolutionFromJson(part, jsValue) match {
          case JsSuccess(solution, _) => Success(solution)
          case JsError(errors)        =>
            errors.foreach(println)
            Failure(new Exception(errors.map(_.toString).mkString("\n")))
        }
    }

  // Other helper methods

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): ProgCompleteEx = ProgCompleteEx(
    ProgExercise(id, SemanticVersion(0, 1, 0), title = "", author, text = "", state,
      folderIdentifier = "", functionname = "", outputType = ProgDataTypes.STRING, baseData = None),
    inputTypes = Seq[ProgInput](), sampleSolutions = Seq[ProgSampleSolution](), sampleTestData = Seq[SampleTestData](), maybeClassDiagramPart = None
  )

  override def instantiateSolution(id: Int, username: String, exercise: ProgCompleteEx, part: ProgExPart,
                                   solution: ProgSolution, points: Points, maxPoints: Points): DBProgSolution =
    DBProgSolution(id, username, exercise.id, exercise.semanticVersion, part, solution.solution, solution.language, solution.extendedUnitTests, points, maxPoints)

  // Correction

  override def correctEx(user: User, sol: ProgSolution, exercise: ProgCompleteEx, part: ProgExPart): Future[Try[ProgCompleteResult]] =
    ProgCorrector.correct(user, sol, exercise, toolMain = this)

  override def futureSampleSolutionsForExerciseAndPart(id: Int, part: ProgExPart): Future[Seq[String]] = part match {
    case ProgExParts.Implementation => tables.futureSampleSolutionsForExercisePart(id, part)
    case _                          => Future(Seq.empty) // TODO!
  }

  // Views

  override def renderExercise(user: User, exercise: ProgCompleteEx, part: ProgExPart, maybeOldSolution: Option[DBProgSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {

    // FIXME: how to get language? ==> GET param?
    val language: ProgLanguage = ProgLanguages.PYTHON_3

    part match {
      case ProgExParts.TestdataCreation =>
        val oldTestData: Seq[CommitedTestData] = maybeOldSolution.map(_.commitedTestData).getOrElse(Seq[CommitedTestData]())
        views.html.idExercises.programming.testDataCreation(user, exercise, oldTestData, this)

      case ProgExParts.Implementation =>

        val declaration: String = maybeOldSolution map (_.solution) map {
          case _: ProgTestDataSolution => ""
          case pss: ProgStringSolution => pss.solution
        } getOrElse {
          exercise.sampleSolutions find (_.language == language) map (_.base) getOrElse ""
        }

        views.html.idExercises.programming.progExercise(user, this, exercise, declaration, ProgExParts.Implementation)

      case ProgExParts.ActivityDiagram =>
        // TODO: use old soluton!
        val definitionRest: String = exercise.sampleSolutions.find(_.language == language) map (_.base) match {
          case None       => ""
          case Some(base) => base.split("\n").zipWithIndex.map(si => (si._2 + 1).toString + "\t" + si._1).mkString("\n")
        }

        views.html.idExercises.umlActivity.activityDrawing(user, exercise, language, definitionRest, toolObject = this)
    }
  }

  override def renderUserExerciseEditForm(user: User, newExForm: Form[ProgCompleteEx], isCreation: Boolean)
                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = ???

  override def renderEditRest(exercise: ProgCompleteEx): Html = ???

}
