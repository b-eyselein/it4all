package controllers

import javax.inject.{Inject, Singleton}
import model.lesson.Lesson
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.ToolJsonProtocol
import model.tools.collectionTools.sql._
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
    with AbstractApiExerciseController {

  override protected val adminRightsRequired: Boolean = false

  private implicit val lessonFormat: Format[Lesson] = ToolJsonProtocol.lessonFormat

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

  // Lessons

  def apiLessonCount(toolType: String): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async {
    implicit request =>
      tables.futureLessonCount(request.toolMain.urlPart).map { lessonCount =>
        Ok(Json.toJson(lessonCount))
      }
  }

  def apiAllLessons(toolType: String): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async {
    implicit request =>
      tables.futureAllLessons(request.toolMain.urlPart).map { lessons =>
        Ok(Json.toJson(lessons))
      }
  }

  def apiLesson(toolType: String, lessonId: Int): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async {
    implicit request =>
      tables.futureLessonById(request.toolMain.urlPart, lessonId).map {
        case None         => NotFound("No such lesson found!")
        case Some(lesson) => Ok(Json.toJson(lesson))
      }
  }

  // Special routes for tools

  def sqlDbContent(collId: Int): Action[AnyContent] = JwtAuthenticatedAction.async { implicit request =>
    implicit val sqlQueryResultWrites: Writes[SqlQueryResult] = {
      implicit val sqlCellWrites: Writes[SqlCell] = Json.writes

      implicit val sqlRowWrites: Writes[SqlRow] = Json.writes

      Json.writes
    }

    tables.futureCollById(SqlToolMain.urlPart, collId).map {
      case None             => ???
      case Some(collection) => Ok(Json.toJson(SelectDAO.tableContents(collection.shortName)))
    }
  }

}
