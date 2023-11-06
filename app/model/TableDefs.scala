package model

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.{JdbcProfile, JdbcType}

import scala.concurrent.ExecutionContext

class TableDefs @javax.inject.Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile]
    with TopicRepository
    with UserRepository
    with CollectionRepository
    with ExerciseRepository
    with ExerciseTopicsRepository
    with UserSolutionRepository
    with ProficiencyRepository {

  import profile.api._

  protected implicit def levelType: JdbcType[Level] = MappedColumnType.base[Level, String](_.entryName, Level.withNameInsensitive)

  protected implicit def jsValueType: JdbcType[JsValue] = MappedColumnType.base[JsValue, String](Json.stringify, Json.parse)

}
