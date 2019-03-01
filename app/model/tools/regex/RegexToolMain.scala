package model.tools.regex

import javax.inject.Inject
import model.toolMains.CollectionToolMain
import model.tools.regex.persistence.RegexTableDefs
import model.{ExerciseState, MyYamlFormat, Points, SemanticVersionHelper, User}
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class RegexToolMain @Inject()(override val tables: RegexTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Reguläre Ausdrücke", "regex") {

  override type PartType = RegexExPart
  override type ExType = RegexExercise
  override type CollType = RegexCollection


  override type SolType = String
  override type SampleSolType = RegexSampleSolution
  override type UserSolType = RegexUserSolution

  override type ReviewType = RegexExerciseReview

  override type ResultType = RegexEvaluationResult
  override type CompResultType = RegexCompleteResult

  override type Tables = RegexTableDefs

  // Members

  override val exParts: Seq[RegexExPart] = RegexExParts.values

  override val usersCanCreateExes: Boolean = false

  // Yaml, Html forms, Json

  override protected val collectionYamlFormat: MyYamlFormat[RegexCollection] = RegexExYamlProtocol.RegexCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[RegexExercise]   = RegexExYamlProtocol.RegexExYamlFormat

  override val collectionForm    : Form[RegexCollection]     = RegexExForm.collectionFormat
  override val exerciseForm      : Form[RegexExercise]       = RegexExForm.exerciseFormat
  override val exerciseReviewForm: Form[RegexExerciseReview] = RegexExForm.exerciseReviewForm

  override protected val completeResultJsonProtocol: RegexCompleteResultJsonProtocol.type = RegexCompleteResultJsonProtocol

  // Database helpers

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): RegexCollection =
    RegexCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): RegexExercise = RegexExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author, text = "", state,
    sampleSolutions = Seq[RegexSampleSolution](
      RegexSampleSolution(0, "")
    ),
    testData = Seq[RegexTestData](
      RegexTestData(0, "", isIncluded = false)
    )
  )

  override protected def instantiateSolution(id: Int, exercise: RegexExercise, part: RegexExPart, solution: String, points: Points, maxPoints: Points): RegexUserSolution =
    RegexUserSolution(id, part, solution, points, maxPoints)

  // Correction

  override protected def readSolution(user: User, collection: RegexCollection, exercise: RegexExercise, part: RegexExPart)
                                     (implicit request: Request[AnyContent]): Option[String] = request.body.asJson match {
    case Some(JsString(regex)) => Some(regex)
    case _                     => None
  }

  override protected def correctEx(user: User, sol: String, coll: RegexCollection, exercise: RegexExercise, part: RegexExPart): Future[Try[RegexCompleteResult]] = Future.successful(Try {

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

  // Other helper methods

  override def exerciseHasPart(exercise: RegexExercise, partType: RegexExPart): Boolean = true

  // Views

  override def renderExercise(user: User, collection: RegexCollection, exercise: RegexExercise, part: RegexExPart, oldSolution: Option[RegexUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.tools.regex.regexExercise(user, this, collection, exercise, part, oldSolution.map(_.solution))

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[RegexExercise], isCreation: Boolean)(
  //    implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.regex.editRegexExerciseForm(user, newExForm, isCreation, this)

}
