package model.web

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}
import model.web.WebConsts._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class WebTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[WebExercise, WebCompleteEx, String, WebSolution, WebExPart, WebExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = WebExercisesTable
  override protected type SolTableDef = WebSolutionsTable
  override protected type ReviewsTableDef = WebExerciseReviewsTable

  // Table queries

  override protected val exTable      = TableQuery[WebExercisesTable]
  override protected val solTable     = TableQuery[WebSolutionsTable]
  override protected val reviewsTable = TableQuery[WebExerciseReviewsTable]

  private val htmlTasksTable  = TableQuery[HtmlTasksTable]
  private val attributesTable = TableQuery[AttributesTable]
  private val jsTasksTable    = TableQuery[JsTasksTable]
  private val conditionsTable = TableQuery[ConditionsTable]

  private val sampleSolutionsTable = TableQuery[WebSampleSolutionsTable]

  // Dependent query tables

  lazy val webSolutions = TableQuery[WebSolutionsTable]

  // Other queries

  override def futureUserCanSolvePartOfExercise(username: String, exId: Int, exSemVer: SemanticVersion, part: WebExPart): Future[Boolean] = part match {
    case WebExParts.HtmlPart => Future(true)
    case WebExParts.JsPart   => futureOldSolution(username, exId, exSemVer, WebExParts.HtmlPart).map(_.exists(r => r.points == r.maxPoints))
    case WebExParts.PHPPart  => Future(false)
  }

  override def completeExForEx(ex: WebExercise): Future[WebCompleteEx] = for {
    htmlTasks <- htmlTasksForExercise(ex.id)
    jsTasks <- jsTasksForExercise(ex.id)
    sampleSolutions <- db.run(sampleSolutionsTable filter (s => s.exerciseId === ex.id && s.exSemVer === ex.semanticVersion) result)
  } yield WebCompleteEx(ex, htmlTasks sortBy (_.task.id), jsTasks sortBy (_.task.id), sampleSolutions = sampleSolutions)

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

  override protected def copyDBSolType(oldSol: WebSolution, newId: Int): WebSolution = oldSol.copy(id = newId)

  // Saving

  override def saveExerciseRest(compEx: WebCompleteEx): Future[Boolean] = for {
    htmlTasksSaved <- saveSeq[HtmlCompleteTask](compEx.htmlTasks, saveHtmlTask)
    jsTasksSaved <- saveSeq[JsCompleteTask](compEx.jsTasks, saveJsTask)

    sampleSolutionsSaved <- saveSeq[WebSampleSolution](compEx.sampleSolutions, s => db.run(sampleSolutionsTable += s))
  } yield htmlTasksSaved && jsTasksSaved && sampleSolutionsSaved

  private def saveHtmlTask(htmlTask: HtmlCompleteTask): Future[Boolean] = db.run(htmlTasksTable += htmlTask.task) flatMap { _ =>
    saveSeq[Attribute](htmlTask.attributes, a => db.run(attributesTable += a))
  }

  private def saveJsTask(jsTask: JsCompleteTask): Future[Boolean] = db.run(jsTasksTable += jsTask.task) flatMap { _ =>
    saveSeq[JsCondition](jsTask.conditions, c => db.run(conditionsTable += c))
  }

  // Implicit column types

  private implicit val actionTypeColumnType: BaseColumnType[JsActionType] =
    MappedColumnType.base[JsActionType, String](_.entryName, str => JsActionType.withNameInsensitiveOption(str) getOrElse JsActionType.CLICK)

  override protected implicit val partTypeColumnType: BaseColumnType[WebExPart] =
    MappedColumnType.base[WebExPart, String](_.entryName, WebExParts.withNameInsensitive)

  // Table definitions

  class WebExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "web_exercises") {

    def htmlText: Rep[String] = column[String]("html_text")

    def jsText: Rep[String] = column[String]("js_text")

    def phpText: Rep[String] = column[String]("php_text")


    override def * : ProvenShape[WebExercise] = (id, semanticVersion, title, author, text, state, htmlText.?, jsText.?, phpText.?) <> (WebExercise.tupled, WebExercise.unapply)

  }

  abstract class WebTasksTable[T <: WebTask](tag: Tag, name: String) extends ExForeignKeyTable[T](tag, name) {

    def id: Rep[Int] = column[Int]("task_id")

    def text: Rep[String] = column[String]("text")

    def xpathQuery: Rep[String] = column[String]("xpath_query")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))

  }

  class HtmlTasksTable(tag: Tag) extends WebTasksTable[HtmlTask](tag, "html_tasks") {

    def textContent: Rep[String] = column[String]("text_content")


    override def * : ProvenShape[HtmlTask] = (id, exerciseId, exSemVer, text, xpathQuery, textContent.?) <> (HtmlTask.tupled, HtmlTask.unapply)

  }

  class AttributesTable(tag: Tag) extends Table[Attribute](tag, "html_attributes") {

    def key: Rep[String] = column[String]("attr_key")

    def value: Rep[String] = column[String]("attr_value")

    def taskId: Rep[Int] = column[Int]("task_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")


    def pk: PrimaryKey = primaryKey("pk", (key, taskId, exerciseId, exSemVer))

    def taskFk: ForeignKeyQuery[HtmlTasksTable, HtmlTask] = foreignKey("task_fk", (taskId, exerciseId, exSemVer), htmlTasksTable)(t => (t.id, t.exerciseId, t.exSemVer))


    override def * : ProvenShape[Attribute] = (key, taskId, exerciseId, exSemVer, value) <> (Attribute.tupled, Attribute.unapply)

  }

  class JsTasksTable(tag: Tag) extends WebTasksTable[JsTask](tag, "js_tasks") {

    def actionType: Rep[JsActionType] = column[JsActionType]("action_type")

    def keysToSend: Rep[String] = column[String]("keys_to_send")


    override def * : ProvenShape[JsTask] = (id, exerciseId, exSemVer, text, xpathQuery, actionType, keysToSend.?) <> (JsTask.tupled, JsTask.unapply)

  }

  class ConditionsTable(tag: Tag) extends Table[JsCondition](tag, "js_conditions") {

    def conditionId: Rep[Int] = column[Int]("condition_id")

    def taskId: Rep[Int] = column[Int]("task_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def xpathQuery: Rep[String] = column[String]("xpath_query")

    def isPrecondition: Rep[Boolean] = column[Boolean]("is_precondition")

    def awaitedValue: Rep[String] = column[String]("awaited_value")


    def pk: PrimaryKey = primaryKey("pk", (conditionId, taskId, exerciseId, exSemVer))

    def taskFk: ForeignKeyQuery[JsTasksTable, JsTask] = foreignKey("task_fk", (taskId, exerciseId, exSemVer), jsTasksTable)(t => (t.id, t.exerciseId, t.exSemVer))


    override def * : ProvenShape[JsCondition] = (conditionId, taskId, exerciseId, exSemVer, xpathQuery, isPrecondition, awaitedValue) <> (JsCondition.tupled, JsCondition.unapply)

  }

  class WebSampleSolutionsTable(tag: Tag) extends ExForeignKeyTable[WebSampleSolution](tag, "web_sample_solutions") {

    def id: Rep[Int] = column[Int](idName)

    def htmlSample: Rep[String] = column[String]("html_sample")

    def jsSample: Rep[String] = column[String]("js_sample")

    def phpSample: Rep[String] = column[String]("php_sample")

    def * : ProvenShape[WebSampleSolution] = (id, exerciseId, exSemVer, htmlSample.?, jsSample.?, phpSample.?) <> (WebSampleSolution.tupled, WebSampleSolution.unapply)

  }

  class WebSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "web_solutions") {

    def solution: Rep[String] = column[String]("solution")


    override def * : ProvenShape[WebSolution] = (id, username, exerciseId, exSemVer, part, solution, points, maxPoints) <> (WebSolution.tupled, WebSolution.unapply)

  }

  class WebExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "web_exercise_reviews") {

    override def * : ProvenShape[WebExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (WebExerciseReview.tupled, WebExerciseReview.unapply)

  }

}
