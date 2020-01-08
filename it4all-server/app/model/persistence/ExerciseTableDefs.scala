package model.persistence

import javax.inject.Inject
import model._
import model.core.CoreConsts._
import model.learningPath.LearningPathTableDefs
import model.lesson.Lesson
import model.tools.collectionTools._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

class ExerciseTableDefs @Inject()(override val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends LearningPathTableDefs
    with ExerciseTableDefQueries {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Table Queries

  protected final val collectionsTQ  : TableQuery[ExerciseCollectionsTable] = TableQuery[ExerciseCollectionsTable]
  protected final val exercisesTQ    : TableQuery[ExercisesTable]           = TableQuery[ExercisesTable]
  protected final val reviewsTQ      : TableQuery[ExerciseReviewsTable]     = TableQuery[ExerciseReviewsTable]
  protected final val userSolutionsTQ: TableQuery[UserSolutionsTable]       = TableQuery[UserSolutionsTable]
  protected final val lessonsTQ      : TableQuery[LessonsTable]             = TableQuery[LessonsTable]

  // Implicit column types

  private def jsonColumnType[T: ClassTag](tFormat: Format[T], default: => T = ???): BaseColumnType[T] = MappedColumnType.base[T, String](
    t => Json.stringify(tFormat.writes(t)),
    jsonT => tFormat.reads(Json.parse(jsonT)).getOrElse(default)
  )

  private def jsonSeqColumnType[T: ClassTag](tFormat: Format[T]): BaseColumnType[Seq[T]] =
    jsonColumnType(Format(Reads.seq(tFormat), Writes.seq(tFormat)))

  private val stringSeqColumnType: BaseColumnType[Seq[String]] =
    jsonSeqColumnType(Format(Reads.StringReads, Writes.StringWrites))

  private val semanticVersionColumnType: BaseColumnType[SemanticVersion] = jsonColumnType(ToolJsonProtocol.semanticVersionFormat)
  private val jsonValueColumnType      : BaseColumnType[JsValue]         = jsonColumnType(Format[JsValue](x => JsSuccess(x), identity))

  protected val exTagsColumnType: BaseColumnType[Seq[ExTag]] = jsonSeqColumnType(ToolJsonProtocol.exTagFormat)
  protected val exPartColumnType: BaseColumnType[ExPart]     = MappedColumnType.base[ExPart, String](_.entryName, _ => ???)

  // Abstract table classes

  protected final class ExerciseCollectionsTable(tag: Tag) extends Table[ExerciseCollection](tag, "collections") {

    private implicit val ssct: BaseColumnType[Seq[String]] = stringSeqColumnType


    def id: Rep[Int] = column[Int](idName)

    def toolId: Rep[String] = column[String]("tool_id")

    def title: Rep[String] = column[String]("title")

    def authors: Rep[Seq[String]] = column[Seq[String]]("authors")

    def text: Rep[String] = column[String]("ex_text")

    def shortName: Rep[String] = column[String]("short_name")


    def pk = primaryKey("ex_coll_pk", (id, toolId))


    override def * : ProvenShape[ExerciseCollection] = (
      id, toolId, title, authors, text, shortName
    ) <> (ExerciseCollection.tupled, ExerciseCollection.unapply)

  }

  protected final class ExercisesTable(tag: Tag) extends Table[Exercise](tag, "exercises") {

    private implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType
    private implicit val jvct: BaseColumnType[JsValue]         = jsonValueColumnType
    private implicit val ssct: BaseColumnType[Seq[String]]     = stringSeqColumnType
    private implicit val etct: BaseColumnType[Seq[ExTag]]      = exTagsColumnType


    def id: Rep[Int] = column[Int](idName)

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def semanticVersion: Rep[SemanticVersion] = column[SemanticVersion]("semantic_version")


    def title: Rep[String] = column[String]("title")

    def authors: Rep[Seq[String]] = column[Seq[String]]("authors")

    def text: Rep[String] = column[String]("ex_text")

    def tags: Rep[Seq[ExTag]] = column[Seq[ExTag]]("tags")


    def content: Rep[JsValue] = column[JsValue]("content_json")


    def pk: PrimaryKey = primaryKey("pk", (id, collectionId, toolId, semanticVersion))

    def collectionFk: ForeignKeyQuery[ExerciseCollectionsTable, ExerciseCollection] = foreignKey(
      "scenario_fk", (collectionId, toolId), collectionsTQ
    )(c => (c.id, c.toolId))


    override def * : ProvenShape[Exercise] = (
      id, collectionId, toolId, semanticVersion, title, authors, text, tags, content
    ) <> (Exercise.tupled, Exercise.unapply)

  }

  protected abstract class ExForeignKeyTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

    private implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType


    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("exercise_semantic_version")


    def exerciseFk: ForeignKeyQuery[ExercisesTable, Exercise] = foreignKey(
      "exercise_fk", (exerciseId, collectionId, toolId, exSemVer), exercisesTQ
    )(ex => (ex.id, ex.collectionId, ex.toolId, ex.semanticVersion))

  }

  protected final class UserSolutionsTable(tag: Tag) extends Table[DbUserSolution](tag, "user_solutions") {

    private implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType
    private implicit val jvct: BaseColumnType[JsValue]         = jsonValueColumnType
    private implicit val epct: BaseColumnType[ExPart]          = exPartColumnType

    def id: Rep[Int] = column[Int]("id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("exercise_semantic_version")

    def username: Rep[String] = column[String]("username")

    def part: Rep[ExPart] = column[ExPart]("part")

    def solutionJson: Rep[JsValue] = column[JsValue]("solution_json")


    def pk = primaryKey("user_solutions_fk", (id, exerciseId, collectionId, toolId, exSemVer, part, username))


    def exerciseFk: ForeignKeyQuery[ExercisesTable, Exercise] = foreignKey(
      "exercise_fk", (exerciseId, collectionId, toolId, exSemVer), exercisesTQ
    )(ex => (ex.id, ex.collectionId, ex.toolId, ex.semanticVersion))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)


    override def * : ProvenShape[DbUserSolution] = (
      id, exerciseId, collectionId, toolId, exSemVer, part, username, solutionJson
    ) <> (DbUserSolution.tupled, DbUserSolution.unapply)

  }


  protected class ExerciseReviewsTable(tag: Tag) extends Table[DbExerciseReview](tag, "exercise_reviews") {

    private implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType
    private implicit val epct: BaseColumnType[ExPart]          = exPartColumnType
    private implicit val dct : BaseColumnType[Difficulty]      = jsonColumnType(Difficulties.jsonFormat)


    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("exercise_semantic_version")

    def username: Rep[String] = column[String]("username")

    def exercisePart: Rep[ExPart] = column[ExPart]("part")

    def difficulty: Rep[Difficulty] = column[Difficulty](difficultyName)

    def maybeDuration: Rep[Option[Int]] = column[Option[Int]]("maybe_duration")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, collectionId, exercisePart))


    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

    def exerciseFk: ForeignKeyQuery[ExercisesTable, Exercise] = foreignKey(
      "exercise_fk", (exerciseId, collectionId, toolId, exSemVer), exercisesTQ
    )(ex => (ex.id, ex.collectionId, ex.toolId, ex.semanticVersion))

    def * : ProvenShape[DbExerciseReview] = (
      exerciseId, collectionId, toolId, exSemVer, exercisePart, username, difficulty, maybeDuration
    ) <> (DbExerciseReview.tupled, DbExerciseReview.unapply)

  }

  protected class LessonsTable(tag: Tag) extends Table[DbLesson](tag, "lessons") {

    private implicit val jct: BaseColumnType[JsValue] = jsonValueColumnType


    def id: Rep[Int] = column[Int]("id")

    def toolId: Rep[String] = column("tool_id")

    def title: Rep[String] = column[String]("title")

    def contentJson: Rep[JsValue] = column[JsValue]("content_json")


    def pk = primaryKey("lesson_pk", (id, toolId))


    override def * : ProvenShape[DbLesson] = (id, toolId, title, contentJson) <> (DbLesson.tupled, DbLesson.unapply)

  }

}
