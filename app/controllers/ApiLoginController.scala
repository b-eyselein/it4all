package controllers

import java.util.UUID

import com.github.t3hnar.bcrypt._
import javax.inject.{Inject, Singleton}
import model._
import model.json.JsonProtocols
import model.lti.BasicLtiLaunchRequest
import model.persistence.ExerciseTableDefs
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiLoginController @Inject() (
  cc: ControllerComponents,
  tables: ExerciseTableDefs,
  override protected val configuration: Configuration
)(implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with AbstractApiController {

  override protected val adminRightsRequired: Boolean = true

  // Json Web Token session

  private val jwtHashesToClaim: MutableMap[UUID, (JwtSession, User)] = MutableMap.empty

  private def getOrCreateUser(username: String): Future[User] = tables.userByName(username).flatMap {
    case Some(u) => Future(u)
    case None =>
      val newUser   = User(username, None)
      val userSaved = tables.saveUser(newUser)
      userSaved.map(_ => newUser)
  }

  private def writeJsonWebToken(user: User, serializedSession: String): JsValue =
    JsonProtocols.userFormat.writes(user).as[JsObject].+("token" -> JsString(serializedSession))

  def ltiHoneypot: Action[AnyContent] = Action.async { request =>
    request.body.asFormUrlEncoded match {
      case None => Future(BadRequest("TODO!"))
      case Some(data) =>
        val basicLtiRequest = BasicLtiLaunchRequest.fromRequest(data)

        getOrCreateUser(basicLtiRequest.ltiExt.username) map { user =>
          val uuid = UUID.randomUUID()

          jwtHashesToClaim.put(uuid, (createJwtSession(user), user))

          val redirectUrl = s"/lti/${uuid.toString}"

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

  def apiAuthenticate: Action[UserCredentials] = {
    implicit val userCredentialsFormat: Format[UserCredentials] = Json.format

    Action.async(parse.json[UserCredentials]) { implicit request =>
      tables.userByName(request.body.username).map {
        case None => BadRequest("Invalid username!")
        case Some(user) =>
          user.pwHash match {
            case None => BadRequest("No password found!")
            case Some(pwHash) =>
              if (request.body.password.isBcrypted(pwHash)) {
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
