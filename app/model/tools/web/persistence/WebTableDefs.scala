package model.tools.web.persistence

import de.uniwue.webtester.JsActionType
import model.persistence.{DbExerciseFile, DbFilesUserSolution, FilesSolutionExerciseTableDefs}
import model.tools.web.WebConsts._
import model.tools.web._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.ExecutionContext

class WebTableDefs @javax.inject.Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with FilesSolutionExerciseTableDefs[WebExPart, WebExercise, WebCollection, WebExerciseReview]
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

  protected val htmlTasksTable : TableQuery[HtmlTasksTable]  = TableQuery[HtmlTasksTable]
  protected val attributesTable: TableQuery[AttributesTable] = TableQuery[AttributesTable]

  protected val jsTasksTable                   : TableQuery[JsTasksTable]               = TableQuery[JsTasksTable]
  protected val jsConditionsTableQuery         : TableQuery[ConditionsTable]            = TableQuery[ConditionsTable]
  protected val jsConditionAttributesTableQuery: TableQuery[JsConditionAttributesTable] = TableQuery[JsConditionAttributesTable]

  protected val webFilesTableQuery: TableQuery[WebFilesTable] = TableQuery[WebFilesTable]

  // Helper methods

  override protected val dbModels               = WebDbModels
  override protected val exerciseReviewDbModels = WebExerciseReviewDbModels


  override protected def copyDbUserSolType(oldSol: DbFilesUserSolution[WebExPart], newId: Int): DbFilesUserSolution[WebExPart] = oldSol.copy(id = newId)

  override protected def exDbValuesFromExercise(collId: Int, compEx: WebExercise): DbWebExercise =
    dbModels.dbExerciseFromExercise(collId, compEx)

  // Implicit column types

  private implicit val actionTypeColumnType: BaseColumnType[JsActionType] =
    MappedColumnType.base[JsActionType, String](_.entryName, JsActionType.withNameInsensitive)

  override protected implicit val partTypeColumnType: BaseColumnType[WebExPart] =
    MappedColumnType.base[WebExPart, String](_.entryName, WebExParts.withNameInsensitive)

  // Table definitions

  class WebCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "web_collections") {

    override def * : ProvenShape[WebCollection] = (id, title, author, text, state, shortName) <> (WebCollection.tupled, WebCollection.unapply)

  }

  class WebExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "web_exercises") {

    def htmlText: Rep[Option[String]] = column[Option[String]]("html_text")

    def jsText: Rep[Option[String]] = column[Option[String]]("js_text")

    def fileName: Rep[String] = column[String](filenameName)


    override def * : ProvenShape[DbWebExercise] = (id, semanticVersion, collectionId, title, author, text, state, htmlText, jsText, fileName) <> (DbWebExercise.tupled, DbWebExercise.unapply)

  }

  abstract class WebTasksTable[T <: DbWebTask](tag: Tag, name: String) extends ExForeignKeyTable[T](tag, name) {

    def id: Rep[Int] = column[Int]("task_id")

    def text: Rep[String] = column[String]("text")

    def xpathQuery: Rep[String] = column[String]("xpath_query")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, collectionId))

  }

  class HtmlTasksTable(tag: Tag) extends WebTasksTable[DbHtmlTask](tag, "html_tasks") {

    def awaitedTagname: Rep[String] = column[String]("awaited_tagname")

    def textContent: Rep[Option[String]] = column[Option[String]]("text_content")


    override def * : ProvenShape[DbHtmlTask] = (id, exerciseId, collectionId, text, xpathQuery, awaitedTagname, textContent) <> (DbHtmlTask.tupled, DbHtmlTask.unapply)

  }

  class AttributesTable(tag: Tag) extends Table[DbHtmlAttribute](tag, "html_attributes") {

    def key: Rep[String] = column[String]("attr_key")

    def taskId: Rep[Int] = column[Int]("task_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def value: Rep[String] = column[String]("attr_value")


    def pk: PrimaryKey = primaryKey("pk", (key, taskId, exerciseId))

    def taskFk: ForeignKeyQuery[HtmlTasksTable, DbHtmlTask] = foreignKey("task_fk", (taskId, exerciseId), htmlTasksTable)(t => (t.id, t.exerciseId))


    override def * : ProvenShape[DbHtmlAttribute] = (key, taskId, exerciseId, collectionId, value) <> (DbHtmlAttribute.tupled, DbHtmlAttribute.unapply)

  }

  class JsTasksTable(tag: Tag) extends WebTasksTable[DbJsTask](tag, "js_tasks") {

    def actionType: Rep[JsActionType] = column[JsActionType]("action_type")

    def keysToSend: Rep[String] = column[String]("keys_to_send")


    override def * : ProvenShape[DbJsTask] = (id, exerciseId, collectionId, text, xpathQuery, actionType, keysToSend.?) <> (DbJsTask.tupled, DbJsTask.unapply)

  }

  class ConditionsTable(tag: Tag) extends Table[DbJsCondition](tag, "js_conditions") {

    def id: Rep[Int] = column[Int]("condition_id")

    def taskId: Rep[Int] = column[Int]("task_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def isPrecondition: Rep[Boolean] = column[Boolean]("is_precondition")

    def awaitedTagname: Rep[String] = column[String]("awaited_tagname")

    def xpathQuery: Rep[String] = column[String]("xpath_query")

    def awaitedTextContent: Rep[Option[String]] = column[Option[String]]("awaited_value")


    def pk: PrimaryKey = primaryKey("pk", (id, taskId, exerciseId, collectionId, isPrecondition))

    def taskFk: ForeignKeyQuery[JsTasksTable, DbJsTask] = foreignKey("task_fk", (taskId, exerciseId, collectionId),
      jsTasksTable)(t => (t.id, t.exerciseId, t.collectionId))


    override def * : ProvenShape[DbJsCondition] = (id, taskId, exerciseId, collectionId, isPrecondition, xpathQuery,
      awaitedTagname, awaitedTextContent) <> (DbJsCondition.tupled, DbJsCondition.unapply)

  }

  class JsConditionAttributesTable(tag: Tag) extends Table[DbJsConditionAttribute](tag, "web_js_condition_attributes") {

    def key: Rep[String] = column[String]("attr_key")

    def condId: Rep[Int] = column[Int]("cond_id")

    def taskId: Rep[Int] = column[Int]("task_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")


    def collectionId: Rep[Int] = column[Int]("collection_id")

    def isPrecondition: Rep[Boolean] = column[Boolean]("is_precondition")

    def value: Rep[String] = column[String]("attr_value")


    def pk: PrimaryKey = primaryKey("pk", (key, condId, taskId, exerciseId, collectionId, isPrecondition))

    def condFk: ForeignKeyQuery[ConditionsTable, DbJsCondition] = foreignKey("cond_fk", (condId, taskId, exerciseId, collectionId, isPrecondition),
      jsConditionsTableQuery)(jc => (jc.id, jc.taskId, jc.exerciseId, jc.collectionId, jc.isPrecondition))


    override def * : ProvenShape[DbJsConditionAttribute] = (condId, taskId, exerciseId, collectionId, isPrecondition, key, value) <> (DbJsConditionAttribute.tupled, DbJsConditionAttribute.unapply)

  }

  class WebFilesTable(tag: Tag) extends ExForeignKeyTable[DbExerciseFile](tag, "web_files") with ExerciseFilesTable[DbExerciseFile] {

    def * : ProvenShape[DbExerciseFile] = (name, exerciseId, collectionId, content, fileType, editable) <> (DbExerciseFile.tupled, DbExerciseFile.unapply)

  }


  class WebSampleSolutionsTable(tag: Tag) extends AFilesSampleSolutionsTable(tag, "web_sample_solutions")

  class WebSampleSolutionFilesTable(tag: Tag) extends AFilesSampleSolutionFilesTable(tag, "web_sample_solution_files")


  class WebUserSolutionsTable(tag: Tag) extends AFilesUserSolutionsTable(tag, "web_user_solutions")

  class WebUserSolutionFilesTable(tag: Tag) extends AFilesUserSolutionFilesTable(tag, "web_user_solution_files")


  class WebExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "web_exercise_reviews") {

    override def * : ProvenShape[DbWebExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbWebExerciseReview.tupled, DbWebExerciseReview.unapply)

  }

}
