package controllers

import javax.inject.{Inject, Singleton}
import model.graphql.{GraphQLModel, GraphQLRequest}
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.playJson._
import sangria.parser.QueryParser

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject() (
  cc: ControllerComponents,
  override protected val configuration: Configuration,
  override val reactiveMongoApi: ReactiveMongoApi
)(override protected implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with GraphQLModel
    with MongoController
    with ReactiveMongoComponents
    with JwtHelpers {

  private val logger = Logger(classOf[ApiController])

  private val graphQLRequestFormat: Format[GraphQLRequest] = Json.format

  def graphql: Action[GraphQLRequest] = Action.async(parse.json[GraphQLRequest](graphQLRequestFormat)) {
    implicit request =>
      val variables = request.body.variables.getOrElse(Json.obj())

      QueryParser.parse(request.body.query) match {
        case Failure(error) => Future.successful(BadRequest(Json.obj("error" -> error.getMessage)))
        case Success(queryAst) =>
          Executor
            .execute(
              schema,
              queryAst,
              operationName = request.body.operationName,
              variables = variables
            )
            .map(Ok(_))
            .recover {
              case error: QueryAnalysisError =>
                logger.error("Error while analysing query", error)
                BadRequest(error.resolveError)
              case error: ErrorWithResolver =>
                logger.error("Error while executing query:", error)
                InternalServerError(error.resolveError)
            }
      }
  }

}
