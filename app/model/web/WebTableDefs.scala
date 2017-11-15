package model.web

import model.Enums.ExerciseState
import model._
import model.web.WebConsts.{HTML_TYPE, JS_TYPE}
import org.openqa.selenium.{By, SearchContext}
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try

case class WebCompleteEx(ex: WebExercise, htmlTasks: Seq[HtmlCompleteTask], jsTasks: Seq[JsCompleteTask]) extends CompleteEx[WebExercise] {

  override def renderRest: Html = new Html(
    s"""<td>${htmlTasks.size} / ${jsTasks.size}</td>
       |<td>TODO!</td>""".stripMargin)

  override def tags: List[WebExTag] = List(new WebExTag(HTML_TYPE, ex.hasHtmlPart), new WebExTag(JS_TYPE, ex.hasJsPart))

}

trait WebCompleteTask {
  val task: WebTask
}

case class HtmlCompleteTask(task: HtmlTask, attributes: Seq[Attribute]) extends WebCompleteTask

case class JsCompleteTask(task: JsTask, conditions: Seq[JsCondition]) extends WebCompleteTask

class WebExTag(part: String, hasExes: Boolean) extends ExTag {

  override def cssClass: String = if (hasExes) "label label-primary" else "label label-default"

  override def buttonContent: String = part

  override def title = s"Diese Aufgabe besitzt ${if (!hasExes) "k" else ""}einen $part-Teil"

}

// Database classes

object WebExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, Option[String], Boolean, Option[String], Boolean)): WebExercise =
    WebExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)

  def apply(i: Int, ti: String, a: String, te: String, s: ExerciseState, ht: Option[String], hp: Boolean, jt: Option[String], jp: Boolean) =
    new WebExercise(BaseValues(i, ti, a, te, s), ht, hp, jt, jp)

  def unapply(arg: WebExercise): Option[(Int, String, String, String, ExerciseState, Option[String], Boolean, Option[String], Boolean)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.htmlText, arg.hasHtmlPart, arg.jsText, arg.hasJsPart))

}

case class WebExercise(bv: BaseValues, htmlText: Option[String], hasHtmlPart: Boolean, jsText: Option[String], hasJsPart: Boolean) extends Exercise(bv)

trait WebTask {
  val id        : Int
  val exerciseId: Int
  val text      : String
  val xpathQuery: String
}

case class HtmlTask(id: Int, exerciseId: Int, text: String, xpathQuery: String, textContent: Option[String]) extends WebTask

case class Attribute(key: String, taskId: Int, exerciseId: Int, value: String)

case class JsTask(id: Int, exerciseId: Int, text: String, xpathQuery: String, actionType: JsActionType, keysToSend: Option[String]) extends WebTask {

  def perform(context: SearchContext): Boolean = Option(context.findElement(By.xpath(xpathQuery))) match {
    case None          => false
    case Some(element) => actionType match {
      case JsActionType.CLICK   =>
        element.click()
        true
      case JsActionType.FILLOUT =>
        element.sendKeys(keysToSend.getOrElse(""))
        // click on other element to fire the onchange event...
        context.findElement(By.xpath("//body")).click()
        true
      case _                    => false
    }
  }

}

case class JsCondition(id: Int, taskId: Int, exerciseId: Int, xpathQuery: String, isPrecondition: Boolean, awaitedValue: String) {

  def description = s"Element mit XPath '$xpathQuery' sollte den Inhalt '$awaitedValue' haben"

}

case class WebSolution(exerciseId: Int, userName: String, solution: String)

// Tables

trait WebTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Table queries

  object webExercises extends ExerciseTableQuery[WebExercise, WebCompleteEx, WebExerciseTable](new WebExerciseTable(_)) {

    override def saveCompleteEx(completeEx: WebCompleteEx)(implicit ec: ExecutionContext): Future[Int] =
      db.run(saveEx(completeEx.ex) zip DBIO.sequence(completeEx.htmlTasks.map(saveHtmlTask)) zip DBIO.sequence(completeEx.jsTasks.map(saveJsTask)))
        .map(_._1._1)

    override protected def completeExForEx(ex: WebExercise)(implicit ec: ExecutionContext): Future[WebCompleteEx] = db.run(htmlTasksAction(ex.id) zip jsTasksAction(ex.id)) map { tasks =>
      val htmlTasks = tasks._1 groupBy (_._1) mapValues (_.map(_._2)) map (ct => HtmlCompleteTask(ct._1, ct._2.flatten)) toSeq
      val jsTasks = tasks._2 groupBy (_._1) mapValues (_.map(_._2)) map (ct => JsCompleteTask(ct._1, ct._2.flatten)) toSeq

      WebCompleteEx(ex, htmlTasks sortBy (_.task.id), jsTasks sortBy (_.task.id))
    }

    private def htmlTasksAction(exId: Int) = htmlTasks.joinLeft(attributes)
      .on { case (ht, att) => ht.id === att.taskId && ht.exerciseId === att.exerciseId }
      .filter(_._1.exerciseId === exId)
      .result

    private def jsTasksAction(exId: Int) = jsTasks.joinLeft(conditions)
      .on { case (ht, con) => ht.id === con.taskId && ht.exerciseId === con.exerciseId }
      .filter(_._1.exerciseId === exId)
      .result

    private def saveHtmlTask(htmlTask: HtmlCompleteTask) =
      (htmlTasks insertOrUpdate htmlTask.task) zip DBIO.sequence(htmlTask.attributes map (attr => attributes insertOrUpdate attr))

    private def saveJsTask(jsTask: JsCompleteTask) =
      (jsTasks insertOrUpdate jsTask.task) zip DBIO.sequence(jsTask.conditions map (cond => conditions insertOrUpdate cond))


  }

  val htmlTasks = TableQuery[HtmlTasksTable]

  val attributes = TableQuery[AttributesTable]

  val jsTasks = TableQuery[JsTasksTable]

  val conditions = TableQuery[ConditionsTable]

  // Dependent query tables

  lazy val webSolutions = TableQuery[WebSolutionsTable]

  // Implicit column types

  implicit val ActionTypeColumnType: BaseColumnType[JsActionType] =
    MappedColumnType.base[JsActionType, String](_.name, str => Try(JsActionType.valueOf(str)).getOrElse(JsActionType.CLICK))

  // Table definitions

  class WebExerciseTable(tag: Tag) extends HasBaseValuesTable[WebExercise](tag, "web_exercises") {

    def htmlText = column[String]("html_text")

    def hasHtmlPart = column[Boolean]("has_html_part")

    def jsText = column[String]("js_text")

    def hasJsPart = column[Boolean]("has_js_part")


    override def * = (id, title, author, text, state, htmlText.?, hasHtmlPart, jsText.?, hasJsPart) <> (WebExercise.tupled, WebExercise.unapply)

  }

  abstract class WebTasksTable[T <: WebTask](tag: Tag, name: String) extends Table[T](tag, name) {

    def id = column[Int]("task_id")

    def exerciseId = column[Int]("exercise_id")

    def text = column[String]("text")

    def xpathQuery = column[String]("xpath_query")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, webExercises)(_.id)

  }

  class HtmlTasksTable(tag: Tag) extends WebTasksTable[HtmlTask](tag, "html_tasks") {

    def textContent = column[String]("text_content")


    override def * = (id, exerciseId, text, xpathQuery, textContent.?) <> (HtmlTask.tupled, HtmlTask.unapply)

  }

  class AttributesTable(tag: Tag) extends Table[Attribute](tag, "html_attributes") {

    def key = column[String]("attr_key")

    def value = column[String]("attr_value")

    def taskId = column[Int]("task_id")

    def exerciseId = column[Int]("exercise_id")


    def pk = primaryKey("pk", (key, taskId, exerciseId))

    def taskFk = foreignKey("task_fk", (taskId, exerciseId), htmlTasks)(t => (t.id, t.exerciseId))


    override def * = (key, taskId, exerciseId, value) <> ((Attribute.apply _).tupled, Attribute.unapply)

  }

  class JsTasksTable(tag: Tag) extends WebTasksTable[JsTask](tag, "js_tasks") {

    def actionType = column[JsActionType]("action_type")

    def keysToSend = column[String]("keys_to_send")


    override def * = (id, exerciseId, text, xpathQuery, actionType, keysToSend.?) <> (JsTask.tupled, JsTask.unapply)

  }

  class ConditionsTable(tag: Tag) extends Table[JsCondition](tag, "js_conditions") {

    def conditionId = column[Int]("condition_id")

    def taskId = column[Int]("task_id")

    def exerciseId = column[Int]("exercise_id")

    def xpathQuery = column[String]("xpath_query")

    def isPrecondition = column[Boolean]("is_precondition")

    def awaitedValue = column[String]("awaited_value")


    def pk = primaryKey("pk", (conditionId, taskId, exerciseId))

    def taskFk = foreignKey("task_fk", (taskId, exerciseId), jsTasks)(t => (t.id, t.exerciseId))


    override def * = (conditionId, taskId, exerciseId, xpathQuery, isPrecondition, awaitedValue) <> (JsCondition.tupled, JsCondition.unapply)

  }

  class WebSolutionsTable(tag: Tag) extends Table[WebSolution](tag, "web_solutions") {

    def exerciseId = column[Int]("exercise_id")

    def userName = column[String]("user_name")

    def solution = column[String]("solution")


    def pk = primaryKey("primaryKey", (exerciseId, userName))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, webExercises)(_.id)

    def userFk = foreignKey("user_fk", userName, users)(_.username)


    override def * = (exerciseId, userName, solution) <> (WebSolution.tupled, WebSolution.unapply)

  }

}
