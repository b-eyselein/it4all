package controllers.coll

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model.core._
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.web.{WebExParts, WebToolJsonProtocol, WebToolMain}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

@deprecated
@Singleton
class CollectionController @Inject()(
  cc: ControllerComponents,
  val dbConfigProvider: DatabaseConfigProvider,
  ws: WSClient,
  val repository: Repository,
  tables: ExerciseTableDefs
)(implicit ec: ExecutionContext)
  extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile]
    with Secured
    with play.api.i18n.I18nSupport {

  private val logger = Logger(classOf[CollectionController])

  override protected val adminRightsRequired: Boolean = false

  // Routes

  def webSolution(collId: Int, exId: Int, partStr: String, fileName: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      tables.futureCollById(WebToolMain.urlPart, collId) flatMap {
        case None             => ??? //Future.successful(onNoSuchCollection(user, webToolMain, collId))
        case Some(collection) =>

          tables.futureExerciseById(WebToolMain.urlPart, collection.id, exId).flatMap {
            case None           => ??? // Future.successful(onNoSuchExercise(user, webToolMain, collection, exId))
            case Some(exercise) =>

              WebToolMain.partTypeFromUrl(partStr) match {
                case None    => ??? // Future.successful(onNoSuchExercisePart(user, webToolMain, collection, exercise, partStr))
                case Some(_) =>

                  val contentType: String = fileName.split("\\.").last match {
                    case "html" => "text/html"
                    case "css"  => "text/css"
                    case "js"   => "text/javascript"
                    case _      => "text/plain"
                  }

                  ws.url(WebToolMain.getSolutionUrl(user, exercise, fileName)).get()
                    .map(wsRequest => Ok(wsRequest.body).as(contentType))
              }
          }
      }
  }

  def updateWebSolution(collId: Int, id: Int, part: String): EssentialAction = withUser { user =>
    implicit request =>
      request.body.asJson match {
        case Some(jsValue) =>
          WebToolJsonProtocol.solutionFormat.reads(jsValue) match {
            case JsSuccess(ideWorkSpace, _) =>

              WebToolMain.writeWebSolutionFiles(user.username, collId, id, WebToolMain.partTypeFromUrl(part).getOrElse(WebExParts.HtmlPart), ideWorkSpace) match {
                case Success(_)     => Ok("Solution saved")
                case Failure(error) =>
                  logger.error("Error while updating web solution", error)
                  BadRequest("Solution was not saved!")
              }

            case _ => ???
          }

        case _ => BadRequest("No or wrong content!")
      }
  }

}
