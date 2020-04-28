package controllers

import javax.inject.{Inject, Singleton}
import model.persistence.ExerciseTableDefs
import model.User
import model.graphql.{GraphQLContext, GraphQLModel, GraphQLRequest}
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.QueryParser

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject() (
  cc: ControllerComponents,
  tables: ExerciseTableDefs,
  graphQLModel: GraphQLModel,
  override protected val configuration: Configuration
)(implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with AbstractApiController {

  override protected val adminRightsRequired: Boolean = false

  private val graphQLRequestFormat: Format[GraphQLRequest] = Json.format

  private def executeGraphQLQuery(
    query: Document,
    user: Option[User],
    operationName: Option[String],
    variables: JsObject
  ): Future[Result] = {
    val userContext = GraphQLContext(tables, user)

    Executor
      .execute(graphQLModel.schema, query, userContext, operationName = operationName, variables = variables)
      .map(Ok(_))
      .recover {
        case error: QueryAnalysisError =>
          println(error)
          BadRequest(error.resolveError)
        case error: ErrorWithResolver =>
          println(error)
          InternalServerError(error.resolveError)
      }
  }

  def graphql: Action[GraphQLRequest] = Action.async(parse.json[GraphQLRequest](graphQLRequestFormat)) {
    implicit request =>
      QueryParser.parse(request.body.query) match {
        case Success(queryAst) =>
          executeGraphQLQuery(
            queryAst,
            userFromHeader(request),
            request.body.operationName,
            request.body.variables.getOrElse(Json.obj())
          )
        case Failure(error) => Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
      }
  }

}
