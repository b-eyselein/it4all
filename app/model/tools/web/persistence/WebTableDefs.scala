package model.tools.web.persistence

import model.SemanticVersion
import model.persistence.ExerciseCollectionTableDefs
import model.tools.web._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.TypedType
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class WebTableDefs @javax.inject.Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseCollectionTableDefs[WebExercise, WebExPart, WebCollection, WebSolution, WebSampleSolution, WebUserSolution /*, WebExerciseReview*/ ] {

  import profile.api._

  // Abstract types

  override protected type CollTableDef = WebCollectionsTable


  override protected type DbExType = DbWebExercise

  override protected type ExTableDef = WebExercisesTable


  override protected type DbSampleSolType = DbWebSampleSolution

  override protected type DbSampleSolTable = WebSampleSolutionsTable


  override protected type DbUserSolType = DbWebUserSolution

  override protected type DbUserSolTable = WebSolutionsTable


  //  override protected type ReviewsTableDef = WebExerciseReviewsTable

  // Table queries

  override protected val exTable   = TableQuery[WebExercisesTable]
  override protected val collTable = TableQuery[WebCollectionsTable]
  override protected val solTable  = TableQuery[WebSolutionsTable]
  //  override protected val reviewsTable = TableQuery[WebExerciseReviewsTable]

  private val htmlTasksTable  = TableQuery[HtmlTasksTable]
  private val attributesTable = TableQuery[AttributesTable]
  private val jsTasksTable    = TableQuery[JsTasksTable]
  private val conditionsTable = TableQuery[ConditionsTable]

  private val sampleSolutionsTable = TableQuery[WebSampleSolutionsTable]

  lazy val webSolutions = TableQuery[WebSolutionsTable]

  // Helper methods

  override protected val dbModels = WebDbModels

  override protected def copyDbUserSolType(oldSol: DbWebUserSolution, newId: Int): DbWebUserSolution = oldSol.copy(id = newId)

  override protected def exDbValuesFromExercise(collId: Int, compEx: WebExercise): DbWebExercise =
    dbModels.dbExerciseFromExercise(collId, compEx)

  // Other queries

  //  override def futureUserCanSolvePartOfExercise(username: String, exId: Int, exSemVer: SemanticVersion, collId: Int, part: WebExPart): Future[Boolean] = part match {
  //    case WebExParts.HtmlPart => Future.successful(true)
  //    case WebExParts.JsPart   => futureMaybeOldSolution(username, exId, collId /* exSemVer*/, WebExParts.HtmlPart).map(_.exists(r => r.points == r.maxPoints))
  //  }

  override def completeExForEx(collId: Int, ex: DbWebExercise): Future[WebExercise] = for {
    htmlTasks <- htmlTasksForExercise(collId, ex.id)
    jsTasks <- jsTasksForExercise(collId, ex.id)
    sampleSolutions <- db.run(sampleSolutionsTable filter (s => s.exerciseId === ex.id && s.exSemVer === ex.semanticVersion) result) map (_ map (dbModels.sampleSolFromDbSampleSol))
  } yield dbModels.exerciseFromDbExercise(ex, htmlTasks sortBy (_.id), jsTasks sortBy (_.id), sampleSolutions)

  private def htmlTasksForExercise(collId: Int, exId: Int): Future[Seq[HtmlTask]] = {

    val htmlTasksForExQuery = htmlTasksTable.filter {
      ht => ht.exerciseId === exId && ht.collectionId === collId
    }.result

    def dbHtmlAttributesForTask(taskId: Int) = attributesTable.filter {
      ha => ha.taskId === taskId && ha.exerciseId === exId && ha.collectionId === collId
    }.result

    db.run(htmlTasksForExQuery) flatMap { dbHtmlTasks: Seq[DbHtmlTask] =>
      Future.sequence(dbHtmlTasks map { dbHtmlTask =>
        for {
          attributes <- db.run(dbHtmlAttributesForTask(dbHtmlTask.id))
        } yield dbModels.htmlTaskFromDbHtmlTask(dbHtmlTask, attributes)
      })
    }
  }

  private def jsTasksForExercise(collId: Int, exId: Int): Future[Seq[JsTask]] = {
    val jsTasksForExQuery = jsTasksTable.filter {
      jt => jt.exerciseId === exId && jt.collectionId === collId
    }.result

    def dbJsConditionsForTaskQuery(taskId: Int) = conditionsTable.filter {
      jc => jc.taskId === taskId && jc.exerciseId === exId && jc.collectionId === collId
    }.result

    db.run(jsTasksForExQuery) flatMap { dbJsTasks: Seq[DbJsTask] =>
      Future.sequence(dbJsTasks map { dbJsTask =>
        for {
          conditions <- db.run(dbJsConditionsForTaskQuery(dbJsTask.id))
        } yield dbModels.jsTaskFromDbJsTask(dbJsTask, conditions)
      })
    }
  }

  override def futureSampleSolutionsForExPart(collId: Int, exerciseId: Int, part: WebExPart): Future[Seq[String]] = db.run(
    sampleSolutionsTable
      .filter { s => s.exerciseId === exerciseId && s.collectionId === collId }
      .map(sample => part match {
        case WebExParts.HtmlPart => sample.htmlSample
        case WebExParts.JsPart   => sample.jsSample
      })
      .result
  )

  // Saving

  override def saveExerciseRest(collId: Int, ex: WebExercise): Future[Boolean] = {
    val dbSamples = ex.sampleSolutions map (s => dbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))


    for {
      htmlTasksSaved <- saveSeq[HtmlTask](ex.htmlTasks, t => saveHtmlTask(ex.id, ex.semanticVersion, collId, t))
      jsTasksSaved <- saveSeq[JsTask](ex.jsTasks, t => saveJsTask(ex.id, ex.semanticVersion, collId, t))

      sampleSolutionsSaved <- saveSeq[DbWebSampleSolution](dbSamples, s => db.run(sampleSolutionsTable += s))
    } yield htmlTasksSaved && jsTasksSaved && sampleSolutionsSaved
  }

  private def saveHtmlTask(exId: Int, exSemVer: SemanticVersion, collId: Int, htmlTask: HtmlTask): Future[Boolean] = {
    val (dbHtmlTask, dbHtmlAttributes) = dbModels.dbHtmlTaskFromHtmlTask(exId, exSemVer, collId, htmlTask)

    db.run(htmlTasksTable += dbHtmlTask) flatMap { _ =>
      saveSeq[DbHtmlAttribute](dbHtmlAttributes, a => db.run(attributesTable += a))
    }
  }

  private def saveJsTask(exId: Int, exSemVer: SemanticVersion, collId: Int, jsTask: JsTask): Future[Boolean] = {
    val (dbJsTask, dbJsConditions) = dbModels.dbJsTaskFromJsTask(exId, exSemVer, collId, jsTask)

    db.run(jsTasksTable += dbJsTask) flatMap { _ =>
      saveSeq[DbJsCondition](dbJsConditions, c => db.run(conditionsTable += c))
    }
  }

  // Implicit column types

  private implicit val actionTypeColumnType: BaseColumnType[JsActionType] =
    MappedColumnType.base[JsActionType, String](_.entryName, str => JsActionType.withNameInsensitiveOption(str) getOrElse JsActionType.CLICK)

  override protected implicit val partTypeColumnType: BaseColumnType[WebExPart] =
    MappedColumnType.base[WebExPart, String](_.entryName, WebExParts.withNameInsensitive)

  override protected implicit val solTypeColumnType: TypedType[WebSolution] = null // ScalaBaseType.stringType

  // Table definitions

  class WebCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "web_collections") {

    override def * : ProvenShape[WebCollection] = (id, title, author, text, state, shortName) <> (WebCollection.tupled, WebCollection.unapply)

  }

  class WebExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "web_exercises") {

    def htmlText: Rep[String] = column[String]("html_text")

    def jsText: Rep[String] = column[String]("js_text")


    override def * : ProvenShape[DbWebExercise] = (id, semanticVersion, collectionId, title, author, text, state, htmlText.?, jsText.?) <> (DbWebExercise.tupled, DbWebExercise.unapply)

  }

  abstract class WebTasksTable[T <: DbWebTask](tag: Tag, name: String) extends ExForeignKeyTable[T](tag, name) {

    def id: Rep[Int] = column[Int]("task_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def text: Rep[String] = column[String]("text")

    def xpathQuery: Rep[String] = column[String]("xpath_query")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))

  }

  class HtmlTasksTable(tag: Tag) extends WebTasksTable[DbHtmlTask](tag, "html_tasks") {

    def textContent: Rep[String] = column[String]("text_content")


    override def * : ProvenShape[DbHtmlTask] = (id, exerciseId, exSemVer, collectionId, text, xpathQuery, textContent.?) <> (DbHtmlTask.tupled, DbHtmlTask.unapply)

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

    def xpathQuery: Rep[String] = column[String]("xpath_query")

    def isPrecondition: Rep[Boolean] = column[Boolean]("is_precondition")

    def awaitedValue: Rep[String] = column[String]("awaited_value")


    def pk: PrimaryKey = primaryKey("pk", (id, taskId, exerciseId, exSemVer))

    def taskFk: ForeignKeyQuery[JsTasksTable, DbJsTask] = foreignKey("task_fk", (taskId, exerciseId, exSemVer), jsTasksTable)(t => (t.id, t.exerciseId, t.exSemVer))


    override def * : ProvenShape[DbJsCondition] = (id, taskId, exerciseId, exSemVer, collectionId, xpathQuery, isPrecondition, awaitedValue) <> (DbJsCondition.tupled, DbJsCondition.unapply)

  }

  class WebSampleSolutionsTable(tag: Tag) extends ASampleSolutionsTable(tag, "web_sample_solutions") {

    def htmlSample: Rep[String] = column[String]("html_sample")

    def jsSample: Rep[String] = column[String]("js_sample")


    def * : ProvenShape[DbWebSampleSolution] = (id, exerciseId, exSemVer, collectionId, htmlSample, jsSample) <> (DbWebSampleSolution.tupled, DbWebSampleSolution.unapply)

  }

  class WebSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "web_solutions") {

    def htmlSolution: Rep[String] = column[String]("html_solution")

    def jsSolution: Rep[String] = column[String]("js_solution")

    override def * : ProvenShape[DbWebUserSolution] = (id, exerciseId, exSemVer, collectionId, username, part,
      htmlSolution, jsSolution, points, maxPoints) <> (DbWebUserSolution.tupled, DbWebUserSolution.unapply)

  }

  //  class WebExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "web_exercise_reviews") {
  //
  //    override def * : ProvenShape[WebExerciseReview] = (username, exerciseId, exerciseSemVer,
  //      exercisePart, difficulty, maybeDuration.?) <> (WebExerciseReview.tupled, WebExerciseReview.unapply)
  //
  //  }

}
