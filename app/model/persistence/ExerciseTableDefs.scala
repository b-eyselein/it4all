package model.persistence

import javax.inject.Inject
import model._
import model.learningPath.LearningPathTableDefs
import model.tools._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

class ExerciseTableDefs @Inject() (override val dbConfigProvider: DatabaseConfigProvider)(
  override implicit val executionContext: ExecutionContext
) extends LearningPathTableDefs
    with ProficiencyTableDefs
    with ExerciseTableDefQueries {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Table Queries

  protected final val topicsTQ: TableQuery[TopicsTable]                   = TableQuery[TopicsTable]
  protected final val collectionsTQ: TableQuery[ExerciseCollectionsTable] = TableQuery[ExerciseCollectionsTable]
  protected final val exercisesTQ: TableQuery[ExercisesTable]             = TableQuery[ExercisesTable]
  protected final val exerciseTopicsTQ: TableQuery[ExerciseTopicsTable]   = TableQuery[ExerciseTopicsTable]
  protected final val sampleSolutionsTQ: TableQuery[SampleSolutionsTable] = TableQuery[SampleSolutionsTable]
  protected final val userSolutionsTQ: TableQuery[UserSolutionsTable]     = TableQuery[UserSolutionsTable]
  protected final val lessonsTQ: TableQuery[LessonsTable]                 = TableQuery[LessonsTable]

  // Implicit column types

  private def jsonColumnType[T: ClassTag](tFormat: Format[T], default: => T = ???): BaseColumnType[T] =
    MappedColumnType.base[T, String](
      t => Json.stringify(tFormat.writes(t)),
      jsonT => tFormat.reads(Json.parse(jsonT)).getOrElse(default)
    )

  private def jsonSeqColumnType[T: ClassTag](tFormat: Format[T]): BaseColumnType[Seq[T]] =
    jsonColumnType(Format(Reads.seq(tFormat), Writes.seq(tFormat)))

  private val stringSeqColumnType: BaseColumnType[Seq[String]] =
    jsonSeqColumnType(Format(Reads.StringReads, Writes.StringWrites))

  private val jsonValueColumnType: BaseColumnType[JsValue] = jsonColumnType(
    Format[JsValue](x => JsSuccess(x), identity)
  )

  protected val exPartColumnType: BaseColumnType[ExPart] = MappedColumnType.base[ExPart, String](_.entryName, _ => ???)

  // Table classes

  protected class TopicsTable(tag: Tag) extends Table[Topic](tag, "topics") {

    def id: Rep[Int] = column[Int]("id")

    def toolId: Rep[String] = column[String]("tool_id")

    def abbreviation: Rep[String] = column[String]("abbreviation")

    def title: Rep[String] = column[String]("title")

    def pk = primaryKey("topics_pk", (id, toolId))

    override def * : ProvenShape[Topic] = (id, toolId, abbreviation, title) <> (Topic.tupled, Topic.unapply)

  }

  protected class ExerciseCollectionsTable(tag: Tag) extends Table[ExerciseCollection](tag, "collections") {

    private implicit val ssct: BaseColumnType[Seq[String]] = stringSeqColumnType

    def id: Rep[Int] = column[Int]("id")

    def toolId: Rep[String] = column[String]("tool_id")

    def title: Rep[String] = column[String]("title")

    def authors: Rep[Seq[String]] = column[Seq[String]]("authors")

    def text: Rep[String] = column[String]("ex_text")

    def shortName: Rep[String] = column[String]("short_name")

    def pk = primaryKey("ex_coll_pk", (id, toolId))

    override def * : ProvenShape[ExerciseCollection] =
      (
        id,
        toolId,
        title,
        authors,
        text,
        shortName
      ) <> (ExerciseCollection.tupled, ExerciseCollection.unapply)

  }

  protected final class ExercisesTable(tag: Tag) extends Table[DbExercise](tag, "exercises") {

    private implicit val jvct: BaseColumnType[JsValue]     = jsonValueColumnType
    private implicit val ssct: BaseColumnType[Seq[String]] = stringSeqColumnType

    def id: Rep[Int] = column[Int]("id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def title: Rep[String] = column[String]("title")

    def authors: Rep[Seq[String]] = column[Seq[String]]("authors")

    def text: Rep[String] = column[String]("ex_text")

    def difficulty: Rep[Int] = column[Int]("difficulty")

    def content: Rep[JsValue] = column[JsValue]("content")

    def pk: PrimaryKey = primaryKey("pk", (id, collectionId, toolId))

    def collectionFk: ForeignKeyQuery[ExerciseCollectionsTable, ExerciseCollection] =
      foreignKey("collection_fk", (collectionId, toolId), collectionsTQ)(c => (c.id, c.toolId))

    override def * : ProvenShape[DbExercise] =
      (id, collectionId, toolId, title, authors, text, difficulty, content) <> (DbExercise.tupled, DbExercise.unapply)

  }

  protected final class ExerciseTopicsTable(tag: Tag) extends Table[DbExerciseTopic](tag, "exercise_topics") {

    def topicId: Rep[Int] = column[Int]("topic_id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def pk: PrimaryKey = primaryKey("exercise_topics_fk", (topicId, exerciseId, collectionId, toolId))

    def exerciseFk: ForeignKeyQuery[ExercisesTable, DbExercise] =
      foreignKey("exercise_topics_exercise_fk", (exerciseId, collectionId, toolId), exercisesTQ)(
        ex => (ex.id, ex.collectionId, ex.toolId)
      )

    override def * : ProvenShape[DbExerciseTopic] =
      (topicId, exerciseId, collectionId, toolId) <> (DbExerciseTopic.tupled, DbExerciseTopic.unapply)

  }

  protected final class SampleSolutionsTable(tag: Tag) extends Table[DbSampleSolution](tag, "exercise_sample_solutions") {
    private implicit val jvct: BaseColumnType[JsValue] = jsonValueColumnType
    private implicit val epct: BaseColumnType[ExPart]  = exPartColumnType

    def id: Rep[Int] = column[Int]("id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def solutionJson: Rep[JsValue] = column[JsValue]("solution_json")

    def pk = primaryKey("user_solutions_fk", (id, exerciseId, collectionId, toolId))

    def exerciseFk: ForeignKeyQuery[ExercisesTable, DbExercise] =
      foreignKey("exercise_fk", (exerciseId, collectionId, toolId), exercisesTQ)(
        ex => (ex.id, ex.collectionId, ex.toolId)
      )

    override def * : ProvenShape[DbSampleSolution] =
      (
        id,
        exerciseId,
        collectionId,
        toolId,
        solutionJson
      ) <> (DbSampleSolution.tupled, DbSampleSolution.unapply)

  }

  protected final class UserSolutionsTable(tag: Tag) extends Table[DbUserSolution](tag, "user_solutions") {

    private implicit val jvct: BaseColumnType[JsValue] = jsonValueColumnType
    private implicit val epct: BaseColumnType[ExPart]  = exPartColumnType

    def id: Rep[Int] = column[Int]("id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def username: Rep[String] = column[String]("username")

    def part: Rep[ExPart] = column[ExPart]("part")

    def solutionJson: Rep[JsValue] = column[JsValue]("solution_json")

    def pk = primaryKey("user_solutions_fk", (id, exerciseId, collectionId, toolId, part, username))

    def exerciseFk: ForeignKeyQuery[ExercisesTable, DbExercise] =
      foreignKey("exercise_fk", (exerciseId, collectionId, toolId), exercisesTQ)(
        ex => (ex.id, ex.collectionId, ex.toolId)
      )

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

    override def * : ProvenShape[DbUserSolution] =
      (
        id,
        exerciseId,
        collectionId,
        toolId,
        part,
        username,
        solutionJson
      ) <> (DbUserSolution.tupled, DbUserSolution.unapply)

  }

  protected class LessonsTable(tag: Tag) extends Table[DbLesson](tag, "lessons") {

    private implicit val jct: BaseColumnType[JsValue] = jsonValueColumnType

    def id: Rep[Int] = column[Int]("id")

    def toolId: Rep[String] = column("tool_id")

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")

    def contentJson: Rep[JsValue] = column[JsValue]("content_json")

    def pk = primaryKey("lesson_pk", (id, toolId))

    override def * : ProvenShape[DbLesson] =
      (id, toolId, title, description, contentJson) <> (DbLesson.tupled, DbLesson.unapply)

  }

}
