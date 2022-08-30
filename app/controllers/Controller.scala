package controllers

import model._
import model.graphql.{GraphQLContext, GraphQLModel, GraphQLRequest}
import model.mongo.MongoClientQueries
import play.api.libs.json._
import play.api.mvc._
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.QueryParser

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class Controller @Inject() (
  assets: Assets,
  cc: ControllerComponents,
  jwtAction: JwtAction,
  mongoQueries: MongoClientQueries,
  tableDefs: TableDefs
)(override protected implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with GraphQLModel {

  private val graphQlRequestJsonFormat: OFormat[GraphQLRequest] = Json.format

  def index: Action[AnyContent] = assets.at("index.html")

  def assetOrDefault(resource: String): Action[AnyContent] = if (resource.contains(".")) assets.at(resource) else index

  def graphiql: Action[AnyContent] = Action { _ => Ok(views.html.graphiql()) }

  def graphql: Action[GraphQLRequest] = jwtAction.async(parse.json(graphQlRequestJsonFormat)) { case JwtRequest(maybeUser, request) =>
    request.body match {
      case GraphQLRequest(query, operationName, variables) =>
        QueryParser.parse(query) match {
          case Failure(error) => Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
          case Success(queryAst) =>
            Executor
              .execute(
                schema,
                queryAst,
                userContext = GraphQLContext(maybeUser, mongoQueries, tableDefs),
                operationName = operationName,
                variables = variables.getOrElse(Json.obj())
              )
              .map(Ok(_))
              .recover {
                case error: QueryAnalysisError => BadRequest(error.resolveError)
                case error: ErrorWithResolver  => InternalServerError(error.resolveError)
              }
        }
    }
  }

  // Json Web Token session

  private def getOrCreateUser(username: String): Future[String] = for {
    maybeUser <- tableDefs.futureUserByUsername(username)
    user <- maybeUser match {
      case Some(u) => Future(u)
      case None    => tableDefs.futureInsertUser(username, None)
    }
  } yield user.username

  def ltiLogin: Action[BasicLtiLaunchRequest] = Action.async(parse.form(BasicLtiLaunchRequest.form)) { request =>
    for {
      username <- getOrCreateUser(request.body.username)
    } yield {
      val uuid = UUID.randomUUID().toString

      jwtHashesToClaim.put(uuid, LoginResult(username, createJwtSession(username)))

      Redirect(s"/lti/$uuid").withNewSession
    }
  }

}
