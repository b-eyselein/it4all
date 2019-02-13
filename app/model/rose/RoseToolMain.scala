package model.rose


import javax.inject.{Inject, Singleton}
import model._
import model.programming.ProgLanguages
import model.rose.RoseConsts.{difficultyName, durationName}
import model.rose.persistence.RoseTableDefs
import model.toolMains.{ASingleExerciseToolMain, ToolState}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesProvider
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

@Singleton
class RoseToolMain @Inject()(val tables: RoseTableDefs)(implicit ec: ExecutionContext)
  extends ASingleExerciseToolMain("Rose", "rose") {

  // Abstract types

  override type ExType = RoseExercise

  override type Tables = RoseTableDefs

  override type PartType = RoseExPart

  override type SolType = String

  override type DBSolType = RoseSolution

  override type R = RoseEvalResult

  override type CompResult = RoseCompleteResult

  override type ReviewType = RoseExerciseReview

  // Other members

  override val toolState: ToolState = ToolState.BETA

  override protected val exParts: Seq[RoseExPart] = RoseExParts.values

  override val exerciseForm: Form[RoseExercise] = RoseExerciseForm.format

  override protected val completeResultJsonProtocol: RoseCompleteResultJsonProtocol.type = RoseCompleteResultJsonProtocol

  // Forms

  override def exerciseReviewForm(username: String, exercise: RoseExercise, exercisePart: RoseExPart): Form[RoseExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )
    (RoseExerciseReview(username, exercise.id, exercise.semanticVersion, exercisePart, _, _))
    (rer => Some((rer.difficulty, rer.maybeDuration)))
  )

  // DB

  override protected def readSolution(user: User, exercise: RoseExercise, part: RoseExPart)(implicit request: Request[AnyContent]): Try[String] = request.body.asJson match {
    case None       => Failure(new Exception("Request body does not contain json!"))
    case Some(json) => json match {
      case JsString(solution) => Success(solution)
      case _                  => Failure(new Exception("Request body is no string!"))
    }
  }

  // Other helper methods

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): RoseExercise = RoseExercise(
    id, SemanticVersion(0, 1, 0), title = "", author, text = "", state, fieldWidth = 0, fieldHeight = 0, isMultiplayer = false,
    inputTypes = Seq[RoseInputType](), sampleSolutions = Seq[RoseSampleSolution]()
  )

  override def instantiateSolution(id: Int, username: String, exercise: RoseExercise, part: RoseExPart, solution: String,
                                   points: Points, maxPoints: Points): RoseSolution =
    RoseSolution(id, username, exercise.id, exercise.semanticVersion, part, solution, points, maxPoints)

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[RoseExercise] = RoseExYamlProtocol.RoseExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: RoseExercise, part: RoseExPart, maybeOldSolution: Option[RoseSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = {
    val declaration = maybeOldSolution map (_.solution) getOrElse exercise.declaration(forUser = true)
    views.html.idExercises.rose.roseExercise(user, exercise, declaration, this)
  }

  override def renderEditRest(exercise: RoseExercise): Html = ???

  override def renderUserExerciseEditForm(user: User, newExForm: Form[RoseExercise], isCreation: Boolean)
                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.rose.exitRoseExerciseForm(user, newExForm, isCreation, this)

  // Correction

  override protected def correctEx(user: User, sol: SolType, exercise: RoseExercise, part: RoseExPart): Future[Try[RoseCompleteResult]] = {
    val solDir = solutionDirForExercise(user.username, exercise.id)

    for {
      result <- RoseCorrector.correct(user, exercise, sol, ProgLanguages.StandardLanguage, exerciseResourcesFolder, solDir)
    } yield Try(RoseCompleteResult(sol, result))
  }

  // Result handlers

  override def onLiveCorrectionResult(pointsSaved: Boolean, result: RoseCompleteResult): JsValue = {
    val (resultType, resultJson): (String, JsValue) = result.result match {
      case rer: RoseExecutionResult    => ("success", Json.parse(rer.result))
      case rser: RoseSyntaxErrorResult => ("syntaxError", JsString(rser.cause))
      case other                       => (RoseConsts.errorName, JsString(other.toString))
    }

    Json.obj("resultType" -> resultType, "result" -> resultJson)
  }

}
