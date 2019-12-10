package controllers

import java.util.UUID

import com.github.t3hnar.bcrypt._
import javax.inject.{Inject, Singleton}
import model._
import model.core.Repository
import model.lti.BasicLtiLaunchRequest
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json._
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiLoginController @Inject()(
  cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, repository: Repository, configuration: Configuration
)(implicit ec: ExecutionContext)
  extends AbstractApiController(cc, configuration) with HasDatabaseConfigProvider[JdbcProfile] {

  override protected val adminRightsRequired: Boolean = true

  def changeRole: EssentialAction = apiWithUser { (request, admin) =>

    val onFormError: Form[UpdateRoleForm] => Future[Result] = { _ =>
      Future(BadRequest("TODO!"))
    }

    val onFromValue: UpdateRoleForm => Future[Result] = { updateRoleForm =>
      repository.updateUserRole(updateRoleForm.username, updateRoleForm.newRole) map { roleChanged =>
        if (roleChanged) Ok(Json.obj("name" -> updateRoleForm.username, "stdRole" -> updateRoleForm.newRole.entryName))
        else BadRequest("TODO!")
      }
    }

    if (admin.stdRole != Role.RoleSuperAdmin) Future(Forbidden("You do not have sufficient privileges to change roles!"))
    else RequestBodyHelpers.updateRoleForm.bindFromRequest()(request).fold(onFormError, onFromValue)
  }


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

      repository.userByName(request.body.username).flatMap {
        case None       => Future.successful(BadRequest("Invalid username!"))
        case Some(user) =>

          repository.pwHashForUser(user.username).map {
            case None         => BadRequest("No password found!")
            case Some(pwHash) =>
              if (request.body.password.isBcrypted(pwHash.pwHash)) {
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
