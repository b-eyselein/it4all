package controllers

import java.util.UUID

import com.github.t3hnar.bcrypt._
import javax.inject.{Inject, Singleton}
import model._
import model.core.Repository
import model.lti.BasicLtiLaunchRequest
import model.toolMains.{CollectionToolMain, ToolList}
import pdi.jwt.JwtSession
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}
import slick.jdbc.JdbcProfile

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject()(
  cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository, val configuration: Configuration
)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) with ApiControllerBasics
    with HasDatabaseConfigProvider[JdbcProfile]
    with play.api.i18n.I18nSupport {

  private val logger = Logger(classOf[ApiController])

  override protected type ToolMainType = CollectionToolMain

  override protected val adminRightsRequired: Boolean = false

  // Json Web Token session

  private val jwtHashesToClaim: MutableMap[UUID, (JwtSession, User)] = MutableMap.empty

  private def getOrCreateUser(username: String): Future[User] = repository.userByName(username).flatMap {
    case Some(u) => Future(u)
    case None    =>
      val newUser   = LtiUser(username)
      val userSaved = repository.saveUser(newUser)
      userSaved.map(_ => newUser)
  }

  private def writeJsonWebToken(user: User, serializedSession: String): JsValue =
    JsonProtocol.userFormat.writes(user).as[JsObject].+("token" -> JsString(serializedSession))


  def ltiHoneypot: Action[AnyContent] = Action.async { request =>

    request.body.asFormUrlEncoded match {
      case None       => Future(BadRequest("TODO!"))
      case Some(data) =>

        val basicLtiRequest = BasicLtiLaunchRequest.fromRequest(data)

        val username = basicLtiRequest.ltiExt.username

        getOrCreateUser(username) map { user =>
          // FIXME: write jwt to session?

          val uuid = UUID.randomUUID()

          jwtHashesToClaim.put(uuid, (createJwtSession(user), user))

          val redirectUrl = s"/lti/${uuid.toString}" // routes.FrontendController.index().url

          Redirect(redirectUrl).withNewSession
        }

    }
  }

  def claimJsonWebToken(uuidStr: String): Action[AnyContent] = Action { implicit request =>
    jwtHashesToClaim.remove(UUID.fromString(uuidStr)) match {
      case None                     => NotFound("")
      case Some((jwtSession, user)) => Ok(writeJsonWebToken(user, jwtSession.serialize))
    }
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
                      val session = createJwtSession(user)

                      Ok(writeJsonWebToken(user, session.serialize))
                    } else {
                      BadRequest("Password invalid!")
                    }
                }
            }
        }
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
      case None             => NotFound(s"There is no such collection with id $collId for tool ${toolMain.toolname}")
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
