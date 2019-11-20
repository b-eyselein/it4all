package model.tools.collectionTools.web.persistence

import de.uniwue.webtester._
import model._
import model.persistence._
import model.points.Points
import model.tools.collectionTools.web._

object WebDbModels extends ADbModels[WebExercise, DbWebExercise] {

  // Exercise

  override def dbExerciseFromExercise(ex: WebExercise): DbWebExercise =
    DbWebExercise(ex.id, ex.collectionId, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.htmlText, ex.jsText, ex.siteSpec.fileName)

  def exerciseFromDbExercise(
    ex: DbWebExercise,
    htmlTasks: Seq[HtmlTask],
    jsTasks: Seq[JsTask],
    files: Seq[ExerciseFile],
    sampleSolutions: Seq[FilesSampleSolution]
  ): WebExercise = WebExercise(
    ex.id, ex.collectionId, WebConsts.toolId, ex.semanticVersion,
    ex.title, ex.author, ex.text, ex.state,
    ex.htmlText,
    ex.jsText,
    SiteSpec(ex.fileName, htmlTasks, jsTasks),
    files,
    sampleSolutions
  )

  // HtmlTask

  def dbHtmlTaskFromHtmlTask(exId: Int, collId: Int, htmlTask: HtmlTask): DbHtmlTask = DbHtmlTask(
    htmlTask.id, exId, collId, htmlTask.text, htmlTask.xpathQuery,
    htmlTask.awaitedTagName, htmlTask.awaitedTextContent, htmlTask.attributes)

  def htmlTaskFromDbHtmlTask(dbHtmlTask: DbHtmlTask): HtmlTask = HtmlTask(
    dbHtmlTask.id, dbHtmlTask.text, dbHtmlTask.xpathQuery, dbHtmlTask.awaitedTag, dbHtmlTask.textContent, dbHtmlTask.attributes
  )

  // JsTask

  def dbJsTaskFromJsTask(exId: Int, collId: Int, jsTask: JsTask): DbJsTask =
    DbJsTask(jsTask.id, exId, collId, jsTask.text, jsTask.action.xpathQuery, jsTask.action.actionType, jsTask.action.keysToSend, jsTask.preConditions, jsTask.postConditions)


  def jsTaskFromDbJsTask(dbJsTask: DbJsTask): JsTask = {
    val jsAction = JsAction(dbJsTask.xpathQuery, dbJsTask.actionType, dbJsTask.keysToSend)

    JsTask(dbJsTask.id, dbJsTask.text, dbJsTask.preConditions, jsAction, dbJsTask.postConditions)
  }

  // JsCondition

  def dbJsConditionFromJsCondition(
    taskId: Int, exId: Int, collId: Int,
    jsCondition: HtmlElementSpec,
    isPrecondition: Boolean
  ): DbJsCondition = DbJsCondition(
    jsCondition.id, taskId, exId, collId, isPrecondition, jsCondition.xpathQuery, jsCondition.awaitedTagName, jsCondition.awaitedTextContent, jsCondition.attributes
  )

}

object WebExerciseReviewDbModels extends AExerciseReviewDbModels[WebExPart, WebExerciseReview, DbWebExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: WebExPart, review: WebExerciseReview): DbWebExerciseReview =
    DbWebExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbWebExerciseReview): WebExerciseReview =
    WebExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbWebExercise(
  id: Int, collectionId: Int, /* toolId: String, */ semanticVersion: SemanticVersion,
  title: String, author: String, text: LongText, state: ExerciseState,
  htmlText: Option[String], jsText: Option[String], fileName: String
) extends ADbExercise {

  override val toolId: String = WebConsts.toolId

}

final case class DbWebSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, htmlSample: String, jsSample: Option[String])
  extends ADbSampleSol

final case class DbWebUserSolution(
  id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: WebExPart,
  htmlSolution: String, jsSolution: Option[String], points: Points, maxPoints: Points
)
  extends ADbUserSol[WebExPart]


// Tasks

trait DbWebTask {
  val id        : Int
  val exId      : Int
  val collId    : Int
  val text      : String
  val xpathQuery: String
}

// HtmlTask, HtmlAttribute

final case class DbHtmlTask(
  id: Int, exId: Int, collId: Int,
  text: String,
  xpathQuery: String,
  awaitedTag: String,
  textContent: Option[String],
  attributes: Map[String, String]
) extends DbWebTask

// JsTask, JsCondition

final case class DbJsTask(
  id: Int, exId: Int, collId: Int,
  text: String,
  xpathQuery: String,
  actionType: JsActionType,
  keysToSend: Option[String],
  preConditions: Seq[JsHtmlElementSpec],
  postConditions: Seq[JsHtmlElementSpec]
) extends DbWebTask

final case class DbJsCondition(
  id: Int, taskId: Int, exId: Int, collId: Int,
  isPrecondition: Boolean, xpathQuery: String,
  awaitedTag: String, awaitedTextContent: Option[String],
  attributes: Map[String, String]
)

// Exercise review

final case class DbWebExerciseReview(
  username: String, collId: Int, exerciseId: Int, exercisePart: WebExPart,
  difficulty: Difficulty, maybeDuration: Option[Int]
) extends DbExerciseReview[WebExPart]
