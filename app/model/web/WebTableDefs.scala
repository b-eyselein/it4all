package model.web

import com.google.common.base.Splitter
import model.Enums.ExerciseState
import model.core.{CompleteEx, ExTag}
import model.web.HtmlTaskHelper._
import model.{Exercise, TableDefs}
import org.openqa.selenium.{By, SearchContext}
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile
import controllers.exes.WebController._
import scala.collection.JavaConverters._

object HtmlTaskHelper {
  val ATTRS_JOIN_STR          = ";"
  val ATTR_SPLITTER: Splitter = Splitter.on(ATTRS_JOIN_STR).omitEmptyStrings()
}

class WebExTag(part: String, hasExes: Boolean) extends ExTag {

  override def cssClass: String = if (hasExes) "label label-primary" else "label label-default"

  override def buttonContent: String = part

  override def title = s"Diese Aufgabe besitzt ${if (!hasExes) "k" else ""}einen $part-Teil"

}

case class WebCompleteEx(ex: WebExercise, htmlTasks: Seq[HtmlTask], jsTasks: Seq[JsTask]) extends CompleteEx[WebExercise] {

  override def renderRest: Html = new Html(
    s"""<td>${htmlTasks.size} / ${jsTasks.size}</td>
       |<td>TODO!</td>""".stripMargin)

   override def tags: List[WebExTag] = List(
     new WebExTag(HTML_TYPE, htmlTasks.nonEmpty), new WebExTag(JS_TYPE, jsTasks.nonEmpty)
   )

}

case class WebExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState, htmlText: String, jsText: String)
  extends Exercise(i, ti, a, te, s) {

  override def renderEditRest(isCreation: Boolean): Html = views.html.web.editWebExRest.render(this, isCreation)

}

abstract class WebTask(val id: Int, val exerciseId: Int, val text: String, val xpathQuery: String)

case class HtmlTask(taskId: Int, exId: Int, t: String, x: String, attributes: String, textContent: String) extends WebTask(taskId, exId, t, x) {

  def this(taskId: Int, exId: Int, t: String, x: String, attrs: Seq[Attribute], textContent: String) =
    this(taskId, exId, t, x, attrs.map(_.forDB).mkString(ATTRS_JOIN_STR), textContent)

  def getAttributes: List[Attribute] = ATTR_SPLITTER.splitToList(attributes).asScala.map(Attribute.fromString).toList

}

case class JsTask(taskId: Int, exId: Int, t: String, x: String /*,conditions: List[Condition], action: Action*/) extends WebTask(taskId, exId, t, x) {

  def conditions: List[JsCondition] = List.empty

  def action: JsAction = null
}

case class JsAction(actionId: Int, taskId: Int, exerciseId: Int, actionType: ActionType, xpathQuery: String, keysToSend: String) {

  def perform(context: SearchContext): Boolean = Option(context.findElement(By.xpath(xpathQuery))) match {
    case None          => false
    case Some(element) => actionType match {
      case ActionType.CLICK   =>
        element.click()
        true
      case ActionType.FILLOUT =>
        element.sendKeys(keysToSend)
        // click on other element to fire the onchange event...
        context.findElement(By.xpath("//body")).click()
        true
      case _                  => false
    }
  }

}

case class JsCondition(id: Int, taskId: Int, exerciseId: Int, xpathQuery: String, isPrecondition: Boolean, awaitedValue: String) {

  def description = s"Element mit XPath '$xpathQuery' sollte den Inhalt '$awaitedValue' haben"

}

case class WebSolution(exerciseId: Int, userName: String, solution: String)

trait WebTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Table queries

  val webExercises = TableQuery[WebExerciseTable]

  val htmlTasks = TableQuery[HtmlTasksTable]

  val jsTasks = TableQuery[JsTasksTable]

  val actions = TableQuery[ActionsTable]

  val conditions = TableQuery[ConditionsTable]

  // Dependent query tables

  lazy val webSolutions = TableQuery[WebSolutionsTable]

  // DB Actions

  def htmlTasksForEx(exId: Int): Query[HtmlTasksTable, HtmlTask, Seq] = htmlTasks.filter(_.exerciseId === exId)

  def jsTasksForEx(exId: Int): Query[JsTasksTable, JsTask, Seq] = jsTasks.filter(_.exerciseId === exId)

  // Implicit Column types

  implicit val ActionTypeColumnType: BaseColumnType[ActionType] =
    MappedColumnType.base[ActionType, String](_.name, str => Option(ActionType.valueOf(str)).getOrElse(ActionType.CLICK))

  // Tables

  class WebExerciseTable(tag: Tag) extends HasBaseValuesTable[WebExercise](tag, "web_exercises") {

    def htmlText = column[String]("html_text")

    def jsText = column[String]("js_text")


    def * = (id, title, author, text, state, htmlText, jsText) <> (WebExercise.tupled, WebExercise.unapply)
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

    def attributes = column[String]("attributes")

    def textContent = column[String]("text_content")


    def * = (id, exerciseId, text, xpathQuery, attributes, textContent) <> (HtmlTask.tupled, HtmlTask.unapply)

  }

  class JsTasksTable(tag: Tag) extends WebTasksTable[JsTask](tag, "js_tasks") {

    def * = (id, exerciseId, text, xpathQuery) <> (JsTask.tupled, JsTask.unapply)

  }

  class ConditionsTable(tag: Tag) extends Table[JsCondition](tag, "conditions") {

    def conditionId = column[Int]("condition_id")

    def taskId = column[Int]("task_id")

    def exerciseId = column[Int]("exercise_id")

    def xpathQuery = column[String]("xpath_query")

    def isPrecondition = column[Boolean]("is_precondition")

    def awaitedValue = column[String]("awaited_value")


    def pk = primaryKey("pk", (conditionId, taskId, exerciseId))

    def taskFk = foreignKey("task_fk", (taskId, exerciseId), jsTasks)(t => (t.id, t.exerciseId))


    def * = (conditionId, taskId, exerciseId, xpathQuery, isPrecondition, awaitedValue) <> (JsCondition.tupled, JsCondition.unapply)

  }

  class WebSolutionsTable(tag: Tag) extends Table[WebSolution](tag, "web_solutions") {

    def exerciseId = column[Int]("exercise_id")

    def userName = column[String]("user_name")

    def solution = column[String]("solution")


    def pk = primaryKey("primaryKey", (exerciseId, userName))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, webExercises)(_.id)

    def userFk = foreignKey("user_fk", userName, users)(_.username)


    def * = (exerciseId, userName, solution) <> (WebSolution.tupled, WebSolution.unapply)

  }

  class ActionsTable(tag: Tag) extends Table[JsAction](tag, "ACTIONS") {

    def actionId = column[Int]("CONDITION_ID")

    def taskId = column[Int]("TASK_ID")

    def exerciseId = column[Int]("EXERCISE_ID")

    def actionType = column[ActionType]("ACTION_TYPE")

    def xpathQuery = column[String]("XPATH_QUERY")

    def keysToSend = column[String]("KEYS_TO_SEND")


    def pk = primaryKey("PK", (actionId, taskId, exerciseId))

    def taskFk = foreignKey("TASK_FK", (taskId, exerciseId), jsTasks)(t => (t.id, t.exerciseId))


    def * = (actionId, taskId, exerciseId, actionType, xpathQuery, keysToSend) <> (JsAction.tupled, JsAction.unapply)

  }

}