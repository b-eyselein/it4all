package model.web

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class WebTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[WebExercise, WebCompleteEx, String, WebSolution, WebExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = WebExercisesTable

  override protected type SolTableDef = WebSolutionsTable

  // Table queries

  override protected val exTable  = TableQuery[WebExercisesTable]
  override protected val solTable = TableQuery[WebSolutionsTable]

  private val htmlTasksTable  = TableQuery[HtmlTasksTable]
  private val attributesTable = TableQuery[AttributesTable]
  private val jsTasksTable    = TableQuery[JsTasksTable]
  private val conditionsTable = TableQuery[ConditionsTable]

  // Dependent query tables

  lazy val webSolutions = TableQuery[WebSolutionsTable]

  // Other queries

  override def futureUserCanSolvePartOfExercise(username: String, exerciseId: Int, part: WebExPart): Future[Boolean] = part match {
    case WebExParts.HtmlPart => Future(true)
    case WebExParts.JsPart   => futureOldSolution(username, exerciseId, WebExParts.HtmlPart).map(_.exists(r => r.points == r.maxPoints))
    case WebExParts.PHPPart  => Future(false)
  }


  override def completeExForEx(ex: WebExercise): Future[WebCompleteEx] = for {
    htmlTasks <- htmlTasksForExercise(ex.id)
    jsTasks <- jsTasksForExercise(ex.id)
  } yield WebCompleteEx(ex, htmlTasks sortBy (_.task.id), jsTasks sortBy (_.task.id))

  private def htmlTasksForExercise(exId: Int): Future[Seq[HtmlCompleteTask]] =
    db.run(htmlTasksTable.filter(_.exerciseId === exId).result) flatMap { htmlTasks: Seq[HtmlTask] =>
      Future.sequence(htmlTasks map { htmlTask =>
        db.run(attributesTable.filter(att => att.exerciseId === exId && att.taskId === htmlTask.id).result) map {
          attributes => HtmlCompleteTask(htmlTask, attributes)
        }
      })
    }

  private def jsTasksForExercise(exId: Int): Future[Seq[JsCompleteTask]] = db.run(jsTasksTable.filter(_.exerciseId === exId).result) flatMap { jsTs: Seq[JsTask] =>
    Future.sequence(jsTs map { jsTask =>
      db.run(conditionsTable.filter(cond => cond.exerciseId === exId && cond.taskId === jsTask.id).result) map {
        conditions => JsCompleteTask(jsTask, conditions)
      }
    })
  }

  // Saving

  override def saveExerciseRest(compEx: WebCompleteEx): Future[Boolean] = for {
    htmlTasksSaved <- saveSeq[HtmlCompleteTask](compEx.htmlTasks, saveHtmlTask)
    jsTasksSaved <- saveSeq[JsCompleteTask](compEx.jsTasks, saveJsTask)
  } yield htmlTasksSaved && jsTasksSaved

  private def saveHtmlTask(htmlTask: HtmlCompleteTask): Future[Boolean] = db.run(htmlTasksTable += htmlTask.task) flatMap { _ =>
    saveSeq[Attribute](htmlTask.attributes, a => db.run(attributesTable += a))
  }

  private def saveJsTask(jsTask: JsCompleteTask): Future[Boolean] = db.run(jsTasksTable += jsTask.task) flatMap { _ =>
    saveSeq[JsCondition](jsTask.conditions, c => db.run(conditionsTable += c))
  }

  // Implicit column types

  private implicit val ActionTypeColumnType: BaseColumnType[JsActionType] =
    MappedColumnType.base[JsActionType, String](_.entryName, str => JsActionType.withNameInsensitiveOption(str) getOrElse JsActionType.CLICK)

  override protected implicit val partTypeColumnType: BaseColumnType[WebExPart] =
    MappedColumnType.base[WebExPart, String](_.entryName, WebExParts.withNameInsensitive)

  // Table definitions

  class WebExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "web_exercises") {

    def htmlText = column[String]("html_text")

    def jsText = column[String]("js_text")

    def phpText = column[String]("php_text")


    override def * = (id, semanticVersion, title, author, text, state, htmlText.?, jsText.?, phpText.?).mapTo[WebExercise]

  }

  abstract class WebTasksTable[T <: WebTask](tag: Tag, name: String) extends Table[T](tag, name) {

    def id = column[Int]("task_id")

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def text = column[String]("text")

    def xpathQuery = column[String]("xpath_query")


    def pk = primaryKey("pk", (id, exerciseId, exSemVer))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))

  }

  class HtmlTasksTable(tag: Tag) extends WebTasksTable[HtmlTask](tag, "html_tasks") {

    def textContent = column[String]("text_content")


    override def * = (id, exerciseId, exSemVer, text, xpathQuery, textContent.?).mapTo[HtmlTask]

  }

  class AttributesTable(tag: Tag) extends Table[Attribute](tag, "html_attributes") {

    def key = column[String]("attr_key")

    def value = column[String]("attr_value")

    def taskId = column[Int]("task_id")

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")


    def pk = primaryKey("pk", (key, taskId, exerciseId, exSemVer))

    def taskFk = foreignKey("task_fk", (taskId, exerciseId, exSemVer), htmlTasksTable)(t => (t.id, t.exerciseId, t.exSemVer))


    override def * = (key, taskId, exerciseId, exSemVer, value).mapTo[Attribute]

  }

  class JsTasksTable(tag: Tag) extends WebTasksTable[JsTask](tag, "js_tasks") {

    def actionType = column[JsActionType]("action_type")

    def keysToSend = column[String]("keys_to_send")


    override def * = (id, exerciseId, exSemVer, text, xpathQuery, actionType, keysToSend.?).mapTo[JsTask]

  }

  class ConditionsTable(tag: Tag) extends Table[JsCondition](tag, "js_conditions") {

    def conditionId = column[Int]("condition_id")

    def taskId = column[Int]("task_id")

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def xpathQuery = column[String]("xpath_query")

    def isPrecondition = column[Boolean]("is_precondition")

    def awaitedValue = column[String]("awaited_value")


    def pk = primaryKey("pk", (conditionId, taskId, exerciseId, exSemVer))

    def taskFk = foreignKey("task_fk", (taskId, exerciseId, exSemVer), jsTasksTable)(t => (t.id, t.exerciseId, t.exSemVer))


    override def * = (conditionId, taskId, exerciseId, exSemVer, xpathQuery, isPrecondition, awaitedValue).mapTo[JsCondition]

  }

  class WebSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "web_solutions") {

    def solution = column[String]("solution")


    override def * = (username, exerciseId, exSemVer, part, solution, points, maxPoints).mapTo[WebSolution]

  }

}
