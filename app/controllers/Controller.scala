package controllers

import java.util.UUID

import com.github.t3hnar.bcrypt._
import javax.inject.{Inject, Singleton}
import model.graphql.{GraphQLModel, GraphQLRequest}
import model.json.JsonProtocols
import model.lti.BasicLtiLaunchRequest
import model.{LoggedInUser, LoggedInUserWithToken, User, UserCredentials}
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.http.HttpErrorHandler
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.QueryParser

import scala.collection.mutable.{Map => MutableMap}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class Controller @Inject() (
  assets: Assets,
  cc: ControllerComponents,
  errorHandler: HttpErrorHandler,
  override protected val configuration: Configuration,
  override val reactiveMongoApi: ReactiveMongoApi
)(override protected implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with GraphQLModel
    with MongoController
    with ReactiveMongoComponents {

  def index: Action[AnyContent] = assets.at("index.html")

  def graphiql: Action[AnyContent] =
    Action { implicit request =>
      Ok(views.html.graphiql())
    }

  def assetOrDefault(resource: String): Action[AnyContent] =
    if (resource.startsWith(configuration.get[String]("apiPrefix"))) {
      Action.async(r => errorHandler.onClientError(r, NOT_FOUND, "Not found"))
    } else {
      if (resource.contains(".")) assets.at(resource) else index
    }

  private implicit val graphQLRequestFormat: Format[GraphQLRequest] = Json.format

  def graphql: Action[GraphQLRequest] =
    Action.async(parse.json[GraphQLRequest]) { implicit request =>
      QueryParser.parse(request.body.query) match {
        case Failure(error) => Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
        case Success(queryAst) =>
          Executor
            .execute(
              schema,
              queryAst,
              operationName = request.body.operationName,
              variables = request.body.variables.getOrElse(Json.obj())
            )
            .map(Ok(_))
            .recover {
              case error: QueryAnalysisError => BadRequest(error.resolveError)
              case error: ErrorWithResolver  => InternalServerError(error.resolveError)
            }
      }
    }

  // Json Web Token session

  private val jwtHashesToClaim: MutableMap[UUID, (JwtSession, LoggedInUser)] = MutableMap.empty

  private def getOrCreateUser(username: String): Future[LoggedInUser] =
    getUser(username)
      .flatMap {
        case Some(u) => Future(u)
        case None =>
          val newUser = User(username)
          insertUser(newUser).map { _ => newUser }
      }
      .map { user => LoggedInUser(user.username) }

  def ltiHoneypot: Action[AnyContent] =
    Action.async { request =>
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

  def claimJsonWebToken(uuidStr: String): Action[AnyContent] =
    Action { implicit request =>
      jwtHashesToClaim.remove(UUID.fromString(uuidStr)) match {
        case None => NotFound("")
        case Some((jwtSession, user)) =>
          val loggedInUserWithToken = LoggedInUserWithToken(user, jwtSession.serialize)

          Ok(writeJsonWebToken(loggedInUserWithToken))
      }
    }

  def apiAuthenticate: Action[UserCredentials] =
    Action.async(parse.json[UserCredentials](JsonProtocols.userCredentialsFormat)) { implicit request =>
      getUser(request.body.username).map {
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
