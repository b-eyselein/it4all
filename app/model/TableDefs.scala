package model

import com.github.tminglei.slickpg.{ExPostgresProfile, PgEnumSupport, PgPlayJsonSupport}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.ExecutionContext

trait MyPostgresProfile extends ExPostgresProfile with PgEnumSupport with PgPlayJsonSupport {

  override val pgjson = "jsonb"

  trait MyAPI extends super.API with JsonImplicits

  override val api: MyAPI = new MyAPI {}

}

object MyPostgresProfile extends MyPostgresProfile {}

class TableDefs @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val ec: ExecutionContext)
    extends HasDatabaseConfigProvider[JdbcProfile]
    with UserRepository
    with CollectionRepository
    with ExerciseRepository
    with UserSolutionRepository
    with ProficiencyRepository
