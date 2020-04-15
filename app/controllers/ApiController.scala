package controllers

import javax.inject.{Inject, Singleton}
import model.persistence.ExerciseTableDefs
import model.{GraphQLContext, GraphQLModel, GraphQLRequest, User}
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
  override protected val configuration: Configuration
)(implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with AbstractApiController {

  override protected val adminRightsRequired: Boolean = false

  private val graphQLSchema = GraphQLModel.schema

  private def executeGraphQLQuery(
    query: Document,
    user: Option[User],
    operationName: Option[String],
    variables: JsObject
  ): Future[Result] =
    Executor
      .execute(
        graphQLSchema,
        query,
        userContext = GraphQLContext(tables, ec, user),
        operationName = operationName,
        variables = variables
      )
      .map(Ok(_))
      .recover {
        case error: QueryAnalysisError =>
          println(error)
          BadRequest(error.resolveError)
        case error: ErrorWithResolver =>
          println(error)
          InternalServerError(error.resolveError)
      }

  def graphql: Action[GraphQLRequest] = Action.async(parse.json[GraphQLRequest](GraphQLModel.graphQLRequestFormat)) {
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
