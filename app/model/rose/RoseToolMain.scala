package model.rose


import javax.inject.{Inject, Singleton}
import model._
import model.programming.ProgLanguages
import model.rose.RoseConsts.{difficultyName, durationName}
import model.toolMains.{IdExerciseToolMain, ToolState}
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
  extends IdExerciseToolMain("Rose", "rose") {

  // Abstract types

  override type ExType = RoseExercise

  override type CompExType = RoseCompleteEx

  override type Tables = RoseTableDefs

  override type PartType = RoseExPart

  override type SolType = String

  override type DBSolType = RoseSolution

  override type R = RoseEvalResult

  override type CompResult = RoseCompleteResult

  override type ReviewType = RoseExerciseReview

  // Other members

  override val toolState: ToolState = ToolState.BETA

  override val consts: Consts = RoseConsts

  override val exParts: Seq[RoseExPart] = RoseExParts.values

  // Forms

  override val compExForm: Form[RoseCompleteEx] = RoseCompleteExerciseForm.format

  override def exerciseReviewForm(username: String, completeExercise: RoseCompleteEx, exercisePart: RoseExPart): Form[RoseExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )
    (RoseExerciseReview(username, completeExercise.ex.id, completeExercise.ex.semanticVersion, exercisePart, _, _))
    (rer => Some((rer.difficulty, rer.maybeDuration)))
  )

  // DB

  //  private def roseSolutionJsonReads(ex: RoseCompleteEx, user: User): Reads[RoseSolution] = (
  //    (__ \ implementationName).read[String] and
  //      (__ \ languageName).readNullable[ProgLanguage](ProgLanguages.jsonFormat) // TODO: temporary fix!
  //    ) (RoseSolution.apply(user.username, ex.ex.id, ex.ex.semanticVersion, RoseExParts.RoseSingleExPart, _, -1 point, -1 point))

  override protected def readSolution(user: User, exercise: RoseCompleteEx, part: RoseExPart)(implicit request: Request[AnyContent]): Try[String] = request.body.asJson match {
    case None       => Failure(new Exception("Request body does not contain json!"))
    case Some(json) => json match {
      case JsString(solution) => Success(solution)
      case _                  => Failure(new Exception("Request body is no string!"))
    }
  }

  // Other helper methods

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): RoseCompleteEx = RoseCompleteEx(
    RoseExercise(id, SemanticVersion(0, 1, 0), title = "", author, text = "", state, fieldWidth = 0, fieldHeight = 0, isMultiplayer = false),
    inputType = Seq[RoseInputType](), sampleSolutions = Seq[RoseSampleSolution]()
  )

  override def instantiateSolution(username: String, exercise: RoseCompleteEx, part: RoseExPart, solution: String, points: Points, maxPoints: Points): RoseSolution =
    RoseSolution(username, exercise.ex.id, exercise.ex.semanticVersion, part, solution, points, maxPoints)

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[RoseCompleteEx] = RoseExYamlProtocol.RoseExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: RoseCompleteEx, part: RoseExPart, maybeOldSolution: Option[RoseSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.rose.roseExercise(user, exercise, maybeOldSolution map (_.solution) getOrElse exercise.declaration(forUser = true), this)

  override def renderEditRest(exercise: RoseCompleteEx): Html = ???

  override def renderUserExerciseEditForm(user: User, newExForm: Form[RoseCompleteEx], isCreation: Boolean)
                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.rose.exitRoseExerciseForm(user, newExForm, isCreation, this)

  // Correction

  override protected def correctEx(user: User, sol: SolType, exercise: RoseCompleteEx, part: RoseExPart): Future[Try[RoseCompleteResult]] = {
    val solDir = solutionDirForExercise(user.username, exercise.ex.id)

    for {
      result <- RoseCorrector.correct(user, exercise, sol, ProgLanguages.STANDARD_LANG, exerciseResourcesFolder, solDir)
    } yield Try(RoseCompleteResult(sol, result))
  }

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: RoseExPart): Future[Option[String]] = part match {
    case RoseExParts.RoseSingleExPart => futureCompleteExById(id) map {
      maybeCompleteEx => maybeCompleteEx flatMap (_.sampleSolutions.headOption map (_.solution))
    }
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
