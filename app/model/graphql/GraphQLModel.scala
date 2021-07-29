package model.graphql

import model.LoggedInUser
import model.mongo.MongoClientQueries
import model.tools._
import play.api.libs.json._
import sangria.schema._

import scala.concurrent.Future

final case class GraphQLRequest(
  query: String,
  operationName: Option[String],
  variables: Option[JsObject]
)

final case class GraphQLContext(
  loggedInUser: Option[LoggedInUser]
)

trait GraphQLModel
    extends BasicGraphQLModels
    with CollectionGraphQLModel
    with ExerciseGraphQLModels
    with LessonGraphQLModel
    with GraphQLMutations
    with MongoClientQueries {

  // Types

  private val ToolType: ObjectType[GraphQLContext, Tool] = ObjectType(
    "CollectionTool",
    fields[GraphQLContext, Tool](
      Field("id", IDType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("state", toolStateType, resolve = _.value.toolState),
      // Lesson fields
      Field("lessonCount", LongType, resolve = context => futureLessonCountForTool(context.value.id)),
      Field(
        "lessons",
        ListType(lessonType),
        resolve = context => futureLessonsForTool(context.value.id)
      ),
      Field(
        "lesson",
        OptionType(lessonType),
        arguments = lessonIdArgument :: Nil,
        resolve = context => futureLessonById(context.value.id, context.arg(lessonIdArgument))
      ),
      // Collection fields
      Field("collectionCount", LongType, resolve = context => futureCollectionCountForTool(context.value.id)),
      Field(
        "collections",
        ListType(collectionType),
        resolve = context =>
          futureCollectionsForTool(context.value.id)
            .map((collections) => collections.map((collection) => (context.value, collection)))
      ),
      Field(
        "collection",
        OptionType(collectionType),
        arguments = collIdArgument :: Nil,
        resolve = context =>
          futureCollectionById(context.value.id, context.arg(collIdArgument))
            .map((collections) => collections.map((collection) => (context.value, collection)))
      ),
      // Special fields for exercises
      Field("exerciseCount", LongType, resolve = context => futureExerciseCountForTool(context.value.id)),
      Field(
        "allExercises",
        ListType(exerciseType),
        resolve = context => futureExercisesForTool(context.value)
      ),
      // Fields for users
      Field(
        // TODO: move to tool!
        "proficiencies",
        ListType(userProficiencyType),
        resolve = context =>
          context.ctx.loggedInUser match {
            case None                            => Future.successful(Seq.empty)
            case Some(LoggedInUser(username, _)) => userProficienciesForTool(username, context.value.id)
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
        resolve = context => ToolList.tools.find(_.id == context.arg(toolIdArgument))
      )
    )
  )

  val schema: Schema[GraphQLContext, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
