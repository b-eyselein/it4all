package controllers

import java.time.Clock

import com.github.t3hnar.bcrypt._
import javax.inject.{Inject, Singleton}
import model.core.Repository
import model.toolMains.{CollectionToolMain, ToolList}
import model.{ApiModelHelpers, RequestBodyHelpers, User}
import pdi.jwt.JwtSession
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsError, JsSuccess, Json, Writes}
import play.api.mvc._
import play.api.{Configuration, Logger}
import slick.jdbc.JdbcProfile

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject()(
  cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository, configuration: Configuration
)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl)
    with HasDatabaseConfigProvider[JdbcProfile]
    with play.api.i18n.I18nSupport {

  private val logger = Logger(classOf[ApiController])

  override protected type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)

  override protected val adminRightsRequired: Boolean = false


  private val clock: Clock = Clock.systemDefaultZone()

  private val apiJwtHashes: MutableMap[String, (JwtSession, User)] = MutableMap.empty

  private val bearerHeaderRegex: Regex = "Bearer (.*)".r


  private def createJwtHash(username: String, isAdmin: Boolean): JwtSession = {
    JwtSession()(configuration, clock) ++ (("user", username), ("isAdmin", isAdmin))
  }

  def apiAuthenticate: Action[AnyContent] = Action.async { implicit request =>

    request.body.asJson match {
      case None          => Future.successful(BadRequest("Body did not contain json!"))
      case Some(jsValue) =>

        RequestBodyHelpers.userCredentialsFormat.reads(jsValue) match {
          case JsError(errors)           =>
            errors.foreach(println)
            Future.successful(BadRequest("Body did contain invalid json!"))
          case JsSuccess(credentials, _) =>

            repository.userByName(credentials.username).flatMap {
              case None       => Future.successful(BadRequest("Invalid username!"))
              case Some(user) =>

                repository.pwHashForUser(credentials.username).map {
                  case None         => BadRequest("No password found!")
                  case Some(pwHash) =>
                    if (credentials.password.isBcrypted(pwHash.pwHash)) {
                      val session = createJwtHash(credentials.username, user.isAdmin)

                      val serializedSession = session.serialize

                      apiJwtHashes.put(serializedSession, (session, user))

                      Ok(Json.obj("id" -> credentials.username, "token" -> serializedSession))
                    } else {
                      BadRequest("Password invalid!")
                    }
                }
            }
        }
    }
  }

  private def apiWithToolMain(toolType: String)(f: (Request[AnyContent], User, CollectionToolMain) => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    request.headers.get("Authorization") match {
      case None =>
        // No authorization header present
        Future.successful(Unauthorized("You are not authorized to access this resource!"))

      case Some(bearerHeaderRegex(serializedJwtToken)) =>

        apiJwtHashes.get(serializedJwtToken) match {
          case None            => Future.successful(Unauthorized("You are not authorized to access this resource!"))
          case Some((_, user)) =>

            // FIXME: verify token?
            getToolMain(toolType) match {
              case None           => Future.successful(onNoSuchTool(toolType))
              case Some(toolMain) => f(request, user, toolMain)
            }
        }

      case Some(_) =>
        // Authorization header had wrong format...
        Future.successful(Unauthorized("You are not authorized to access this resource!"))
    }
  }

  def apiAllCollections(toolType: String): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    toolMain.futureAllCollections.map { collections =>
      val apiCollections = collections.map(ApiModelHelpers.apiExCollFromExColl(toolMain, _))

      Ok(Json.toJson(apiCollections)(Writes.seq(ApiModelHelpers.apiExCollJsonFormat)))
    }
  }

  def apiCollection(toolType: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    toolMain.futureCollById(collId).map {
      case None             => ???
      case Some(collection) => Ok(Json.toJson(collection)(toolMain.collectionJsonFormat))
    }
  }

  def apiExercises(toolType: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    // FIXME: send only id, title, ...
    toolMain.futureExercisesInColl(collId).map { exercises =>
      Ok(Json.toJson(exercises)(Writes.seq(toolMain.exerciseJsonFormat)))
    }
  }

  def apiExercise(toolType: String, collId: Int, exId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    toolMain.futureExerciseById(collId, exId).map {
      case None           => ???
      case Some(exercise) => Ok(Json.toJson(exercise)(toolMain.exerciseJsonFormat))
    }
  }

  def apiCorrect(toolType: String, collId: Int, exId: Int, partStr: String): Action[AnyContent] = apiWithToolMain(toolType) { (request, user, toolMain) =>
    toolMain.futureCollById(collId) flatMap {
      case None             => Future.successful(onNoSuchCollection(toolMain, collId))
      case Some(collection) =>

        toolMain.futureExerciseById(collection.id, exId) flatMap {
          case None           => Future.successful(onNoSuchExercise(toolMain, collection, exId))
          case Some(exercise) =>

            toolMain.partTypeFromUrl(partStr) match {
              case None         => Future.successful(onNoSuchExercisePart(toolMain, exercise, partStr))
              case Some(exPart) =>

                toolMain.correctAbstract(user, collection, exercise, exPart)(request, ec).map {
                  case Success(result) => Ok(toolMain.onLiveCorrectionResult(result))
                  case Failure(error)  =>
                    logger.error("There has been an internal correction error:", error)
                    BadRequest(toolMain.onLiveCorrectionError(error))
                }
            }
        }
    }
  }

}
