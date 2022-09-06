package model.graphql

import model.tools.Helper.UntypedExercise
import model.tools._
import model.{ExerciseCollection, TableDefs, User}
import play.api.libs.json._
import sangria.macros.derive._
import sangria.schema._

import scala.concurrent.Future
import scala.util.{Failure, Success}

final case class GraphQLRequest(
  query: String,
  operationName: Option[String],
  variables: Option[JsObject]
)

final case class GraphQLContext(
  loggedInUser: Option[User],
  tableDefs: TableDefs
)

trait GraphQLModel extends BasicGraphQLModels with ExerciseGraphQLModels with GraphQLMutations {

  // Types

  private val collectionType: ObjectType[GraphQLContext, ExerciseCollection] = deriveObjectType(
    AddFields[GraphQLContext, ExerciseCollection](
      Field(
        "exerciseCount",
        IntType,
        resolve = context => context.ctx.tableDefs.futureExerciseCountForCollection(context.value.toolId, context.value.collectionId)
      ),
      Field(
        "exercises",
        ListType(exerciseType),
        resolve = context => context.ctx.tableDefs.futureExercisesForCollection(context.value.toolId, context.value.collectionId)
      ),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context => {
          val toolId     = context.value.toolId
          val exerciseId = context.arg(exIdArgument)

          ToolList.tools.find(_.id == toolId) match {
            case None => Future.failed(new Exception(s"No such tool with id ${toolId}"))
            case Some(tool) =>
              context.ctx.tableDefs
                .futureExerciseById(tool, context.value.collectionId, exerciseId)
                .asInstanceOf[Future[Option[UntypedExercise]]]
          }
        }
      )
    )
  )

  private val toolType: ObjectType[GraphQLContext, Tool] = ObjectType(
    "Tool",
    fields[GraphQLContext, Tool](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("isBeta", BooleanType, resolve = _.value.isBeta),
      // Collection fields
      Field("collectionCount", IntType, resolve = context => context.ctx.tableDefs.futureCollectionCountForTool(context.value.id)),
      Field("collections", ListType(collectionType), resolve = context => context.ctx.tableDefs.futureCollectionsForTool(context.value.id)),
      Field(
        "collection",
        collectionType,
        arguments = collIdArgument :: Nil,
        resolve = context => {
          val collectionId = context.arg(collIdArgument)

          context.ctx.tableDefs
            .futureCollectionById(context.value.id, collectionId)
            .flatMap {
              case Some(collection) => Future.successful(collection)
              case None             => Future.failed(new Exception(s"No such collection with id $collectionId"))
            }
        }
      ),
      // Special fields for exercises
      Field("exerciseCount", IntType, resolve = context => context.ctx.tableDefs.futureExerciseCountForTool(context.value.id)),
      Field(
        "allExercises",
        ListType(exerciseType),
        resolve = context => context.ctx.tableDefs.futureExercisesForTool(context.value).asInstanceOf[Future[Seq[UntypedExercise]]]
      ),
      // Fields for users
      Field(
        "proficiencies",
        ListType(userProficiencyType),
        resolve = context =>
          context.ctx.loggedInUser match {
            case None       => Future.successful(Seq.empty)
            case Some(user) => Future.successful(Seq.empty) // TODO: context.ctx.mongoQueries.userProficienciesForTool(user.username, context.value.id)
          }
      )
    )
  )

  private val QueryType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
      Field("tools", ListType(toolType), resolve = _ => ToolList.tools),
      Field(
        "tool",
        toolType,
        arguments = toolIdArgument :: Nil,
        resolve = context => {
          val toolId = context.arg(toolIdArgument)

          ToolList.tools.find(_.id == toolId) match {
            case Some(tool) => Success(tool)
            case None       => Failure(new Exception(s"No such tool with id ${context.arg(toolIdArgument)}"))
          }
        }
      )
    )
  )

  val schema: Schema[GraphQLContext, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
