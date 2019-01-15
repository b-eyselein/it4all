package model.regex

import javax.inject.Inject
import model.toolMains.IdExerciseToolMain
import model.{Consts, ExerciseState, MyYamlFormat, Points, SemanticVersionHelper, User}
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class RegexToolMain @Inject()(override val tables: RegexTableDefs)(implicit ec: ExecutionContext)
  extends IdExerciseToolMain("Reguläre Ausdrücke", "regex") {

  override type ExType = RegexExercise

  override type CompExType = RegexCompleteEx

  override type Tables = RegexTableDefs

  override type PartType = RegexExPart

  override type SolType = String

  override type DBSolType = RegexDBSolution

  override type R = RegexEvaluationResult

  override type CompResult = RegexCompleteResult

  override type ReviewType = RegexExerciseReview


  override protected val exParts: Seq[RegexExPart] = RegexExParts.values

  override protected val yamlFormat: MyYamlFormat[RegexCompleteEx] = RegexExYamlProtocol.RegexExYamlFormat

  override protected val completeResultJsonProtocol: RegexCompleteResultJsonProtocol.type = RegexCompleteResultJsonProtocol

  override val usersCanCreateExes: Boolean = false

  override def compExForm: Form[RegexCompleteEx] = RegexCompleteExForm.format

  // Database helpers

  override protected def instantiateSolution(id: Int, username: String, exercise: RegexCompleteEx, part: RegexExPart,
                                             solution: String, points: Points, maxPoints: Points): RegexDBSolution =
    RegexDBSolution(id, username, exercise.ex.id, exercise.ex.semanticVersion, part, solution, points, maxPoints)

  // Correction

  override protected def readSolution(user: User, exercise: RegexCompleteEx, part: RegexExPart)(implicit request: Request[AnyContent]): Try[String] = request.body.asJson match {
    case Some(JsString(regex)) => Success(regex)
    case _                     => Failure(new Exception("TODO!"))
  }

  override protected def correctEx(user: User, sol: String, exercise: RegexCompleteEx, part: RegexExPart): Future[Try[RegexCompleteResult]] = Future(Try {

    val regex = sol.r

    val results: Seq[RegexEvaluationResult] = exercise.testData.map {
      testData =>
        val classificationResultType: BinaryClassificationResultType = testData.data match {
          case regex(_*) =>
            if (testData.isIncluded) BinaryClassificationResultTypes.TruePositive
            else BinaryClassificationResultTypes.FalsePositive
          case _         =>
            if (testData.isIncluded) BinaryClassificationResultTypes.FalseNegative
            else BinaryClassificationResultTypes.TrueNegative
        }

        RegexEvaluationResult(testData, classificationResultType)
    }

    RegexCompleteResult(sol, exercise, part, results)
  })


  override def exerciseReviewForm(username: String, completeExercise: RegexCompleteEx, exercisePart: RegexExPart): Form[RegexExerciseReview] = ???

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): RegexCompleteEx = RegexCompleteEx(
    RegexExercise(id, title = "", author, text = "", state, SemanticVersionHelper.DEFAULT),
    sampleSolutions = Seq[RegexSampleSolution](
      RegexSampleSolution(0, id, SemanticVersionHelper.DEFAULT, "")
    ),
    testData = Seq[RegexTestData](
      RegexTestData(0, id, SemanticVersionHelper.DEFAULT, "", isIncluded = false)
    )
  )

  // Views

  override def renderExercise(user: User, exercise: RegexCompleteEx, part: RegexExPart, oldSolution: Option[RegexDBSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.regex.regexExercise(user, this, exercise, part, oldSolution.map(_.solution))

  override def renderUserExerciseEditForm(user: User, newExForm: Form[RegexCompleteEx], isCreation: Boolean)(
    implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.regex.editRegexExerciseForm(user, newExForm, isCreation, this)

}
