package model.tools.regex

import javax.inject.Inject
import model.points._
import model.toolMains.{CollectionToolMain, ToolState}
import model.tools.regex.BinaryClassificationResultTypes._
import model.tools.regex.persistence.RegexTableDefs
import model._
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.{Format, JsString}
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
  override type SampleSolType = StringSampleSolution
  override type UserSolType = StringUserSolution[RegexExPart]

  override type ReviewType = RegexExerciseReview

  override type ResultType = RegexEvaluationResult
  override type CompResultType = RegexCompleteResult

  override type Tables = RegexTableDefs

  // Members

  override val toolState: ToolState = ToolState.BETA

  override val exParts: Seq[RegexExPart] = RegexExParts.values

  override val usersCanCreateExes: Boolean = false

  // Yaml, Html forms, Json

  override protected val collectionYamlFormat: MyYamlFormat[RegexCollection] = RegexToolYamlProtocol.RegexCollectionYamlFormat
  override protected val exerciseYamlFormat  : MyYamlFormat[RegexExercise]   = RegexToolYamlProtocol.RegexExYamlFormat

  override val collectionForm    : Form[RegexCollection]     = RegexToolForm.collectionFormat
  override val exerciseForm      : Form[RegexExercise]       = RegexToolForm.exerciseFormat
  override val exerciseReviewForm: Form[RegexExerciseReview] = RegexToolForm.exerciseReviewForm

  override val sampleSolutionJsonFormat: Format[StringSampleSolution] = StringSampleSolutionJsonProtocol.stringSampleSolutionJsonFormat

  override protected val completeResultJsonProtocol: RegexCompleteResultJsonProtocol.type = RegexCompleteResultJsonProtocol

  // Database helpers

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): RegexCollection =
    RegexCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): RegexExercise = RegexExercise(
    id, SemanticVersionHelper.DEFAULT, title = "", author, text = "", state, maxPoints = 0,
    sampleSolutions = Seq[StringSampleSolution](
      StringSampleSolution(0, "")
    ),
    testData = Seq[RegexTestData](
      RegexTestData(0, "", isIncluded = false)
    )
  )

  override protected def instantiateSolution(id: Int, exercise: RegexExercise, part: RegexExPart, solution: String, points: Points, maxPoints: Points): StringUserSolution[RegexExPart] =
    StringUserSolution[RegexExPart](id, part, solution, points, maxPoints)

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: RegexExPart): Either[String, String] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) => jsValue match {
      case JsString(regex) => Right(regex)
      case other           => Left(s"Json was no string but ${other}")
    }
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

    val correctResultsCount: Int = results.filter {
      s => s.resultType == TruePositive || s.resultType == TrueNegative
    }.size

    val points: Points = (correctResultsCount.toDouble / exercise.testData.size.toDouble * exercise.maxPoints * 4).toInt.quarterPoints

    RegexCompleteResult(sol, exercise, part, results, points, exercise.maxPoints.points)
  })

  // Other helper methods

  override def exerciseHasPart(exercise: RegexExercise, partType: RegexExPart): Boolean = true

  // Views

  override def previewExerciseRest(ex: Exercise): Html = ex match {
    case re: RegexExercise => views.html.toolViews.regex.previewRegexExerciseRest(re)
    case _                 => ???
  }

  override def renderExercise(user: User, collection: RegexCollection, exercise: RegexExercise, part: RegexExPart, oldSolution: Option[StringUserSolution[RegexExPart]])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.toolViews.regex.regexExercise(user, this, collection, exercise, part, oldSolution.map(_.solution))

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[RegexExercise], isCreation: Boolean)(
  //    implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.regex.editRegexExerciseForm(user, newExForm, isCreation, this)

}
