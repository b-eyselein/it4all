package model.graphql

import javax.inject.{Inject, Singleton}
import model.MongoClientQueries
import model.tools._
import play.api.Configuration
import play.api.libs.json._
import reactivemongo.api.DefaultDB
import sangria.schema._

import scala.concurrent.{ExecutionContext, Future}

final case class GraphQLRequest(
  query: String,
  operationName: Option[String],
  variables: Option[JsObject]
)

final case class GraphQLContext(
  mongoDB: Future[DefaultDB]
)

@Singleton
class GraphQLModel @Inject() (
  override val configuration: Configuration
)(implicit val ec: ExecutionContext)
    extends BasicGraphQLModels
    with CollectionGraphQLModel
    with ExerciseGraphQLModels
    with GraphQLMutations
    with MongoClientQueries {

  // Types

  private val lessonFieldsForToolType = fields[GraphQLContext, CollectionTool](
    Field(
      "lessonCount",
      LongType,
      resolve = context => lessonCountForTool(context.ctx.mongoDB, context.value.id)
    ),
    Field(
      "lessons",
      ListType(LessonGraphQLModel.LessonType),
      resolve = context => lessonsForTool(context.ctx.mongoDB, context.value.id)
    ),
    Field(
      "lesson",
      OptionType(LessonGraphQLModel.LessonType),
      arguments = lessonIdArgument :: Nil,
      resolve = context => getLesson(context.ctx.mongoDB, context.value.id, context.arg(lessonIdArgument))
    )
  )

  private val collectionFieldsForToolType = fields[GraphQLContext, CollectionTool](
    Field(
      "collectionCount",
      LongType,
      resolve = context => getCollectionCount(context.ctx.mongoDB, context.value.id)
    ),
    Field(
      "collections",
      ListType(CollectionType),
      resolve = context => getExerciseCollections(context.ctx.mongoDB, context.value.id)
    ),
    Field(
      "collection",
      OptionType(CollectionType),
      arguments = collIdArgument :: Nil,
      resolve = context => getExerciseCollection(context.ctx.mongoDB, context.value.id, context.arg(collIdArgument))
    )
  )

  protected val ToolType: ObjectType[GraphQLContext, CollectionTool] = ObjectType(
    "CollectionTool",
    fields[GraphQLContext, CollectionTool](
      Field("id", IDType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("state", toolStateType, resolve = _.value.toolState),
      // Special fields for exercises
      Field(
        "exerciseCount",
        LongType,
        resolve = context => getExerciseCountForTool(context.ctx.mongoDB, context.value.id)
      ),
      Field(
        "allExercises",
        ListType(exerciseType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.id) match {
            case None       => ???
            case Some(tool) => getExercisesForTool(context.ctx.mongoDB, tool)
          }
      ),
      Field(
        "part",
        OptionType(exPartType),
        arguments = partIdArgument :: Nil,
        resolve = context => ??? // context.value.parts
      )
    ) ++ lessonFieldsForToolType ++ collectionFieldsForToolType
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
