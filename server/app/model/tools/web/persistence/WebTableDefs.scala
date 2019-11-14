package model.tools.web.persistence

import de.uniwue.webtester.{HtmlElementSpec, JsActionType}
import javax.inject.Inject
import model.ExParts
import model.persistence.{DbExerciseFile, DbFilesUserSolution, FilesSolutionExerciseTableDefs}
import model.tools.web.WebConsts._
import model.tools.web._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

class WebTableDefs @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with FilesSolutionExerciseTableDefs[WebExPart, WebExercise, WebExerciseReview]
    with WebTableQueries {

  import profile.api._

  // Abstract types

  override protected type CollTableDef = WebCollectionsTable


  override protected type DbExType = DbWebExercise

  override protected type ExTableDef = WebExercisesTable


  override protected type DbSampleSolTable = WebSampleSolutionsTable

  override protected type DbFilesSampleSolutionFilesTable = WebSampleSolutionFilesTable


  override protected type DbUserSolTable = WebUserSolutionsTable

  override protected type DbFilesUserSolutionFilesTable = WebUserSolutionFilesTable


  override protected type DbReviewType = DbWebExerciseReview

  override protected type ReviewsTable = WebExerciseReviewsTable

  // Table queries

  override protected val exTable  : TableQuery[WebExercisesTable]   = TableQuery[WebExercisesTable]
  override protected val collTable: TableQuery[WebCollectionsTable] = TableQuery[WebCollectionsTable]

  override protected val sampleSolutionsTableQuery    : TableQuery[WebSampleSolutionsTable]     = TableQuery[WebSampleSolutionsTable]
  override protected val sampleSolutionFilesTableQuery: TableQuery[WebSampleSolutionFilesTable] = TableQuery[WebSampleSolutionFilesTable]

  override protected val userSolutionsTableQuery    : TableQuery[WebUserSolutionsTable]     = TableQuery[WebUserSolutionsTable]
  override protected val userSolutionFilesTableQuery: TableQuery[WebUserSolutionFilesTable] = TableQuery[WebUserSolutionFilesTable]

  override protected val reviewsTable: TableQuery[WebExerciseReviewsTable] = TableQuery[WebExerciseReviewsTable]

  protected val htmlTasksTable: TableQuery[HtmlTasksTable] = TableQuery[HtmlTasksTable]

  protected val jsTasksTable: TableQuery[JsTasksTable] = TableQuery[JsTasksTable]

  protected val webFilesTableQuery: TableQuery[WebFilesTable] = TableQuery[WebFilesTable]

  // Helper methods

  override protected val exParts: ExParts[WebExPart] = WebExParts

  override protected val dbModels               = WebDbModels
  override protected val exerciseReviewDbModels = WebExerciseReviewDbModels


  override protected def copyDbUserSolType(oldSol: DbFilesUserSolution[WebExPart], newId: Int): DbFilesUserSolution[WebExPart] = oldSol.copy(id = newId)

  // Implicit column types

  private val actionTypeColumnType: BaseColumnType[JsActionType] =
    MappedColumnType.base[JsActionType, String](_.entryName, JsActionType.withNameInsensitive)

  private val htmlElementSpecSeqColumnType: BaseColumnType[Seq[HtmlElementSpec]] =
    jsonSeqColumnType(WebCompleteResultJsonProtocol.htmlElementSpecFormat)

  override protected implicit val partTypeColumnType: BaseColumnType[WebExPart] = jsonColumnType(exParts.jsonFormat)

  // Table definitions

  protected class WebCollectionsTable(tag: Tag) extends ExerciseCollectionsTable(tag, "web_collections")

  protected class WebExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "web_exercises") {

    def htmlText: Rep[Option[String]] = column[Option[String]]("html_text")

    def jsText: Rep[Option[String]] = column[Option[String]]("js_text")

    def fileName: Rep[String] = column[String](filenameName)


    override def * : ProvenShape[DbWebExercise] = (id, collectionId, semanticVersion, title, author, text, state, htmlText, jsText, fileName) <> (DbWebExercise.tupled, DbWebExercise.unapply)

  }

  protected abstract class WebTasksTable[T <: DbWebTask](tag: Tag, name: String) extends ExForeignKeyTable[T](tag, name) {

    def id: Rep[Int] = column[Int]("task_id")

    def text: Rep[String] = column[String]("text")

    def xpathQuery: Rep[String] = column[String]("xpath_query")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, collectionId))

  }

  protected class HtmlTasksTable(tag: Tag) extends WebTasksTable[DbHtmlTask](tag, "web_html_tasks") {

    private implicit val hact: BaseColumnType[Map[String, String]] = stringMapColumnType


    def awaitedTagname: Rep[String] = column[String]("awaited_tagname")

    def textContent: Rep[Option[String]] = column[Option[String]]("text_content")

    def attributes: Rep[Map[String, String]] = column[Map[String, String]]("attributes_json")


    override def * : ProvenShape[DbHtmlTask] = (id, exerciseId, collectionId, text, xpathQuery, awaitedTagname, textContent, attributes) <> (DbHtmlTask.tupled, DbHtmlTask.unapply)

  }

  protected class JsTasksTable(tag: Tag) extends WebTasksTable[DbJsTask](tag, "web_js_tasks") {

    private implicit val atct: BaseColumnType[JsActionType] = actionTypeColumnType

    private implicit val essf: BaseColumnType[Seq[HtmlElementSpec]] = htmlElementSpecSeqColumnType


    def actionType: Rep[JsActionType] = column[JsActionType]("action_type")

    def keysToSend: Rep[String] = column[String]("keys_to_send")

    def preConditions: Rep[Seq[HtmlElementSpec]] = column[Seq[HtmlElementSpec]]("pre_conditions_json")

    def postConditions: Rep[Seq[HtmlElementSpec]] = column[Seq[HtmlElementSpec]]("post_conditions_json")


    override def * : ProvenShape[DbJsTask] = (id, exerciseId, collectionId, text, xpathQuery, actionType, keysToSend.?, preConditions, postConditions) <> (DbJsTask.tupled, DbJsTask.unapply)

  }

  protected class WebFilesTable(tag: Tag) extends ExForeignKeyTable[DbExerciseFile](tag, "web_files") with ExerciseFilesTable[DbExerciseFile] {

    def * : ProvenShape[DbExerciseFile] = (name, exerciseId, collectionId, content, fileType, editable) <> (DbExerciseFile.tupled, DbExerciseFile.unapply)

  }


  protected class WebSampleSolutionsTable(tag: Tag) extends AFilesSampleSolutionsTable(tag, "web_sample_solutions")

  protected class WebSampleSolutionFilesTable(tag: Tag) extends AFilesSampleSolutionFilesTable(tag, "web_sample_solution_files")


  protected class WebUserSolutionsTable(tag: Tag) extends AFilesUserSolutionsTable(tag, "web_user_solutions")

  protected class WebUserSolutionFilesTable(tag: Tag) extends AFilesUserSolutionFilesTable(tag, "web_user_solution_files")


  protected class WebExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "web_exercise_reviews") {

    override def * : ProvenShape[DbWebExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbWebExerciseReview.tupled, DbWebExerciseReview.unapply)

  }

}
