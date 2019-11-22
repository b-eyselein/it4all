package model.persistence

import javax.inject.Inject
import model._
import model.core.CoreConsts._
import model.core.{LongText, LongTextJsonProtocol}
import model.learningPath.LearningPathTableDefs
import model.tools.collectionTools._
import model.tools.collectionTools.uml.{UmlClassDiagram, UmlToolJsonProtocol}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag

class ExerciseTableDefs @Inject()(override val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends LearningPathTableDefs with ExerciseTableDefQueries {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Table Queries

  protected final val collTable              : TableQuery[ExerciseCollectionsTable] = TableQuery[ExerciseCollectionsTable]
  protected final val exTable                : TableQuery[ExercisesTable]           = TableQuery[ExercisesTable]
  protected final val reviewsTable           : TableQuery[ExerciseReviewsTable]     = TableQuery[ExerciseReviewsTable]
  protected final val userSolutionsTableQuery: TableQuery[UserSolutionsTable]       = TableQuery[UserSolutionsTable]

  // Implicit column types

  protected def jsonColumnType[T: ClassTag](tFormat: Format[T], default: => T = ???): BaseColumnType[T] = {
    MappedColumnType.base[T, String](
      t => Json.stringify(tFormat.writes(t)),
      jsonT => tFormat.reads(Json.parse(jsonT)).getOrElse(default)
    )
  }

  protected def jsonSeqColumnType[T](tFormat: Format[T]): BaseColumnType[Seq[T]] = {
    val tSeqFormat: Format[Seq[T]] = Format(Reads.seq(tFormat), Writes.seq(tFormat))

    jsonColumnType(tSeqFormat, Seq.empty)
  }


  protected def stringSeqColumnType: BaseColumnType[Seq[String]] = {
    val stringReads: Reads[String] = jsValue => JsSuccess(jsValue.asInstanceOf[JsString].value)

    val stringWrites: Writes[String] = (x: String) => JsString(x)

    jsonSeqColumnType[String](Format(stringReads, stringWrites))
  }

  protected def stringMapColumnType: BaseColumnType[Map[String, String]] = {
    val stringMapFormat: Format[Map[String, String]] = Format(
      Reads.mapReads[String, String](x => JsSuccess(x)),
      MapWrites.mapWrites(x => JsString(x))
    )

    jsonColumnType(stringMapFormat)
  }


  //  protected implicit val partTypeColumnType: BaseColumnType[ExPart]

  protected val exerciseStateColumnType: BaseColumnType[ExerciseState] = jsonColumnType(ExerciseState.jsonFormat)

  protected val semanticVersionColumnType: BaseColumnType[SemanticVersion] = jsonColumnType(ToolJsonProtocol.semanticVersionFormat)

  protected val difficultyColumnType: BaseColumnType[Difficulty] = jsonColumnType(Difficulties.jsonFormat)

  protected val longTextColumnType: BaseColumnType[LongText] = jsonColumnType(LongTextJsonProtocol.format)

  protected val jsonValueColumnType: BaseColumnType[JsValue] = jsonColumnType(Format[JsValue](x => JsSuccess(x), identity))

  protected val umlClassDiagramColumnType: BaseColumnType[UmlClassDiagram] = jsonColumnType(UmlToolJsonProtocol.umlClassDiagramJsonFormat)

  // Abstract table classes

  protected final class ExerciseCollectionsTable(tag: Tag) extends Table[ExerciseCollection](tag, "collections") {

    private implicit val esct: BaseColumnType[ExerciseState] = exerciseStateColumnType


    def id: Rep[Int] = column[Int](idName)


    def toolId: Rep[String] = column[String]("tool_id")

    def title: Rep[String] = column[String]("title")

    def author: Rep[String] = column[String]("author")

    def text: Rep[String] = column[String]("ex_text")

    def state: Rep[ExerciseState] = column[ExerciseState]("state_json")

    def shortName: Rep[String] = column[String]("short_name")


    def pk = primaryKey("ex_coll_pk", (id, toolId))


    override def * : ProvenShape[ExerciseCollection] = (id, toolId, title, author, text, state, shortName) <> (ExerciseCollection.tupled, ExerciseCollection.unapply)

  }

  protected final class ExercisesTable(tag: Tag) extends Table[Exercise](tag, "exercises") {

    private implicit val esct: BaseColumnType[ExerciseState]   = exerciseStateColumnType
    private implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType
    private implicit val ltct: BaseColumnType[LongText]        = longTextColumnType
    private implicit val jvct: BaseColumnType[JsValue]         = jsonValueColumnType


    def id: Rep[Int] = column[Int](idName)

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def semanticVersion: Rep[SemanticVersion] = column[SemanticVersion]("semantic_version")


    def title: Rep[String] = column[String]("title")

    def author: Rep[String] = column[String]("author")

    def text: Rep[LongText] = column[LongText]("ex_text")

    def state: Rep[ExerciseState] = column[ExerciseState]("state_json")


    def content: Rep[JsValue] = column[JsValue]("content_json")


    def pk: PrimaryKey = primaryKey("pk", (id, collectionId, toolId, semanticVersion))

    def collectionFk: ForeignKeyQuery[ExerciseCollectionsTable, ExerciseCollection] = foreignKey("scenario_fk", (collectionId, toolId), collTable)(c => (c.id, c.toolId))


    override def * : ProvenShape[Exercise] = (id, collectionId, toolId, semanticVersion, title, author, text, state, content) <> (Exercise.tupled, Exercise.unapply)

  }

  protected abstract class ExForeignKeyTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

    private implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType


    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def toolId: Rep[String] = column[String]("tool_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")


    def exerciseFk: ForeignKeyQuery[ExercisesTable, Exercise] = foreignKey(
      "exercise_fk", (exerciseId, collectionId, toolId, exSemVer), exTable
    )(ex => (ex.id, ex.collectionId, ex.toolId, ex.semanticVersion))

  }

  protected final class UserSolutionsTable(tag: Tag) extends ExForeignKeyTable[DbUserSolution](tag, "user_solutions") {

    private implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType
    private implicit val jvct: BaseColumnType[JsValue]         = jsonValueColumnType
    private implicit val epct: BaseColumnType[ExPart]          = ???


    def id: Rep[Int] = column[Int]("id")

    def username: Rep[String] = column[String]("username")

    def part: Rep[ExPart] = column[ExPart]("part")

    def solutionJson: Rep[JsValue] = column[JsValue]("solution_json")


    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)


    override def * : ProvenShape[DbUserSolution] = (
      id, exerciseId, collectionId, toolId, exSemVer, part, username, solutionJson
    ) <> (DbUserSolution.tupled, DbUserSolution.unapply)

  }


  final class ExerciseReviewsTable(tag: Tag) extends ExForeignKeyTable[DbExerciseReview](tag, "exercise_reviews") {

    //    protected implicit val ptct: BaseColumnType[PartType] = partTypeColumnType

    private implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType
    private implicit val epct: BaseColumnType[ExPart]          = ???
    private implicit val dct : BaseColumnType[Difficulty]      = difficultyColumnType


    def username: Rep[String] = column[String]("username")

    def exercisePart: Rep[ExPart] = column[ExPart](partName)

    def difficulty: Rep[Difficulty] = column[Difficulty](difficultyName)

    def maybeDuration: Rep[Option[Int]] = column[Option[Int]]("maybe_duration")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, collectionId, exercisePart))


    def * : ProvenShape[DbExerciseReview] = (
      exerciseId, collectionId, toolId, exSemVer, exercisePart, username, difficulty, maybeDuration
    ) <> (DbExerciseReview.tupled, DbExerciseReview.unapply)

  }

}
