package model.graphql

import model.tools._
import model.{LoggedInUser, MongoClientQueries}
import play.api.libs.json._
import sangria.schema._

final case class GraphQLRequest(
  query: String,
  operationName: Option[String],
  variables: Option[JsObject]
)

trait GraphQLModel
    extends BasicGraphQLModels
    with CollectionGraphQLModel
    with ExerciseGraphQLModels
    with GraphQLMutations
    with MongoClientQueries {

  // Types

  private val ToolType: ObjectType[Unit, (LoggedInUser, CollectionTool)] = ObjectType(
    "CollectionTool",
    fields[Unit, (LoggedInUser, CollectionTool)](
      Field("id", IDType, resolve = _.value._2.id),
      Field("name", StringType, resolve = _.value._2.name),
      Field("state", toolStateType, resolve = _.value._2.toolState),
      // Lesson fields
      Field("lessonCount", LongType, resolve = context => lessonCountForTool(context.value._2.id)),
      Field(
        "lessons",
        ListType(LessonGraphQLModel.LessonType),
        resolve = context => lessonsForTool(context.value._2.id)
      ),
      Field(
        "lesson",
        OptionType(LessonGraphQLModel.LessonType),
        arguments = lessonIdArgument :: Nil,
        resolve = context => getLesson(context.value._2.id, context.arg(lessonIdArgument))
      ),
      // Collection fields
      Field("collectionCount", LongType, resolve = context => getCollectionCount(context.value._2.id)),
      Field(
        "collections",
        ListType(collectionType),
        resolve = context =>
          getExerciseCollections(context.value._2.id)
            .map(futureExerciseCollections => futureExerciseCollections.map(coll => (context.value._2, coll)))
      ),
      Field(
        "collection",
        OptionType(collectionType),
        arguments = collIdArgument :: Nil,
        resolve = context =>
          getExerciseCollection(context.value._2.id, context.arg(collIdArgument))
            .map(futureMaybeExerciseCollection => futureMaybeExerciseCollection.map(coll => (context.value._2, coll)))
      ),
      // Special fields for exercises
      Field("exerciseCount", LongType, resolve = context => getExerciseCountForTool(context.value._2.id)),
      Field(
        "allExercises",
        ListType(exerciseType),
        resolve = context => getExercisesForTool(context.value._2).map(untypedExercises)
      ),
      // Fields for users
      Field(
        // TODO: move to tool!
        "proficiencies",
        ListType(userProficiencyType),
        resolve = context => userProficienciesForTool(context.value._1.username, context.value._2.id)
      )
    )
  )

  protected val loggedInUserType: ObjectType[Unit, LoggedInUser] = ObjectType(
    "User",
    fields[Unit, LoggedInUser](
      Field("tools", ListType(ToolType), resolve = context => ToolList.tools.map(tool => (context.value, tool))),
      Field(
        "tool",
        OptionType(ToolType),
        arguments = toolIdArgument :: Nil,
        resolve = context =>
          ToolList.tools
            .find(_.id == context.arg(toolIdArgument))
            .map(tool => (context.value, tool))
      )
    )
  )

  private val QueryType: ObjectType[Unit, Unit] = ObjectType(
    "Query",
    fields[Unit, Unit](
      Field(
        "me",
        OptionType(loggedInUserType),
        arguments = userJwtArgument :: Nil,
        resolve = context => deserializeJwt(context.arg(userJwtArgument))
      )
    )
  )

  val schema: Schema[Unit, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
