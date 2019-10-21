package model.tools.web.persistence

import de.uniwue.webtester._
import model.persistence._
import model.points.Points
import model.tools.web._
import model._

object WebDbModels extends ADbModels[WebExercise, DbWebExercise] {

  // Exercise

  override def dbExerciseFromExercise(collId: Int, ex: WebExercise): DbWebExercise =
    DbWebExercise(ex.id, ex.semanticVersion, collId, ex.title, ex.author, ex.text, ex.state, ex.htmlText, ex.jsText, ex.siteSpec.fileName)

  def exerciseFromDbExercise(ex: DbWebExercise, htmlTasks: Seq[HtmlTask], jsTasks: Seq[JsTask], files: Seq[ExerciseFile], sampleSolutions: Seq[FilesSampleSolution]): WebExercise =
    WebExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.htmlText, ex.jsText,
      SiteSpec(1, ex.fileName, htmlTasks, jsTasks), files, sampleSolutions)

  // HtmlTask

  def dbHtmlTaskFromHtmlTask(exId: Int, collId: Int, htmlTask: HtmlTask): (DbHtmlTask, Seq[DbHtmlAttribute]) = (
    DbHtmlTask(htmlTask.id, exId, collId, htmlTask.text, htmlTask.elementSpec.xpathQuery,
      htmlTask.elementSpec.awaitedTagName, htmlTask.elementSpec.awaitedTextContent),
    htmlTask.elementSpec.attributes map (ha => dbHtmlAttributeFromHtmlAttribute(htmlTask.id, exId, collId, ha))
  )

  def htmlTaskFromDbHtmlTask(dbHtmlTask: DbHtmlTask, dbHtmlAttributes: Seq[DbHtmlAttribute]): HtmlTask = HtmlTask(
    dbHtmlTask.text, HtmlElementSpec(dbHtmlTask.id, dbHtmlTask.xpathQuery, dbHtmlTask.awaitedTag, dbHtmlTask.textContent, dbHtmlAttributes map htmlAttributeFromDbHtmlAttribute)
  )

  // HtmlAttributes

  def dbHtmlAttributeFromHtmlAttribute(taskId: Int, exId: Int, collId: Int, attribute: HtmlAttribute): DbHtmlAttribute =
    DbHtmlAttribute(attribute.key, taskId, exId, collId, attribute.value)

  def htmlAttributeFromDbHtmlAttribute(dbHtmlAttribute: DbHtmlAttribute): HtmlAttribute =
    HtmlAttribute(dbHtmlAttribute.key, dbHtmlAttribute.value)

  // JsTask

  def dbJsTaskFromJsTask(exId: Int, collId: Int, jsTask: JsTask): (DbJsTask, Seq[(DbJsCondition, Seq[DbJsConditionAttribute])]) = {
    val dbPreConditions = jsTask.preConditions.map(pc => dbJsConditionFromJsCondition(jsTask.id, exId, collId, pc, isPrecondition = true))
    val dbPostConditions = jsTask.postConditions.map(pc => dbJsConditionFromJsCondition(jsTask.id, exId, collId, pc, isPrecondition = false))

    (
      DbJsTask(jsTask.id, exId, collId, jsTask.text, jsTask.action.xpathQuery, jsTask.action.actionType, jsTask.action.keysToSend),
      dbPreConditions ++ dbPostConditions
    )
  }

  def jsTaskFromDbJsTask(dbJsTask: DbJsTask, dbJsconditions: Seq[(DbJsCondition, Seq[DbJsConditionAttribute])]): JsTask = {
    val jsAction = JsAction(dbJsTask.xpathQuery, dbJsTask.actionType, dbJsTask.keysToSend)

    val (dbPreConds, dbPostConds) = dbJsconditions.partition { case (condition, _) => condition.isPrecondition }

    JsTask(
      dbJsTask.id, dbJsTask.text, dbPreConds.map(jsConditionFromDbJsCondition), jsAction, dbPostConds.map(jsConditionFromDbJsCondition)
    )
  }

  // JsCondition

  def dbJsConditionFromJsCondition(
    taskId: Int, exId: Int, collId: Int,
    jsCondition: HtmlElementSpec,
    isPrecondition: Boolean
  ): (DbJsCondition, Seq[DbJsConditionAttribute]) = (
    DbJsCondition(jsCondition.id, taskId, exId, collId, isPrecondition, jsCondition.xpathQuery, jsCondition.awaitedTagName, jsCondition.awaitedTextContent),
    jsCondition.attributes.map(dbJsConditionAttributeFromHtmlAttribute(jsCondition.id, taskId, exId, collId, isPrecondition, _))
  )

  private val jsConditionFromDbJsCondition: ((DbJsCondition, Seq[DbJsConditionAttribute])) => HtmlElementSpec = {
    case (dbJsCondition, dbJsConditionAttributes) =>
      HtmlElementSpec(dbJsCondition.id, dbJsCondition.xpathQuery, dbJsCondition.awaitedTag, dbJsCondition.awaitedTextContent,
        dbJsConditionAttributes.map(htmlAttributeFromDbJsConditionAttribute))
  }

  // JsConditionAttribute

  def dbJsConditionAttributeFromHtmlAttribute(condId: Int, taskId: Int, exId: Int, collId: Int, isPrecondition: Boolean, ha: HtmlAttribute): DbJsConditionAttribute =
    DbJsConditionAttribute(condId, taskId, exId, collId, isPrecondition, ha.key, ha.value)

  def htmlAttributeFromDbJsConditionAttribute(dbAttr: DbJsConditionAttribute): HtmlAttribute = HtmlAttribute(dbAttr.key, dbAttr.value)

}

object WebExerciseReviewDbModels extends AExerciseReviewDbModels[WebExPart, WebExerciseReview, DbWebExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: WebExPart, review: WebExerciseReview): DbWebExerciseReview =
    DbWebExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbWebExerciseReview): WebExerciseReview =
    WebExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbWebExercise(
  id: Int, semanticVersion: SemanticVersion, collectionId: Int, title: String, author: String, text: String, state: ExerciseState,
  htmlText: Option[String], jsText: Option[String], fileName: String
) extends ADbExercise

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

final case class DbHtmlTask(id: Int, exId: Int, collId: Int, text: String, xpathQuery: String, awaitedTag: String, textContent: Option[String])
  extends DbWebTask

final case class DbHtmlAttribute(key: String, taskId: Int, exId: Int, collId: Int, value: String)

// JsTask, JsCondition

final case class DbJsTask(id: Int, exId: Int, collId: Int, text: String, xpathQuery: String, actionType: JsActionType, keysToSend: Option[String])
  extends DbWebTask

final case class DbJsCondition(
  id: Int, taskId: Int, exId: Int, collId: Int,
  isPrecondition: Boolean, xpathQuery: String, awaitedTag: String, awaitedTextContent: Option[String]
)

final case class DbJsConditionAttribute(
  condId: Int, taskId: Int, exId: Int, collId: Int,
  isPrecondition: Boolean, key: String, value: String
)

// Exercise review

final case class DbWebExerciseReview(
  username: String, collId: Int, exerciseId: Int, exercisePart: WebExPart,
  difficulty: Difficulty, maybeDuration: Option[Int]
) extends DbExerciseReview[WebExPart]