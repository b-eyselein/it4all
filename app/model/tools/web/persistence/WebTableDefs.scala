package model.tools.web.persistence

import de.uniwue.webtester.JsActionType
import model.SemanticVersion
import model.persistence.ExerciseTableDefs
import model.tools.web._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

class WebTableDefs @javax.inject.Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseTableDefs[WebExPart, WebExercise, WebCollection, WebSolution, WebSampleSolution, WebUserSolution, WebExerciseReview]
    with WebTableQueries {

  import profile.api._

  // Abstract types

  override protected type CollTableDef = WebCollectionsTable


  override protected type DbExType = DbWebExercise

  override protected type ExTableDef = WebExercisesTable


  override protected type DbSampleSolType = DbWebSampleSolution

  override protected type DbSampleSolTable = WebSampleSolutionsTable


  override protected type DbUserSolType = DbWebUserSolution

  override protected type DbUserSolTable = WebSolutionsTable


  override protected type DbReviewType = DbWebExerciseReview

  override protected type ReviewsTable = WebExerciseReviewsTable

  // Table queries

  override protected val exTable  : TableQuery[WebExercisesTable]   = TableQuery[WebExercisesTable]
  override protected val collTable: TableQuery[WebCollectionsTable] = TableQuery[WebCollectionsTable]

  override protected val sampleSolutionsTableQuery: TableQuery[WebSampleSolutionsTable] = TableQuery[WebSampleSolutionsTable]
  override protected val userSolutionsTableQuery  : TableQuery[WebSolutionsTable]       = TableQuery[WebSolutionsTable]

  override protected val reviewsTable: TableQuery[WebExerciseReviewsTable] = TableQuery[WebExerciseReviewsTable]

  protected val htmlTasksTable : TableQuery[HtmlTasksTable]  = TableQuery[HtmlTasksTable]
  protected val attributesTable: TableQuery[AttributesTable] = TableQuery[AttributesTable]

  protected val jsTasksTable                   : TableQuery[JsTasksTable]               = TableQuery[JsTasksTable]
  protected val jsConditionsTableQuery         : TableQuery[ConditionsTable]            = TableQuery[ConditionsTable]
  protected val jsConditionAttributesTableQuery: TableQuery[JsConditionAttributesTable] = TableQuery[JsConditionAttributesTable]

  // Helper methods

  override protected val dbModels               = WebDbModels
  override protected val solutionDbModels       = WebSolutionDbModels
  override protected val exerciseReviewDbModels = WebExerciseReviewDbModels

  override protected def copyDbUserSolType(oldSol: DbWebUserSolution, newId: Int): DbWebUserSolution = oldSol.copy(id = newId)

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

    def fileName: Rep[String] = column[String]("file_name")


    override def * : ProvenShape[DbWebExercise] = (id, semanticVersion, collectionId, title, author, text, state, htmlText, jsText, fileName) <> (DbWebExercise.tupled, DbWebExercise.unapply)

  }

  abstract class WebTasksTable[T <: DbWebTask](tag: Tag, name: String) extends ExForeignKeyTable[T](tag, name) {

    def id: Rep[Int] = column[Int]("task_id")

    def text: Rep[String] = column[String]("text")

    def xpathQuery: Rep[String] = column[String]("xpath_query")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, collectionId))

  }

  class HtmlTasksTable(tag: Tag) extends WebTasksTable[DbHtmlTask](tag, "html_tasks") {

    def awaitedTagname: Rep[String] = column[String]("awaited_tagname")

    def textContent: Rep[Option[String]] = column[Option[String]]("text_content")


    override def * : ProvenShape[DbHtmlTask] = (id, exerciseId, exSemVer, collectionId, text, xpathQuery, awaitedTagname, textContent) <> (DbHtmlTask.tupled, DbHtmlTask.unapply)

  }

  class AttributesTable(tag: Tag) extends Table[DbHtmlAttribute](tag, "html_attributes") {

    def key: Rep[String] = column[String]("attr_key")

    def taskId: Rep[Int] = column[Int]("task_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def value: Rep[String] = column[String]("attr_value")


    def pk: PrimaryKey = primaryKey("pk", (key, taskId, exerciseId, exSemVer))

    def taskFk: ForeignKeyQuery[HtmlTasksTable, DbHtmlTask] = foreignKey("task_fk", (taskId, exerciseId, exSemVer), htmlTasksTable)(t => (t.id, t.exerciseId, t.exSemVer))


    override def * : ProvenShape[DbHtmlAttribute] = (key, taskId, exerciseId, exSemVer, collectionId, value) <> (DbHtmlAttribute.tupled, DbHtmlAttribute.unapply)

  }

  class JsTasksTable(tag: Tag) extends WebTasksTable[DbJsTask](tag, "js_tasks") {

    def actionType: Rep[JsActionType] = column[JsActionType]("action_type")

    def keysToSend: Rep[String] = column[String]("keys_to_send")


    override def * : ProvenShape[DbJsTask] = (id, exerciseId, exSemVer, collectionId, text, xpathQuery, actionType, keysToSend.?) <> (DbJsTask.tupled, DbJsTask.unapply)

  }

  class ConditionsTable(tag: Tag) extends Table[DbJsCondition](tag, "js_conditions") {

    def id: Rep[Int] = column[Int]("condition_id")

    def taskId: Rep[Int] = column[Int]("task_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def isPrecondition: Rep[Boolean] = column[Boolean]("is_precondition")

    def awaitedTagname: Rep[String] = column[String]("awaited_tagname")

    def xpathQuery: Rep[String] = column[String]("xpath_query")

    def awaitedTextContent: Rep[Option[String]] = column[Option[String]]("awaited_value")


    def pk: PrimaryKey = primaryKey("pk", (id, taskId, exerciseId, exSemVer, collectionId, isPrecondition))

    def taskFk: ForeignKeyQuery[JsTasksTable, DbJsTask] = foreignKey("task_fk", (taskId, exerciseId, exSemVer, collectionId),
      jsTasksTable)(t => (t.id, t.exerciseId, t.exSemVer, t.collectionId))


    override def * : ProvenShape[DbJsCondition] = (id, taskId, exerciseId, exSemVer, collectionId, isPrecondition, xpathQuery,
      awaitedTagname, awaitedTextContent) <> (DbJsCondition.tupled, DbJsCondition.unapply)

  }

  class JsConditionAttributesTable(tag: Tag) extends Table[DbJsConditionAttribute](tag, "web_js_condition_attributes") {

    def key: Rep[String] = column[String]("attr_key")

    def condId: Rep[Int] = column[Int]("cond_id")

    def taskId: Rep[Int] = column[Int]("task_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def isPrecondition: Rep[Boolean] = column[Boolean]("is_precondition")

    def value: Rep[String] = column[String]("attr_value")


    def pk: PrimaryKey = primaryKey("pk", (key, condId, taskId, exerciseId, exSemVer, collectionId, isPrecondition))

    def condFk: ForeignKeyQuery[ConditionsTable, DbJsCondition] = foreignKey("cond_fk", (condId, taskId, exerciseId, exSemVer, collectionId, isPrecondition),
      jsConditionsTableQuery)(jc => (jc.id, jc.taskId, jc.exerciseId, jc.exSemVer, jc.collectionId, jc.isPrecondition))


    override def * = (condId, taskId, exerciseId, exSemVer, collectionId, isPrecondition, key, value) <> (DbJsConditionAttribute.tupled, DbJsConditionAttribute.unapply)

  }

  class WebSampleSolutionsTable(tag: Tag) extends ASampleSolutionsTable(tag, "web_sample_solutions") {

    def htmlSample: Rep[String] = column[String]("html_sample")

    def jsSample: Rep[Option[String]] = column[Option[String]]("js_sample")


    def * : ProvenShape[DbWebSampleSolution] = (id, exerciseId, exSemVer, collectionId, htmlSample, jsSample) <> (DbWebSampleSolution.tupled, DbWebSampleSolution.unapply)

  }

  class WebSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "web_solutions") {

    def htmlSolution: Rep[String] = column[String]("html_solution")

    def jsSolution: Rep[Option[String]] = column[Option[String]]("js_solution")

    override def * : ProvenShape[DbWebUserSolution] = (id, exerciseId, exSemVer, collectionId, username, part,
      htmlSolution, jsSolution, points, maxPoints) <> (DbWebUserSolution.tupled, DbWebUserSolution.unapply)

  }

  class WebExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "web_exercise_reviews") {

    override def * : ProvenShape[DbWebExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbWebExerciseReview.tupled, DbWebExerciseReview.unapply)

  }

}
