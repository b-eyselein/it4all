package model.tools.regex

import javax.inject.Inject
import model.tools.regex.persistence.RegexTableDefs
import model.toolMains.CollectionToolMain
import model.{ExerciseState, MyYamlFormat, Points, SemanticVersion, SemanticVersionHelper, User}
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Try

class RegexToolMain @Inject()(override val tables: RegexTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Reguläre Ausdrücke", "regex") {

  override type PartType = RegexExPart

  override type ExType = RegexExercise

  override type CollType = RegexCollection


  override type SolType = String

  override type SampleSolType = RegexSampleSolution

  override type UserSolType = RegexDBSolution


  override type Tables = RegexTableDefs

  override type ResultType = RegexEvaluationResult

  override type CompResultType = RegexCompleteResult

  override type ReviewType = RegexExerciseReview


  override protected val exParts: Seq[RegexExPart] = RegexExParts.values

  //  override protected val yamlFormat: MyYamlFormat[RegexExercise] = RegexExYamlProtocol.RegexExYamlFormat
  override protected val yamlFormat: MyYamlFormat[(RegexCollection, Seq[RegexExercise])] = null // RegexExYamlProtocol.RegexExYamlFormat
  override protected val collectionYamlFormat: MyYamlFormat[RegexCollection] = RegexExYamlProtocol.RegexCollectionYamlFormat

  override protected def exerciseYamlFormat(collId: Int): MyYamlFormat[RegexExercise] = RegexExYamlProtocol.RegexExYamlFormat(collId)

  override protected val completeResultJsonProtocol: RegexCompleteResultJsonProtocol.type = RegexCompleteResultJsonProtocol

  override val usersCanCreateExes: Boolean = false

  //  override def exerciseForm: Form[RegexExercise] = RegexExForm.format
  override def compExTypeForm(collId: Int): Form[RegexExercise] = null

  // Database helpers

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): RegexCollection =
    RegexCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(collId: Int, id: Int, author: String, state: ExerciseState): RegexExercise = {
    val semVer = SemanticVersionHelper.DEFAULT

    RegexExercise(
      id, semVer, collId, title = "", author, text = "", state,
      sampleSolutions = Seq[RegexSampleSolution](
        RegexSampleSolution(0, id, semVer, collId, "")
      ),
      testData = Seq[RegexTestData](
        RegexTestData(0, id, semVer, collId, "", isIncluded = false)
      )
    )
  }

  override protected def instantiateSolution(id: Int, username: String, collection: RegexCollection, exercise: RegexExercise, part: RegexExPart,
                                             solution: String, points: Points, maxPoints: Points): RegexDBSolution =
    RegexDBSolution(id, username, exercise.id, exercise.semanticVersion, collection.id, part, solution, points, maxPoints)

  // Correction

  override protected def readSolution(user: User, collection: RegexCollection, exercise: RegexExercise, part: RegexExPart)
                                     (implicit request: Request[AnyContent]): Option[String] = request.body.asJson match {
    case Some(JsString(regex)) => Some(regex)
    case _                     => None
  }

  override protected def correctEx(user: User, sol: String, coll: RegexCollection, exercise: RegexExercise, part: RegexExPart): Try[RegexCompleteResult] = Try {

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
  }


  //  override def exerciseReviewForm(username: String, exercise: RegexExercise, exercisePart: RegexExPart): Form[RegexExerciseReview] = ???

  // Other helper methods

  override def exerciseHasPart(exercise: RegexExercise, partType: RegexExPart): Boolean = true

  // Views

  override def renderExercise(user: User, collection: RegexCollection, exercise: RegexExercise, part: RegexExPart, oldSolution: Option[RegexDBSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.collectionExercises.regex.regexExercise(user, this, collection, exercise, part, oldSolution.map(_.solution))

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[RegexExercise], isCreation: Boolean)(
  //    implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.regex.editRegexExerciseForm(user, newExForm, isCreation, this)

}
