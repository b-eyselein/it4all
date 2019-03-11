package model.tools.web.persistence

import de.uniwue.webtester._
import model.persistence._
import model.points.Points
import model.tools.web._
import model.{Difficulty, ExerciseFile, ExerciseState, SemanticVersion}

object WebDbModels extends ADbModels[WebExercise, DbWebExercise] {

  // Exercise

  override def dbExerciseFromExercise(collId: Int, ex: WebExercise): DbWebExercise =
    DbWebExercise(ex.id, ex.semanticVersion, collId, ex.title, ex.author, ex.text, ex.state, ex.htmlText, ex.jsText, ex.siteSpec.fileName)

  def exerciseFromDbExercise(ex: DbWebExercise, htmlTasks: Seq[HtmlTask], jsTasks: Seq[JsTask], files: Seq[ExerciseFile], sampleSolutions: Seq[WebSampleSolution]) =
    WebExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.htmlText, ex.jsText,
      SiteSpec(1, ex.fileName, htmlTasks, jsTasks), files, sampleSolutions)

  // HtmlTask

  def dbHtmlTaskFromHtmlTask(exId: Int, exSemVer: SemanticVersion, collId: Int, htmlTask: HtmlTask): (DbHtmlTask, Seq[DbHtmlAttribute]) = (
    DbHtmlTask(htmlTask.id, exId, exSemVer, collId, htmlTask.text, htmlTask.elementSpec.xpathQuery,
      htmlTask.elementSpec.awaitedTagName, htmlTask.elementSpec.awaitedTextContent),
    htmlTask.elementSpec.attributes map (ha => dbHtmlAttributeFromHtmlAttribute(htmlTask.id, exId, exSemVer, collId, ha))
  )

  def htmlTaskFromDbHtmlTask(dbHtmlTask: DbHtmlTask, dbHtmlAttributes: Seq[DbHtmlAttribute]): HtmlTask = HtmlTask(
    dbHtmlTask.text, HtmlElementSpec(dbHtmlTask.id, dbHtmlTask.xpathQuery, dbHtmlTask.awaitedTag, dbHtmlTask.textContent, dbHtmlAttributes map htmlAttributeFromDbHtmlAttribute)
  )

  // HtmlAttributes

  def dbHtmlAttributeFromHtmlAttribute(taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, attribute: HtmlAttribute): DbHtmlAttribute =
    DbHtmlAttribute(attribute.key, taskId, exId, exSemVer, collId, attribute.value)

  def htmlAttributeFromDbHtmlAttribute(dbHtmlAttribute: DbHtmlAttribute): HtmlAttribute =
    HtmlAttribute(dbHtmlAttribute.key, dbHtmlAttribute.value)

  // JsTask

  def dbJsTaskFromJsTask(exId: Int, exSemVer: SemanticVersion, collId: Int, jsTask: JsTask): (DbJsTask, Seq[(DbJsCondition, Seq[DbJsConditionAttribute])]) = {
    val dbPreConditions = jsTask.preConditions.map(pc => dbJsConditionFromJsCondition(jsTask.id, exId, exSemVer, collId, pc, isPrecondition = true))
    val dbPostConditions = jsTask.preConditions.map(pc => dbJsConditionFromJsCondition(jsTask.id, exId, exSemVer, collId, pc, isPrecondition = false))

    (
      DbJsTask(jsTask.id, exId, exSemVer, collId, jsTask.text, jsTask.action.xpathQuery, jsTask.action.actionType, jsTask.action.keysToSend),
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

  def dbJsConditionFromJsCondition(taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, jsCondition: HtmlElementSpec,
                                   isPrecondition: Boolean): (DbJsCondition, Seq[DbJsConditionAttribute]) = (
    DbJsCondition(jsCondition.id, taskId, exId, exSemVer, collId, isPrecondition, jsCondition.xpathQuery, jsCondition.awaitedTagName, jsCondition.awaitedTextContent),
    jsCondition.attributes.map(dbJsConditionAttributeFromHtmlAttribute(jsCondition.id, taskId, exId, exSemVer, collId, isPrecondition, _))
  )

  private val jsConditionFromDbJsCondition: ((DbJsCondition, Seq[DbJsConditionAttribute])) => HtmlElementSpec = {
    case (dbJsCondition, dbJsConditionAttributes) =>
      HtmlElementSpec(dbJsCondition.id, dbJsCondition.xpathQuery, dbJsCondition.awaitedTag, dbJsCondition.awaitedTextContent,
        dbJsConditionAttributes.map(htmlAttributeFromDbJsConditionAttribute))
  }

  // JsConditionAttribute

  def dbJsConditionAttributeFromHtmlAttribute(condId: Int, taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, isPrecondition: Boolean, ha: HtmlAttribute) =
    DbJsConditionAttribute(condId, taskId, exId, exSemVer, collId, isPrecondition, ha.key, ha.value)

  def htmlAttributeFromDbJsConditionAttribute(dbAttr: DbJsConditionAttribute): HtmlAttribute = HtmlAttribute(dbAttr.key, dbAttr.value)

  // WebFile

  def dbWebFileFromWebFile(exId: Int, exSemVer: SemanticVersion, collId: Int, webFile: ExerciseFile): DbWebFile =
    DbWebFile(webFile.path, exId, exSemVer, collId, webFile.resourcePath, webFile.fileType, webFile.editable)

  def webFileFromDbWebFile(dbWebFile: DbWebFile): ExerciseFile = ExerciseFile(dbWebFile.path, dbWebFile.resourcePath, dbWebFile.fileType, dbWebFile.editable)

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
                               htmlText: Option[String], jsText: Option[String], fileName: String) extends ADbExercise

final case class DbWebSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, htmlSample: String, jsSample: Option[String])
  extends ADbSampleSol[WebSolution] {

  override val sample: WebSolution = WebSolution(htmlSample, jsSample)

}

final case class DbWebUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: WebExPart,
                                   htmlSolution: String, jsSolution: Option[String], points: Points, maxPoints: Points)
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

final case class DbHtmlTask(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, text: String, xpathQuery: String, awaitedTag: String, textContent: Option[String])
  extends DbWebTask

final case class DbHtmlAttribute(key: String, taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, value: String)

// JsTask, JsCondition

final case class DbJsTask(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, text: String, xpathQuery: String, actionType: JsActionType, keysToSend: Option[String])
  extends DbWebTask

final case class DbJsCondition(id: Int, taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, isPrecondition: Boolean,
                               xpathQuery: String, awaitedTag: String, awaitedTextContent: Option[String])

final case class DbJsConditionAttribute(condId: Int, taskId: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, isPrecondition: Boolean,
                                        key: String, value: String)

// WebFile

final case class DbWebFile(path: String, exId: Int, exSemVer: SemanticVersion, collId: Int, resourcePath: String, fileType: String, editable: Boolean)

// Exercise review

final case class DbWebExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: WebExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[WebExPart]
