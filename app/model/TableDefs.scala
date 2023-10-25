package model

import com.github.tminglei.slickpg.{ExPostgresProfile, PgEnumSupport, PgPlayJsonSupport}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.{JdbcProfile, JdbcType}

import scala.concurrent.ExecutionContext

trait MyPostgresProfile extends ExPostgresProfile with PgEnumSupport with PgPlayJsonSupport {

  override val pgjson = "jsonb"

  trait MyAPI extends super.API with JsonImplicits {

    implicit val levelMappedType: JdbcType[Level] = createEnumJdbcType("level", _.entryName, Level.withName, quoteName = false)

  }

  override val api: MyAPI = new MyAPI {}

}

object MyPostgresProfile extends MyPostgresProfile {}

class TableDefs @javax.inject.Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile]
    with TopicRepository
    with UserRepository
    with CollectionRepository
    with ExerciseRepository
    with ExerciseTopicsRepository
    with UserSolutionRepository
    with ProficiencyRepository
