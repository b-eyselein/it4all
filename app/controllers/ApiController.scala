package controllers

import javax.inject.{Inject, Singleton}
import model.graphql.{GraphQLContext, GraphQLModel, GraphQLRequest}
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.QueryParser

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject() (
  cc: ControllerComponents,
  graphQLModel: GraphQLModel,
  override protected val configuration: Configuration,
  override val reactiveMongoApi: ReactiveMongoApi
)(implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with MongoController
    with ReactiveMongoComponents
    with JwtHelpers {

  private val graphQLRequestFormat: Format[GraphQLRequest] = Json.format

  private def executeGraphQLQuery(
    query: Document,
    operationName: Option[String],
    variables: JsObject
  ): Future[Result] = {
    val userContext = GraphQLContext(database)

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
      val variables = request.body.variables.getOrElse(Json.obj())

      QueryParser.parse(request.body.query) match {
        case Failure(error)    => Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
        case Success(queryAst) => executeGraphQLQuery(queryAst, request.body.operationName, variables)
      }
  }

}
