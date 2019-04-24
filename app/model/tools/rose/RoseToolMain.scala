package model.tools.rose


import javax.inject.{Inject, Singleton}
import model.{ExerciseState, MyYamlFormat, SemanticVersion, User}
import model.points.Points
import model.core.result.CompleteResultJsonProtocol
import model.tools.programming.ProgLanguages
import model.tools.rose.persistence.RoseTableDefs
import model.toolMains.{CollectionToolMain, ToolState}
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class RoseToolMain @Inject()(val tables: RoseTableDefs)(implicit ec: ExecutionContext)
  extends CollectionToolMain("Rose", "rose") {

  // Abstract types

  override type PartType = RoseExPart
  override type ExType = RoseExercise
  override type CollType = RoseCollection


  override type SolType = String
  override type SampleSolType = RoseSampleSolution
  override type UserSolType = RoseUserSolution

  override type ReviewType = RoseExerciseReview

  override type ResultType = RoseEvalResult
  override type CompResultType = RoseCompleteResult

  override type Tables = RoseTableDefs

  // Other members

  override val toolState: ToolState = ToolState.ALPHA

  override val exParts: Seq[RoseExPart] = RoseExParts.values

  // Yaml, Html forms, Json

  override val collectionYamlFormat: MyYamlFormat[RoseCollection] = RoseExYamlProtocol.RoseCollectionYamlFormat
  override val exerciseYamlFormat  : MyYamlFormat[RoseExercise]   = RoseExYamlProtocol.RoseExYamlFormat

  override val collectionForm    : Form[RoseCollection]     = RoseToolForms.collectionFormat
  override val exerciseForm      : Form[RoseExercise]       = RoseToolForms.exerciseFormat
  override val exerciseReviewForm: Form[RoseExerciseReview] = RoseToolForms.exerciseReviewForm

  override val sampleSolutionJsonFormat: Format[RoseSampleSolution] = RoseSampleSolutionJsonProtocol.roseSampleSolutionJsonFormat

  override protected val completeResultJsonProtocol: CompleteResultJsonProtocol[RoseEvalResult, RoseCompleteResult] = RoseCompleteResultJsonProtocol

  // Other helper methods

  override protected def exerciseHasPart(exercise: RoseExercise, partType: RoseExPart): Boolean = true

  override def instantiateCollection(id: Int, author: String, state: ExerciseState): RoseCollection =
    RoseCollection(id, title = "", author, text = "", state, shortName = "")

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): RoseExercise = RoseExercise(
    id, SemanticVersion(0, 1, 0), title = "", author, text = "", state, fieldWidth = 0, fieldHeight = 0, isMultiplayer = false,
    inputTypes = Seq[RoseInputType](), sampleSolutions = Seq[RoseSampleSolution]()
  )

  override def instantiateSolution(id: Int, exercise: RoseExercise, part: RoseExPart, solution: String, points: Points, maxPoints: Points): RoseUserSolution =
    RoseUserSolution(id, part, language = ProgLanguages.StandardLanguage, solution, points, maxPoints)

  // Views

  override def renderExercise(user: User, collection: RoseCollection, exercise: RoseExercise, part: RoseExPart, maybeOldSolution: Option[RoseUserSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {
    val declaration = maybeOldSolution.map(_.solution).getOrElse(exercise.declaration(forUser = true))
    views.html.toolViews.rose.roseExercise(user, collection, exercise, declaration, this)
  }

  override def renderEditRest(exercise: RoseExercise): Html = ???

  //  override def renderUserExerciseEditForm(user: User, newExForm: Form[RoseExercise], isCreation: Boolean)
  //                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
  //    views.html.idExercises.rose.exitRoseExerciseForm(user, newExForm, isCreation, this)

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: RoseExPart): Either[String, String] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) => jsValue match {
      case JsString(solution) => Right(solution)
      case _                  => Left("Request body is no string!")
    }
  }

  override protected def correctEx(user: User, sol: String, collection: RoseCollection, exercise: RoseExercise, part: RoseExPart): Future[Try[RoseCompleteResult]] = {
    val solDir = solutionDirForExercise(user.username, collection.id, exercise.id)

    for {
      result <- RoseCorrector.correct(user, exercise, sol, ProgLanguages.StandardLanguage, exerciseResourcesFolder, solDir)
    } yield Try(RoseCompleteResult(sol, result))
  }

  // Result handlers

  //  override def onLiveCorrectionResult(pointsSaved: Boolean, result: RoseCompleteResult): JsValue = {
  //    val (resultType, resultJson): (String, JsValue) = result.result match {
  //      case rer: RoseExecutionResult    => ("success", Json.parse(rer.result))
  //      case rser: RoseSyntaxErrorResult => ("syntaxError", JsString(rser.cause))
  //      case other                       => (RoseConsts.errorName, JsString(other.toString))
  //    }
  //
  //    Json.obj("resultType" -> resultType, "result" -> resultJson)
  //  }

}
