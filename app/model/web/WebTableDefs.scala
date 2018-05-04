package model.web

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class WebTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[WebExercise, WebCompleteEx, WebSolution, WebExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = WebExercisesTable

  override protected type SolTableDef = WebSolutionsTable

  // Table queries

  override protected val exTable  = TableQuery[WebExercisesTable]
  override protected val solTable = TableQuery[WebSolutionsTable]

  private val htmlTasks  = TableQuery[HtmlTasksTable]
  private val attributes = TableQuery[AttributesTable]
  private val jsTasks    = TableQuery[JsTasksTable]
  private val conditions = TableQuery[ConditionsTable]


  // Dependent query tables

  lazy val webSolutions = TableQuery[WebSolutionsTable]

  override def completeExForEx(ex: WebExercise): Future[WebCompleteEx] = for {
    htmlTasks <- htmlTasksForExercise(ex.id)
    jsTasks <- jsTasksForExercise(ex.id)
  } yield WebCompleteEx(ex, htmlTasks sortBy (_.task.id), jsTasks sortBy (_.task.id))

  private def htmlTasksForExercise(exId: Int): Future[Seq[HtmlCompleteTask]] = db.run(htmlTasks.filter(_.exerciseId === exId).result) flatMap { htmlTs: Seq[HtmlTask] =>
    Future.sequence(htmlTs map { htmlTask =>
      db.run(attributes.filter(att => att.exerciseId === exId && att.taskId === htmlTask.id).result) map {
        atts => HtmlCompleteTask(htmlTask, atts)
      }
    })
  }

  private def jsTasksForExercise(exId: Int): Future[Seq[JsCompleteTask]] = db.run(jsTasks.filter(_.exerciseId === exId).result) flatMap { jsTs: Seq[JsTask] =>
    Future.sequence(jsTs map { jsTask =>
      db.run(conditions.filter(cond => cond.exerciseId === exId && cond.taskId === jsTask.id).result) map {
        conds => JsCompleteTask(jsTask, conds)
      }
    })
  }

  // Saving

  override def saveExerciseRest(compEx: WebCompleteEx): Future[Boolean] = for {
    htmlTasksSaved <- saveSeq[HtmlCompleteTask](compEx.htmlTasks, saveHtmlTask)
    jsTasksSaved <- saveSeq[JsCompleteTask](compEx.jsTasks, saveJsTask)
  } yield htmlTasksSaved && jsTasksSaved

  private def saveHtmlTask(htmlTask: HtmlCompleteTask): Future[Boolean] = db.run(htmlTasks += htmlTask.task) flatMap { _ =>
    saveSeq[Attribute](htmlTask.attributes, a => db.run(attributes += a))
  }

  private def saveJsTask(jsTask: JsCompleteTask): Future[Boolean] = db.run(jsTasks += jsTask.task) flatMap { _ =>
    saveSeq[JsCondition](jsTask.conditions, c => db.run(conditions += c))
  }

  // Implicit column types

  private implicit val ActionTypeColumnType: BaseColumnType[JsActionType] =
    MappedColumnType.base[JsActionType, String](_.entryName, str => JsActionType.withNameInsensitiveOption(str) getOrElse JsActionType.CLICK)

  override protected implicit val partTypeColumnType: BaseColumnType[WebExPart] =
    MappedColumnType.base[WebExPart, String](_.urlName, str => WebExParts.values.find(_.urlName == str) getOrElse HtmlPart)

  // Table definitions

  class WebExercisesTable(tag: Tag) extends HasBaseValuesTable[WebExercise](tag, "web_exercises") {

    def htmlText = column[String]("html_text")

    def jsText = column[String]("js_text")

    def phpText = column[String]("php_text")


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, htmlText.?, jsText.?, phpText.?) <> (WebExercise.tupled, WebExercise.unapply)

  }

  abstract class WebTasksTable[T <: WebTask](tag: Tag, name: String) extends Table[T](tag, name) {

    def id = column[Int]("task_id")

    def exerciseId = column[Int]("exercise_id")

    def text = column[String]("text")

    def xpathQuery = column[String]("xpath_query")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)

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

  class WebSolutionsTable(tag: Tag) extends PartSolutionsTable[WebSolution](tag, "web_solutions") {

    def solution = column[String]("solution")


    override def * = (username, exerciseId, part, solution) <> (WebSolution.tupled, WebSolution.unapply)

  }

}
