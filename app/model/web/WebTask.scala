package model.web

import com.google.common.base.Splitter
import model.core.HasBaseValues.SPLITTER
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.collection.JavaConverters._

abstract class WebTask(val id: Int, val exerciseId: Int, val text: String, val xpathQuery: String) {
  def textForJson: java.util.List[String] = SPLITTER.splitToList(text)
}

object HtmlTaskHelper {
  val ATTRS_JOIN_STR          = ";"
  val ATTR_SPLITTER: Splitter = Splitter.on(ATTRS_JOIN_STR).omitEmptyStrings()
}

case class HtmlTask(i: Int, e: Int, t: String, x: String, attributes: String, textContent: String) extends WebTask(i, e, t, x) {

  def getAttributes: List[Attribute] = HtmlTaskHelper.ATTR_SPLITTER.splitToList(attributes).asScala.map(Attribute.fromString).toList

}

case class JsTask(i: Int, e: Int, t: String, x: String /*,conditions: List[Condition], action: Action*/) extends WebTask(i, e, t, x) {
  def conditions: List[Condition] = List.empty

  def action: Action = null
}


private[model] trait WebTasks extends WebExercises {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val htmlTasks = TableQuery[HtmlTasksTable]

  val jsTasks = TableQuery[JsTasksTable]

  abstract class WebTasksTable[T <: WebTask](tag: Tag, name: String) extends Table[T](tag, name) {

    def id = column[Int]("TASK_ID")

    def exerciseId = column[Int]("EXERCISE_ID")

    def text = column[String]("TEXT")

    def xpathQuery = column[String]("XPATH_QUERY")


    def pk = primaryKey("PK", (id, exerciseId))


    def exerciseFk = foreignKey("EXERCISE_FK", exerciseId, webExercises)(_.id)

  }

  class HtmlTasksTable(tag: Tag) extends WebTasksTable[HtmlTask](tag, "HTML_TASKS") {

    def attributes = column[String]("ATTRIBUTES")

    def textContent = column[String]("TEXT_CONTENT")


    def * = (id, exerciseId, text, xpathQuery, attributes, textContent) <> (HtmlTask.tupled, HtmlTask.unapply)

  }

  class JsTasksTable(tag: Tag) extends WebTasksTable[JsTask](tag, "JS_TASKS") {

    def * = (id, exerciseId, text, xpathQuery) <> (JsTask.tupled, JsTask.unapply)

  }


}