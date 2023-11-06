package model

import enumeratum.EnumEntry
import model.tools.Helper.UntypedExercise
import model.tools.{Tool, ToolList}
import play.api.libs.json.{Format, JsValue}

import scala.annotation.unused
import scala.concurrent.Future

trait ExPart extends EnumEntry {

  val id: String

  val partName: String

  def isEntryPart: Boolean = true

}

trait ExerciseContent {

  protected type S

  val sampleSolutions: Seq[S]

}

trait ExerciseContentWithParts extends ExerciseContent {

  def parts: Seq[ExPart]

}

trait FileExerciseContent extends ExerciseContent {

  override protected type S = FilesSolution

}

final case class Exercise[C <: ExerciseContent](
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  text: String,
  difficulty: Level,
  content: C
)

protected final case class DbExercise(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  title: String,
  text: String,
  difficulty: Level,
  jsonContent: JsValue
)

trait ExerciseRepository {
  self: TableDefs =>

  import profile.api._

  // Helper functions

  private def readDbExercise(tool: Tool, dbExercise: DbExercise): Option[Exercise[tool.ExContType]] = dbExercise match {
    case DbExercise(toolId, collectionId, exerciseId, title, text, difficulty, jsonContent) =>
      // TODO: filters out exercises if content can't be read, log error!
      for {
        content <- tool.jsonFormats.exerciseContentFormat.reads(jsonContent).asOpt
      } yield Exercise(exerciseId, collectionId, toolId, title, text, difficulty, content)
  }

  private def readDbExercises(tool: Tool, dbExercises: Seq[DbExercise]): Seq[Exercise[tool.ExContType]] = dbExercises.flatMap(readDbExercise(tool, _))

  // Queries

  protected object exercisesTQ extends TableQuery[ExercisesTable](new ExercisesTable(_)) {

    def forTool(toolId: String): Query[ExercisesTable, DbExercise, Seq] = this.filter { _.toolId === toolId }

    def forCollection(toolId: String, collectionId: Int): Query[ExercisesTable, DbExercise, Seq] = this.filter { ex =>
      ex.toolId === toolId && ex.collectionId === collectionId
    }

    def byId(toolId: String, collectionId: Int, exerciseId: Int): Query[ExercisesTable, DbExercise, Seq] = this.filter { ex =>
      ex.toolId === toolId && ex.collectionId === collectionId && ex.exerciseId === exerciseId
    }

  }

  def futureExerciseCountForTool(toolId: String): Future[Int] = db.run(exercisesTQ.forTool(toolId).length.result)

  def futureExerciseCountForCollection(toolId: String, collectionId: Int): Future[Int] = db.run(exercisesTQ.forCollection(toolId, collectionId).length.result)

  def futureExercisesForTool(tool: Tool): Future[Seq[Exercise[tool.ExContType]]] = for {
    dbExercises <- db.run(exercisesTQ.forTool(tool.id).sortBy { _.exerciseId }.result)
  } yield readDbExercises(tool, dbExercises)

  def futureExercisesForCollection(toolId: String, collectionId: Int): Future[Seq[UntypedExercise]] = for {
    tool <- ToolList.tools.find(_.id == toolId) match {
      case Some(tool) => Future.successful(tool)
      case None       => Future.failed(new Exception(s"No such tool with id $toolId"))
    }

    dbExercises <- db.run(exercisesTQ.forCollection(tool.id, collectionId).sortBy { _.exerciseId }.result)
  } yield readDbExercises(tool, dbExercises)

  def futureExerciseExists(toolId: String, collectionId: Int, exerciseId: Int): Future[Boolean] = for {
    lineCount <- db.run(exercisesTQ.byId(toolId, collectionId, exerciseId).length.result)
  } yield lineCount > 0

  def futureExerciseById(tool: Tool, collectionId: Int, exerciseId: Int): Future[Option[Exercise[tool.ExContType]]] = for {
    dbExercise <- db.run(exercisesTQ.byId(tool.id, collectionId, exerciseId).result.headOption)

    maybeExercise = dbExercise.flatMap(readDbExercise(tool, _))
  } yield maybeExercise

  def futureInsertExercise[EC <: ExerciseContent](exercise: Exercise[EC], contentFormat: Format[EC]): Future[Boolean] = exercise match {
    case Exercise(exerciseId, collectionId, toolId, title, text, difficulty, content) =>
      for {
        lineCount <- db.run(exercisesTQ += DbExercise(toolId, collectionId, exerciseId, title, text, difficulty, contentFormat.writes(content)))
      } yield lineCount == 1
  }

  protected class ExercisesTable(tag: Tag) extends Table[DbExercise](tag, "exercises") {

    def toolId = column[String]("tool_id")

    def collectionId = column[Int]("collection_id")

    def exerciseId = column[Int]("exercise_id")

    private def title = column[String]("title", O.Unique)

    private def text = column[String]("text")

    private def difficulty = column[Level]("difficulty")

    private def jsonContent = column[JsValue]("content_json")

    def pk = primaryKey("exercises_pk", (toolId, collectionId, exerciseId))

    // noinspection ScalaWeakerAccess
    @unused("used by slick")
    def collectionsForeignKey = foreignKey("exercises_collections_fk", (toolId, collectionId), collectionsTQ)(
      c => (c.toolId, c.collectionId),
      onUpdate = ForeignKeyAction.Cascade,
      onDelete = ForeignKeyAction.Cascade
    )

    override def * = (toolId, collectionId, exerciseId, title, text, difficulty, jsonContent) <> (DbExercise.tupled, DbExercise.unapply)

  }

}
