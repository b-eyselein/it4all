package model.graphql

import model.LoggedInUser
import model.mongo.MongoClientQueries
import model.tools.Helper.UntypedExercise
import model.tools._
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
    with LessonGraphQLModel
    with GraphQLMutations
    with MongoClientQueries {

  // Types

  private val ToolType: ObjectType[Unit, (LoggedInUser, Tool)] = ObjectType(
    "CollectionTool",
    fields[Unit, (LoggedInUser, Tool)](
      Field("id", IDType, resolve = _.value._2.id),
      Field("name", StringType, resolve = _.value._2.name),
      Field("state", toolStateType, resolve = _.value._2.toolState),
      // Lesson fields
      Field("lessonCount", LongType, resolve = context => futureLessonCountForTool(context.value._2.id)),
      Field(
        "lessons",
        ListType(lessonType),
        resolve = context =>
          futureLessonsForTool(context.value._2.id)
            .map(lessons => lessons.map(lesson => (context.value._1, lesson)))
      ),
      Field(
        "lesson",
        OptionType(lessonType),
        arguments = lessonIdArgument :: Nil,
        resolve = context =>
          futureLessonById(context.value._2.id, context.arg(lessonIdArgument))
            .map(maybeLesson => maybeLesson.map(lesson => (context.value._1, lesson)))
      ),
      // Collection fields
      Field("collectionCount", LongType, resolve = context => futureCollectionCountForTool(context.value._2.id)),
      Field(
        "collections",
        ListType(collectionType),
        resolve = context =>
          futureCollectionsForTool(context.value._2.id)
            .map(futureExerciseCollections => futureExerciseCollections.map(coll => (context.value, coll)))
      ),
      Field(
        "collection",
        OptionType(collectionType),
        arguments = collIdArgument :: Nil,
        resolve = context =>
          futureCollectionById(context.value._2.id, context.arg(collIdArgument))
            .map(futureMaybeExerciseCollection => futureMaybeExerciseCollection.map(coll => (context.value, coll)))
      ),
      // Special fields for exercises
      Field("exerciseCount", LongType, resolve = context => futureExerciseCountForTool(context.value._2.id)),
      Field(
        "allExercises",
        ListType(exerciseType),
        resolve = context =>
          futureExercisesForTool(context.value._2)
            .map(exes => exes.map(ex => (context.value._1, context.value._2, ex.asInstanceOf[UntypedExercise])))
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
