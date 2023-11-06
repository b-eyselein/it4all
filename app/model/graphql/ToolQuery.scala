package model.graphql

import model.tools.Helper.UntypedExercise
import model.tools.Tool
import model.{ExerciseCollection, UserProficiency}
import sangria.schema._

import scala.concurrent.Future

trait ToolQuery extends CollectionQuery {

  private val resolveCollectionCount: Resolver[Tool, Int] = context => context.ctx.tableDefs.futureCollectionCountForTool(context.value.id)

  private val resolveCollections: Resolver[Tool, Seq[ExerciseCollection]] = context => context.ctx.tableDefs.futureCollectionsForTool(context.value.id)

  private val resolveCollection: Resolver[Tool, ExerciseCollection] = context => {
    val collectionId = context.arg(collIdArgument)

    for {
      maybeCollection <- context.ctx.tableDefs.futureCollectionById(context.value.id, collectionId)

      result <- maybeCollection match {
        case Some(collection) => Future.successful(collection)
        case None             => Future.failed(MyUserFacingGraphQLError(s"No such collection with id $collectionId"))
      }
    } yield result
  }

  private val resolveExerciseCount: Resolver[Tool, Int] = context => context.ctx.tableDefs.futureExerciseCountForTool(context.value.id)

  private val resolveAllExercises: Resolver[Tool, Seq[UntypedExercise]] = context =>
    context.ctx.tableDefs.futureExercisesForTool(context.value).asInstanceOf[Future[Seq[UntypedExercise]]]

  private val resolveUserProficiencies: Resolver[Tool, Seq[UserProficiency]] = context =>
    context.ctx.loggedInUser match {
      case None    => Future.successful(Seq.empty)
      case Some(_) => Future.successful(Seq.empty) // TODO: context.ctx.tableDefs.userProficienciesForTool(user.username, context.value.id)
    }

  protected val toolType: ObjectType[GraphQLContext, Tool] = ObjectType(
    "Tool",
    fields[GraphQLContext, Tool](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("isBeta", BooleanType, resolve = _.value.isBeta),
      // Collection fields
      Field("collectionCount", IntType, resolve = resolveCollectionCount),
      Field("collections", ListType(collectionType), resolve = resolveCollections),
      Field("collection", collectionType, arguments = collIdArgument :: Nil, resolve = resolveCollection),
      // Special fields for exercises
      Field("exerciseCount", IntType, resolve = resolveExerciseCount),
      Field("allExercises", ListType(exerciseType), resolve = resolveAllExercises),
      // Fields for users
      Field("proficiencies", ListType(userProficiencyType), resolve = resolveUserProficiencies)
    )
  )

}
