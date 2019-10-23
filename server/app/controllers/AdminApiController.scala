package controllers

import javax.inject.{Inject, Singleton}
import model.{ExerciseCollection, JsonProtocol}
import model.core.Repository
import model.toolMains.{CollectionToolMain, ToolList}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, Writes}
import play.api.mvc._
import play.api.{Configuration, Logger}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class AdminApiController @Inject()(
  cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository, val configuration: Configuration
)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) with ApiControllerBasics
    with HasDatabaseConfigProvider[JdbcProfile]
    with play.api.i18n.I18nSupport {

  private val logger = Logger(classOf[ApiController])

  override protected type ToolMainType = CollectionToolMain


  override protected val adminRightsRequired: Boolean = true

  // Json Web Token session

  def readCollections(toolId: String): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>

    val successfulReadCollections: Seq[ExerciseCollection] = toolMain.readCollectionsFromYaml.flatMap(_.toOption)

    Future.successful(Ok(Json.toJson(successfulReadCollections)(Writes.seq(JsonProtocol.collectionFormat))))
  }

}
