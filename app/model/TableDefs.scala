package model

import com.github.tminglei.slickpg.utils.SimpleArrayUtils
import com.github.tminglei.slickpg.{ExPostgresProfile, PgEnumSupport, PgPlayJsonSupport}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Format, JsValue, Json}
import slick.ast.BaseTypedType
import slick.jdbc.{JdbcProfile, JdbcType}

import javax.inject.Inject
import scala.concurrent.ExecutionContext

trait MyPostgresProfile extends ExPostgresProfile with PgEnumSupport with PgPlayJsonSupport {

  override val pgjson = "jsonb"

  trait MyAPI extends super.API with JsonImplicits {

    implicit val levelMappedType: JdbcType[Level] = createEnumJdbcType("level", _.entryName, Level.withName, quoteName = false)

    implicit val topicMappedType: JdbcType[Topic] with BaseTypedType[Topic] = {
      implicit val tf: Format[Topic] = Json.format

      MappedJdbcType.base[Topic, JsValue](
        topic => Json.toJson(topic),
        jsValue => jsValue.as[Topic]
      )
    }

    implicit val pointsForExercisesListTypeMapper: AdvancedArrayJdbcType[LevelForExercise] = {
      implicit val levelForExerciseFormat: Format[LevelForExercise] = JsonProtocols.levelForExerciseFormat

      new AdvancedArrayJdbcType[LevelForExercise](
        pgjson,
        str => SimpleArrayUtils.fromString(Json.parse(_).as[LevelForExercise])(str).orNull,
        SimpleArrayUtils.mkString[LevelForExercise](lfe => Json.stringify(Json.toJson(lfe)))
      )
    }

  }

  override val api: MyAPI = new MyAPI {}

}

object MyPostgresProfile extends MyPostgresProfile {}

class TableDefs @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile]
    with TopicRepository
    with UserRepository
    with CollectionRepository
    with ExerciseRepository
    with ExerciseTopicsRepository
    with UserSolutionRepository
    with ProficiencyRepository
