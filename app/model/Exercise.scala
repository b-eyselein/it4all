package model

import enumeratum.EnumEntry
import model.tools.Helper.UntypedExercise
import model.tools.{Tool, ToolList}
import play.api.libs.json.{Format, JsValue}

import scala.concurrent.{ExecutionContext, Future}

trait ExPart extends EnumEntry {

  val id: String

  val partName: String

  def isEntryPart: Boolean = true

}

trait ExerciseContent {

  protected type S

  val sampleSolutions: Seq[S]

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

private final case class DbExercise(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  title: String,
  text: String,
  difficulty: Level,
  jsonContent: JsValue
)

trait ExerciseRepository {
  self: play.api.db.slick.HasDatabaseConfig[slick.jdbc.JdbcProfile] =>

  import MyPostgresProfile.api._

  protected implicit val ec: ExecutionContext

  private val exercisesTQ = TableQuery[ExercisesTable]

  def futureExerciseCountForTool(toolId: String): Future[Int] = db.run(exercisesTQ.filter(_.toolId === toolId).length.result)

  def futureExerciseCountForCollection(toolId: String, collectionId: Int): Future[Int] = db.run(
    exercisesTQ.filter { ex => ex.toolId === toolId && ex.collectionId === collectionId }.length.result
  )

  private def readDbExercise(tool: Tool, dbExercise: DbExercise): Option[Exercise[tool.ExContentType]] = dbExercise match {
    case DbExercise(toolId, collectionId, exerciseId, title, text, difficulty, jsonContent) =>
      // TODO: filters out exercises if content can't be read, log error!
      for {
        content <- tool.jsonFormats.exerciseContentFormat.reads(jsonContent).asOpt
      } yield Exercise(exerciseId, collectionId, toolId, title, text, difficulty, content)
  }

  private def readDbExercises(tool: Tool, dbExercises: Seq[DbExercise]): Seq[Exercise[tool.ExContentType]] = dbExercises.flatMap(readDbExercise(tool, _))

  def futureExercisesForTool(tool: Tool): Future[Seq[Exercise[tool.ExContentType]]] = for {
    dbExercises <- db.run(
      exercisesTQ
        .filter(_.toolId === tool.id)
        .sortBy(_.exerciseId)
        .result
    )
  } yield readDbExercises(tool, dbExercises)

  def futureExercisesForCollection(toolId: String, collectionId: Int): Future[Seq[UntypedExercise]] = for {
    tool <- ToolList.tools.find(_.id == toolId) match {
      case Some(tool) => Future.successful(tool)
      case None       => Future.failed(new Exception(s"No such tool with id $toolId"))
    }

    dbExercises <- db.run(
      exercisesTQ
        .filter { ex => ex.toolId === tool.id && ex.collectionId === collectionId }
        .sortBy(_.exerciseId)
        .result
    )
  } yield readDbExercises(tool, dbExercises)

  def futureExerciseExists(toolId: String, collectionId: Int, exerciseId: Int): Future[Boolean] = for {
    lineCount <- db.run(
      exercisesTQ
        .filter { ex => ex.toolId === toolId && ex.collectionId === collectionId && ex.exerciseId === exerciseId }
        .length
        .result
    )
  } yield lineCount > 0

  def futureExerciseById(tool: Tool, collectionId: Int, exerciseId: Int): Future[Option[Exercise[tool.ExContentType]]] = for {
    dbExercise <- db.run(
      exercisesTQ
        .filter { ex => ex.toolId === tool.id && ex.collectionId === collectionId && ex.exerciseId === exerciseId }
        .result
        .headOption
    )

    maybeExercise = dbExercise.flatMap(readDbExercise(tool, _))
  } yield maybeExercise

  def futureInsertExercise[EC <: ExerciseContent](exercise: Exercise[EC], contentFormat: Format[EC]): Future[Boolean] = exercise match {
    case Exercise(exerciseId, collectionId, toolId, title, text, difficulty, content) =>
      for {
        lineCount <- db.run(exercisesTQ += DbExercise(toolId, collectionId, exerciseId, title, text, difficulty, contentFormat.writes(content)))
      } yield lineCount == 1
  }

  private class ExercisesTable(tag: Tag) extends Table[DbExercise](tag, "exercises") {

    def toolId = column[String]("tool_id")

    def collectionId = column[Int]("collection_id")

    def exerciseId = column[Int]("exercise_id")

    def title = column[String]("title")

    def text = column[String]("text")

    def difficulty = column[Level]("difficulty")

    def jsonContent = column[JsValue]("content_json")

    override def * = (toolId, collectionId, exerciseId, title, text, difficulty, jsonContent) <> (DbExercise.tupled, DbExercise.unapply)

  }

}
