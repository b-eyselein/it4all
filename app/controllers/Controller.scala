package controllers

import model._
import model.graphql.{GraphQLContext, GraphQLModel}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.json._
import play.api.mvc._
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.QueryParser

import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

final case class BasicLtiLaunchRequest(lms: String, username: String)

final case class GraphQLRequest(
  query: String,
  operationName: Option[String],
  variables: Option[JsObject]
)

@Singleton
class Controller @Inject() (
  assets: Assets,
  cc: ControllerComponents,
  jwtAction: JwtAction,
  tableDefs: TableDefs
)(implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with GraphQLModel {

  private val logger = Logger(classOf[Controller])

  private val graphQlRequestJsonFormat: OFormat[GraphQLRequest] = Json.format

  def index: Action[AnyContent] = assets.at("index.html")

  def assetOrDefault(resource: String): Action[AnyContent] = if (resource.contains(".")) assets.at(resource) else index

  def graphiql: Action[AnyContent] = Action { _ => Ok(views.html.graphiql()) }

  def graphql: Action[GraphQLRequest] = jwtAction.async(parse.json(graphQlRequestJsonFormat)) { case JwtRequest(maybeUser, request) =>
    val GraphQLRequest(query, operationName, maybeVariables) = request.body

    val variables = maybeVariables.getOrElse(Json.obj())

    QueryParser.parse(query) match {
      case Failure(error) => Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
      case Success(queryAst) =>
        Executor
          .execute(schema, queryAst, userContext = GraphQLContext(maybeUser, tableDefs, ec), operationName = operationName, variables = variables)
          .map(Ok(_))
          .recover {
            case error: QueryAnalysisError => {
              logger.error("Error while analysing query: ", error)
              BadRequest(error.resolveError)
            }
            case error: ErrorWithResolver => {
              logger.error("Error while resolving query: ", error)
              InternalServerError(error.resolveError)
            }
          }
    }
  }

  // Lti

  private val basicLtiLaunchRequestForm: Form[BasicLtiLaunchRequest] = Form(
    mapping(
      "ext_lms"           -> text,
      "ext_user_username" -> text
    )(BasicLtiLaunchRequest.apply)(BasicLtiLaunchRequest.unapply)
  )

  def ltiLogin: Action[BasicLtiLaunchRequest] = Action.async(parse.form(basicLtiLaunchRequestForm)) { request =>
    for {
      maybeUser <- tableDefs.futureUserByUsername(request.body.username)

      user <- maybeUser match {
        case Some(u) => Future(u)
        case None    => tableDefs.futureInsertUser(request.body.username, None)
      }

      username = user.username

      uuid = UUID.randomUUID().toString

      _ = jwtHashesToClaim.put(uuid, createJwtSession(username))
    } yield Redirect(s"/lti/$uuid").withNewSession
  }

}
