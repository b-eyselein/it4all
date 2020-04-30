package controllers

import java.util.UUID

import com.github.t3hnar.bcrypt._
import javax.inject.{Inject, Singleton}
import model._
import model.json.JsonProtocols
import model.lti.BasicLtiLaunchRequest
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiLoginController @Inject() (
  cc: ControllerComponents,
  override val reactiveMongoApi: ReactiveMongoApi,
  override protected val configuration: Configuration
)(implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with MongoController
    with ReactiveMongoComponents
    with MongoClientQueries
    with JwtHelpers {

  // Json Web Token session

  private val jwtHashesToClaim: MutableMap[UUID, (JwtSession, LoggedInUser)] = MutableMap.empty

  private def getOrCreateUser(username: String): Future[LoggedInUser] =
    getUser(database, username)
      .flatMap {
        case Some(u) => Future(u)
        case None =>
          val newUser = User(username)
          insertUser(database, newUser).map { _ => newUser }
      }
      .map { user => LoggedInUser(user.username) }

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
      case None => NotFound("")
      case Some((jwtSession, user)) =>
        val loggedInUserWithToken = LoggedInUserWithToken(user, jwtSession.serialize)

        Ok(writeJsonWebToken(loggedInUserWithToken))
    }
  }

  def apiAuthenticate: Action[UserCredentials] =
    Action.async(parse.json[UserCredentials](JsonProtocols.userCredentialsFormat)) { implicit request =>
      getUser(database, request.body.username).map {
        case None => BadRequest("Invalid username!")
        case Some(user) =>
          user.pwHash match {
            case None => BadRequest("No password found!")
            case Some(pwHash) =>
              if (request.body.password.isBcrypted(pwHash)) {
                val loggedInUser = LoggedInUser(user.username)

                Ok(
                  writeJsonWebToken(
                    LoggedInUserWithToken(loggedInUser, createJwtSession(loggedInUser).serialize)
                  )
                )
              } else {
                BadRequest("Password invalid!")
              }
          }
      }
    }

}
