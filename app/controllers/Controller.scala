package controllers

import com.github.t3hnar.bcrypt._
import model._
import model.graphql.{GraphQLContext, GraphQLModel, GraphQLRequest}
import model.lti.BasicLtiLaunchRequest
import pdi.jwt.JwtSession
import play.api.http.HttpErrorHandler
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.QueryParser

import java.util.UUID
import javax.inject.{Inject, Singleton}
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

  private val logger = Logger(classOf[Controller])

  private val apiPrefix = configuration.get[String]("apiPrefix")

  def index: Action[AnyContent] = Action { implicit request =>
    Redirect(routes.Controller.langIndex("de"))
  }

  def langIndex(lang: String = "de"): Action[AnyContent] = assets.at(s"$lang/index.html")

  def graphiql: Action[AnyContent] = Action { implicit request => Ok(views.html.graphiql()) }

  def assetOrDefault(lang: String, resource: String): Action[AnyContent] = if (resource.startsWith(apiPrefix)) {
    Action.async(r => errorHandler.onClientError(r, NOT_FOUND, "Not found"))
  } else {
    if (resource.contains(".")) assets.at(s"$lang/$resource") else langIndex(lang)
  }

  private implicit val graphQLRequestFormat: Format[GraphQLRequest] = Json.format

  def graphql: Action[GraphQLRequest] = Action.async(parse.json[GraphQLRequest]) { implicit request =>
    userFromRequestHeader(request).flatMap { maybeUser =>
      QueryParser.parse(request.body.query) match {
        case Failure(error) => Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
        case Success(queryAst) =>
          Executor
            .execute(
              schema,
              queryAst,
              userContext = GraphQLContext(maybeUser),
              operationName = request.body.operationName,
              variables = request.body.variables.getOrElse(Json.obj())
            )
            .map(Ok(_))
            .recover {
              case error: QueryAnalysisError =>
                logger.error("There has been a query error", error)
                BadRequest(error.resolveError)
              case error: ErrorWithResolver =>
                logger.error("There has been a query resolve error", error)
                InternalServerError(error.resolveError)
            }
      }
    }
  }

  // Json Web Token session

  private val jwtHashesToClaim: MutableMap[UUID, (JwtSession, LoggedInUser)] = MutableMap.empty

  private def getOrCreateUser(username: String): Future[LoggedInUser] = futureUserByUsername(username)
    .flatMap {
      case Some(u) => Future(u)
      case None =>
        val newUser = User(username)
        futureInsertUser(newUser).map { _ => newUser }
    }
    .map { user => LoggedInUser(user.username) }

  def ltiHoneypot: Action[AnyContent] = Action.async { request =>
    request.body.asFormUrlEncoded match {
      case None => Future(BadRequest("TODO!"))
      case Some(data) =>
        val basicLtiRequest = BasicLtiLaunchRequest.fromRequest(data)

        getOrCreateUser(basicLtiRequest.ltiExt.username) map { user =>
          val uuid = UUID.randomUUID()

          jwtHashesToClaim.put(uuid, (createJwtSession(user.username), user))

          Redirect(s"/de/lti/${uuid.toString}").withNewSession
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

  private implicit val userCredentialsFormat: OFormat[UserCredentials] = JsonProtocols.userCredentialsFormat

  def apiAuthenticate: Action[UserCredentials] = Action.async(parse.json[UserCredentials]) { implicit request =>
    futureUserByUsername(request.body.username).map {
      case None => BadRequest("Invalid username!")
      case Some(User(username, pwHash, _)) =>
        pwHash match {
          case None => BadRequest("No password found!")
          case Some(pwHash) =>
            if (request.body.password.isBcryptedBounded(pwHash)) {
              val loggedInUser = LoggedInUser(username)

              Ok(writeJsonWebToken(LoggedInUserWithToken(loggedInUser, createJwtSession(username).serialize)))
            } else {
              BadRequest("Password invalid!")
            }
        }
    }
  }

}
