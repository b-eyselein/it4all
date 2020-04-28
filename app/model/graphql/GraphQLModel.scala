package model.graphql

import javax.inject.{Inject, Singleton}
import model.User
import model.json.JsonProtocols
import model.persistence.ExerciseTableDefs
import model.tools._
import play.api.Environment
import play.api.libs.json._
import play.api.libs.ws.WSClient
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
class GraphQLModel @Inject() (
  override protected val ws: WSClient,
  override protected val environment: Environment
)(implicit val ec: ExecutionContext)
    extends ToolGraphQLModels
    with CollectionGraphQLModel
    with ExerciseGraphQLModels
    with GraphQLMutations {

  // Types

  protected val ToolType: ObjectType[GraphQLContext, CollectionTool] = ObjectType(
    "CollectionTool",
    fields[GraphQLContext, CollectionTool](
      Field("id", IDType, resolve = _.value.id),
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
                .fold(
                  _ => Seq.empty,
                  readCollectionsMessage => readCollectionsMessage.collections
                )
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
        ListType(exerciseType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.id) match {
            case None       => ???
            case Some(tool) => tool.futureAllExercises(context.ctx.tables)
          }
      ),
      Field(
        "part",
        OptionType(exPartType),
        arguments = partIdArgument :: Nil,
        resolve = context => ??? // context.value.parts
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
      )
    )
  )

  val schema: Schema[GraphQLContext, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
