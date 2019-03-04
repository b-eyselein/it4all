package model.tools.web.persistence

import model.persistence._
import model.points.Points
import model.tools.web._
import model.{Difficulty, ExerciseState, SemanticVersion}

object WebDbModels extends ADbModels[WebExercise, DbWebExercise] {

  // Exercise

  override def dbExerciseFromExercise(collId: Int, ex: WebExercise): DbWebExercise =
    DbWebExercise(ex.id, ex.semanticVersion, collId, ex.title, ex.author, ex.text, ex.state, ex.htmlText, ex.jsText)

  def exerciseFromDbExercise(ex: DbWebExercise, htmlTasks: Seq[HtmlTask], jsTasks: Seq[JsTask], sampleSolutions: Seq[WebSampleSolution]) =
    WebExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.title, ex.state, ex.htmlText, ex.jsText,
      htmlTasks, jsTasks, sampleSolutions)

  // HtmlTask

  def dbHtmlTaskFromHtmlTask(exId: Int, exSemVer: SemanticVersion, collId: Int, htmlTask: HtmlTask): (DbHtmlTask, Seq[DbHtmlAttribute]) = (
    DbHtmlTask(htmlTask.id, exId, exSemVer, collId, htmlTask.text, htmlTask.xpathQuery, htmlTask.textContent),
    htmlTask.attributes map (ha => dbHtmlAttributeFromHtmlAttribute(htmlTask.id, exId, exSemVer, collId, ha))
  )

  def htmlTaskFromDbHtmlTask(dbHtmlTask: DbHtmlTask, dbHtmlAttributes: Seq[DbHtmlAttribute]): HtmlTask =
    HtmlTask(
      dbHtmlTask.id, dbHtmlTask.text, dbHtmlTask.xpathQuery, dbHtmlTask.textContent, dbHtmlAttributes map htmlAttributeFromDbHtmlAttribute
    )

  // HtmlAttributes

  def dbHtmlAttributeFromHtmlAttribute(taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, attribute: HtmlAttribute): DbHtmlAttribute =
    DbHtmlAttribute(attribute.key, taskId, exId, exSemVer, collId, attribute.value)

  def htmlAttributeFromDbHtmlAttribute(dbHtmlAttribute: DbHtmlAttribute): HtmlAttribute =
    HtmlAttribute(dbHtmlAttribute.key, dbHtmlAttribute.value)

  // JsTask

  def dbJsTaskFromJsTask(exId: Int, exSemVer: SemanticVersion, collId: Int, jsTask: JsTask): (DbJsTask, Seq[DbJsCondition]) = (
    DbJsTask(jsTask.id, exId, exSemVer, collId, jsTask.text, jsTask.xpathQuery, jsTask.actionType, jsTask.keysToSend),
    jsTask.conditions map (jc => dbJsConditionFromJsCondition(jsTask.id, exId, exSemVer, collId, jc))
  )

  def jsTaskFromDbJsTask(dbJsTask: DbJsTask, dbJsconditions: Seq[DbJsCondition]): JsTask =
    JsTask(
      dbJsTask.id, dbJsTask.text, dbJsTask.xpathQuery, dbJsTask.actionType, dbJsTask.keysToSend, dbJsconditions map jsConditionFromDbJsCondition
    )

  // JsCondition

  def dbJsConditionFromJsCondition(taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, jsCondition: JsCondition): DbJsCondition =
    DbJsCondition(jsCondition.id, taskId, exId, exSemVer, collId, jsCondition.xpathQuery, jsCondition.isPrecondition, jsCondition.awaitedValue)

  def jsConditionFromDbJsCondition(dbJsCondition: DbJsCondition): JsCondition =
    JsCondition(dbJsCondition.id, dbJsCondition.xpathQuery, dbJsCondition.isPrecondition, dbJsCondition.awaitedValue)

}

object WebSolutionDbModels extends ASolutionDbModels[WebSolution, WebExPart, WebSampleSolution, DbWebSampleSolution, WebUserSolution, DbWebUserSolution] {

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: WebSampleSolution): DbWebSampleSolution =
    DbWebSampleSolution(sample.id, exId, exSemVer, collId, sample.sample.htmlSolution, sample.sample.jsSolution)

  override def sampleSolFromDbSampleSol(dbSample: DbWebSampleSolution): WebSampleSolution =
    WebSampleSolution(dbSample.id, dbSample.sample)

  // User solutions

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: WebUserSolution): DbWebUserSolution =
    DbWebUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.solution.htmlSolution, solution.solution.jsSolution, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbWebUserSolution): WebUserSolution =
    WebUserSolution(dbSolution.id, dbSolution.part, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)

}

object WebExerciseReviewDbModels extends AExerciseReviewDbModels[WebExPart, WebExerciseReview, DbWebExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: WebExPart, review: WebExerciseReview): DbWebExerciseReview =
    DbWebExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbWebExerciseReview): WebExerciseReview =
    WebExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbWebExercise(id: Int, semanticVersion: SemanticVersion, collectionId: Int, title: String, author: String, text: String, state: ExerciseState,
                               htmlText: Option[String], jsText: Option[String]) extends ADbExercise

final case class DbWebSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, htmlSample: String, jsSample: String)
  extends ADbSampleSol[WebSolution] {

  override val sample: WebSolution = WebSolution(htmlSample, jsSample)

}

final case class DbWebUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: WebExPart,
                                   htmlSolution: String, jsSolution: String, points: Points, maxPoints: Points)
  extends ADbUserSol[WebExPart, WebSolution] {

  override val solution: WebSolution = WebSolution(htmlSolution, jsSolution)

}

// Tasks

trait DbWebTask {
  val id        : Int
  val exId      : Int
  val exSemVer  : SemanticVersion
  val collId    : Int
  val text      : String
  val xpathQuery: String
}

// HtmlTask, HtmlAttribute

final case class DbHtmlTask(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, text: String, xpathQuery: String, textContent: Option[String])
  extends DbWebTask

final case class DbHtmlAttribute(key: String, taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, value: String)

// JsTask, JsCondition

final case class DbJsTask(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, text: String, xpathQuery: String, actionType: JsActionType, keysToSend: Option[String])
  extends DbWebTask

final case class DbJsCondition(id: Int, taskId: Int, exerciseId: Int, exSemVer: SemanticVersion, collId: Int,
                               xpathQuery: String, isPrecondition: Boolean, awaitedValue: String)

// Exercise review


final case class DbWebExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: WebExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[WebExPart]
