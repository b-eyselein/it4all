package model.graphql

import model.mongo.MongoClientQueries
import model.tools.Helper.UntypedExercise
import model.tools._
import model.{ExerciseCollection, LoggedInUser}
import play.api.libs.json._
import sangria.macros.derive._
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

trait GraphQLModel extends BasicGraphQLModels with ExerciseGraphQLModels with LessonGraphQLModel with GraphQLMutations with MongoClientQueries {

  // Types

  private val collectionType = deriveObjectType[GraphQLContext, ExerciseCollection](
    AddFields[GraphQLContext, ExerciseCollection](
      Field("exerciseCount", LongType, resolve = context => futureExerciseCountForCollection(context.value.toolId, context.value.collectionId)),
      Field("exercises", ListType(exerciseType), resolve = context => futureExercisesForCollection(context.value.toolId, context.value.collectionId)),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None       => Future.successful(None)
            case Some(tool) => futureExerciseById(tool, context.value.collectionId, context.arg(exIdArgument)).asInstanceOf[Future[Option[UntypedExercise]]]
          }
      )
    )
  )

  private val toolStateType: EnumType[ToolState] = deriveEnumType()

  private val ToolType: ObjectType[GraphQLContext, Tool] = ObjectType(
    "CollectionTool",
    fields[GraphQLContext, Tool](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("toolState", toolStateType, resolve = _.value.toolState),
      // Lesson fields
      Field("lessonCount", LongType, resolve = context => futureLessonCountForTool(context.value.id)),
      Field("lessons", ListType(lessonType), resolve = context => futureLessonsForTool(context.value.id)),
      Field(
        "lesson",
        OptionType(lessonType),
        arguments = lessonIdArgument :: Nil,
        resolve = context => futureLessonById(context.value.id, context.arg(lessonIdArgument))
      ),
      // Collection fields
      Field("collectionCount", LongType, resolve = context => futureCollectionCountForTool(context.value.id)),
      Field("collections", ListType(collectionType), resolve = context => futureCollectionsForTool(context.value.id)),
      Field(
        "collection",
        OptionType(collectionType),
        arguments = collIdArgument :: Nil,
        resolve = context => futureCollectionById(context.value.id, context.arg(collIdArgument))
      ),
      // Special fields for exercises
      Field("exerciseCount", LongType, resolve = context => futureExerciseCountForTool(context.value.id)),
      Field(
        "allExercises",
        ListType(exerciseType),
        resolve = context => futureExercisesForTool(context.value).asInstanceOf[Future[Seq[UntypedExercise]]]
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
