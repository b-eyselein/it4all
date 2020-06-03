package model.graphql

import model.tools._
import model.{LoggedInUser, MongoClientQueries}
import play.api.libs.json._
import sangria.schema._

import scala.concurrent.Future

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

  protected val userType: ObjectType[Unit, LoggedInUser] = ObjectType(
    "User",
    fields[Unit, LoggedInUser](
      Field(
        "proficiencies",
        ListType(userProficiencyType),
        arguments = toolIdArgument :: Nil,
        resolve = context =>
          ToolList.tools.find(_.id == context.arg(toolIdArgument)) match {
            case None       => Future.successful(Seq.empty)
            case Some(tool) => userProficienciesForTool(context.value.username, tool.id)
          }
      )
    )
  )

  protected val ToolType: ObjectType[Unit, CollectionTool] = ObjectType(
    "CollectionTool",
    fields[Unit, CollectionTool](
      Field("id", IDType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("state", toolStateType, resolve = _.value.toolState),
      // Lesson fields
      Field("lessonCount", LongType, resolve = context => lessonCountForTool(context.value.id)),
      Field("lessons", ListType(LessonGraphQLModel.LessonType), resolve = context => lessonsForTool(context.value.id)),
      Field(
        "lesson",
        OptionType(LessonGraphQLModel.LessonType),
        arguments = lessonIdArgument :: Nil,
        resolve = context => getLesson(context.value.id, context.arg(lessonIdArgument))
      ),
      // Collection fields
      Field("collectionCount", LongType, resolve = context => getCollectionCount(context.value.id)),
      Field(
        "collections",
        ListType(collectionType),
        resolve = context =>
          getExerciseCollections(context.value.id)
            .map(futureExerciseCollections => futureExerciseCollections.map(coll => (context.value, coll)))
      ),
      Field(
        "collection",
        OptionType(collectionType),
        arguments = collIdArgument :: Nil,
        resolve = context =>
          getExerciseCollection(context.value.id, context.arg(collIdArgument))
            .map(futureMaybeExerciseCollection => futureMaybeExerciseCollection.map(coll => (context.value, coll)))
      ),
      // Special fields for exercises
      Field("exerciseCount", LongType, resolve = context => getExerciseCountForTool(context.value.id)),
      Field(
        "allExercises",
        ListType(exerciseType),
        resolve = context => getExercisesForTool(context.value).map(untypedExercises)
      )
    )
  )

  private val QueryType: ObjectType[Unit, Unit] = ObjectType(
    "Query",
    fields[Unit, Unit](
      Field(
        "me",
        OptionType(userType),
        arguments = userJwtArgument :: Nil,
        resolve = context => deserializeJwt(context.arg(userJwtArgument))
      ),
      Field("tools", ListType(ToolType), resolve = _ => ToolList.tools),
      Field(
        "tool",
        OptionType(ToolType),
        arguments = toolIdArgument :: Nil,
        resolve = context => ToolList.tools.find(_.id == context.arg(toolIdArgument))
      )
    )
  )

  val schema: Schema[Unit, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
