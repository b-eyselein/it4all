package model

import javax.inject.{Inject, Singleton}
import model.json.JsonProtocols
import model.persistence.ExerciseTableDefs
import model.tools._
import model.tools.sql.SqlGraphQLModels
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.{Environment, Mode}
import sangria.macros.derive._
import sangria.schema._

import scala.concurrent.ExecutionContext

final case class GraphQLRequest(
  query: String,
  operationName: Option[String],
  variables: Option[JsObject]
)

final case class GraphQLContext(
  tables: ExerciseTableDefs,
  user: Option[User]
)

@Singleton
class GraphQLModel @Inject() (ws: WSClient, environment: Environment)(implicit val ec: ExecutionContext)
    extends ToolGraphQLModels
    with GraphQLMutations {

  private val resourcesServerBaseUrl = {
    val port = if (environment.mode == Mode.Dev) 5000 else 5050

    s"http://localhost:$port/tools"
  }

  val graphQLRequestFormat: Format[GraphQLRequest] = Json.format

  // Types

  private val CollectionType: ObjectType[GraphQLContext, ExerciseCollection] = deriveObjectType(
    AddFields(
      Field(
        "exerciseCount",
        IntType,
        resolve = context => context.ctx.tables.futureExerciseCountInColl(context.value.toolId, context.value.id)
      ),
      Field(
        "exercises",
        ListType(exerciseInterfaceType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None       => ???
            case Some(tool) => tool.futureExercisesInCollection(context.ctx.tables, context.value.id)
          }
      ),
      Field(
        "exercise",
        OptionType(exerciseInterfaceType),
        arguments = exIdArgument :: Nil,
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None => ???
            case Some(tool) =>
              tool
                .futureExerciseById(context.ctx.tables, context.value.id, context.arg(exIdArgument))
                .map(_.asInstanceOf[Exercise[_, _]])
          }
      ),
      Field(
        "readExercises",
        ListType(StringType),
        resolve = context => {
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None => ???
            case Some(tool) =>
              ws.url(s"$resourcesServerBaseUrl/${context.value.toolId}/collections/${context.value.id}/exercises")
                .get()
                .map(request => tool.toolJsonProtocol.validateAndWriteReadExerciseMessage(request.json))
          }
        }
      )
    )
  )

  private val ToolType: ObjectType[GraphQLContext, CollectionTool] = ObjectType(
    "CollectionTol",
    fields[GraphQLContext, CollectionTool](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("state", toolStateType, resolve = _.value.toolState),
      // Fields for lessons
      Field("lessonCount", IntType, resolve = context => context.ctx.tables.futureLessonCount(context.value.id)),
      Field(
        "lessons",
        ListType(LessonGraphQLModel.LessonType),
        resolve = context => context.ctx.tables.futureAllLessons(context.value.id)
      ),
      Field(
        "lesson",
        OptionType(LessonGraphQLModel.LessonType),
        arguments = lessonIdArgument :: Nil,
        resolve = context => context.ctx.tables.futureLessonById(context.value.id, context.arg(lessonIdArgument))
      ),
      Field("readLessons", ListType(LessonGraphQLModel.LessonType), resolve = _ => List()),
      // Fields for collections
      Field(
        "collectionCount",
        IntType,
        resolve = context => context.ctx.tables.futureCollectionCount(context.value.id)
      ),
      Field(
        "collections",
        ListType(CollectionType),
        resolve = context => context.ctx.tables.futureAllCollections(context.value.id)
      ),
      Field(
        "collection",
        OptionType(CollectionType),
        arguments = collIdArgument :: Nil,
        resolve = context => context.ctx.tables.futureCollById(context.value.id, context.arg(collIdArgument))
      ),
      Field(
        "readCollections",
        ListType(StringType),
        resolve = context =>
          ws.url(s"$resourcesServerBaseUrl/${context.value.id}/collections")
            .get()
            .map { request =>
              JsonProtocols.readCollectionsMessageReads
                .reads(request.json)
                .map(_.collections)
                .getOrElse(Seq.empty)
                .map(JsonProtocols.collectionFormat.writes)
                .map(Json.stringify)
            }
      ),
      // Special fields for exercises
      Field(
        "exerciseCount",
        IntType,
        resolve = context => context.ctx.tables.futureExerciseCountForTool(context.value.id)
      ),
      Field(
        "allExercises",
        ListType(exerciseInterfaceType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.id) match {
            case None       => ???
            case Some(tool) => tool.futureAllExercises(context.ctx.tables)
          }
      )
    )
  )

  private val QueryType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
      Field("tools", ListType(ToolType), resolve = _ => ToolList.tools),
      Field(
        "tool",
        OptionType(ToolType),
        arguments = toolIdArgument :: Nil,
        resolve = ctx => ToolList.tools.find(_.id == ctx.arg(toolIdArgument))
      ),
      SqlGraphQLModels.dbContentQueryField
    )
  )

  val schema: Schema[GraphQLContext, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
