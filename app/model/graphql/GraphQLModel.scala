package model.graphql

import model.MongoClientQueries
import model.tools._
import play.api.libs.json._
import play.modules.reactivemongo.MongoController
import sangria.schema._

final case class GraphQLRequest(
  query: String,
  operationName: Option[String],
  variables: Option[JsObject]
)

final case class GraphQLContext()

trait GraphQLModel
    extends BasicGraphQLModels
    with CollectionGraphQLModel
    with ExerciseGraphQLModels
    with GraphQLMutations
    with MongoClientQueries {
  self: MongoController =>

  // Types

  private val lessonFieldsForToolType = fields[Unit, CollectionTool](
    Field("lessonCount", LongType, resolve = context => lessonCountForTool(context.value.id)),
    Field("lessons", ListType(LessonGraphQLModel.LessonType), resolve = context => lessonsForTool(context.value.id)),
    Field(
      "lesson",
      OptionType(LessonGraphQLModel.LessonType),
      arguments = lessonIdArgument :: Nil,
      resolve = context => getLesson(context.value.id, context.arg(lessonIdArgument))
    )
  )

  private val collectionFieldsForToolType = fields[Unit, CollectionTool](
    Field("collectionCount", LongType, resolve = context => getCollectionCount(context.value.id)),
    Field("collections", ListType(CollectionType), resolve = context => getExerciseCollections(context.value.id)),
    Field(
      "collection",
      OptionType(CollectionType),
      arguments = collIdArgument :: Nil,
      resolve = context => getExerciseCollection(context.value.id, context.arg(collIdArgument))
    )
  )

  protected val ToolType: ObjectType[Unit, CollectionTool] = ObjectType(
    "CollectionTool",
    fields[Unit, CollectionTool](
      Field("id", IDType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("state", toolStateType, resolve = _.value.toolState),
      // Special fields for exercises
      Field("exerciseCount", LongType, resolve = context => getExerciseCountForTool(context.value.id)),
      Field(
        "allExercises",
        ListType(exerciseType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.id) match {
            case None       => ???
            case Some(tool) => getExercisesForTool(tool)
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

  private val QueryType: ObjectType[Unit, Unit] = ObjectType(
    "Query",
    fields[Unit, Unit](
      Field("tools", ListType(ToolType), resolve = _ => ToolList.tools),
      Field(
        "tool",
        OptionType(ToolType),
        arguments = toolIdArgument :: Nil,
        resolve = ctx => ToolList.tools.find(_.id == ctx.arg(toolIdArgument))
      )
    )
  )

  val schema: Schema[Unit, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
