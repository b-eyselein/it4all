package model.graphql

import model.ExerciseCollection
import model.tools.Helper.UntypedExercise
import model.tools.ToolList
import sangria.schema._

trait CollectionQuery extends ExerciseQuery {

  private val resolveExerciseCount: Resolver[ExerciseCollection, Int] = context =>
    context.ctx.tableDefs.futureExerciseCountForCollection(context.value.toolId, context.value.collectionId)

  private val resolveExercises: Resolver[ExerciseCollection, Seq[UntypedExercise]] = context =>
    context.ctx.tableDefs.futureExercisesForCollection(context.value.toolId, context.value.collectionId)

  private val resolveExercise: Resolver[ExerciseCollection, Option[UntypedExercise]] = context => {
    implicit val ec = context.ctx.executionContext
    val toolId      = context.value.toolId
    val exerciseId  = context.arg(GraphQLArguments.exIdArgument)

    for {
      tool     <- futureFromOption(ToolList.tools.find { _.id == toolId }, MyUserFacingGraphQLError(s"No such tool with id $toolId"))
      exercise <- context.ctx.tableDefs.futureExerciseById(tool, context.value.collectionId, exerciseId)
    } yield exercise.asInstanceOf[Option[UntypedExercise]]
  }

  protected val collectionType: ObjectType[GraphQLContext, ExerciseCollection] = ObjectType(
    "ExerciseCollection",
    fields[GraphQLContext, ExerciseCollection](
      Field("toolId", StringType, resolve = _.value.toolId),
      Field("collectionId", IntType, resolve = _.value.collectionId),
      Field("title", StringType, resolve = _.value.title),
      Field("exerciseCount", IntType, resolve = resolveExerciseCount),
      Field("exercises", ListType(exerciseType), resolve = resolveExercises),
      Field("exercise", OptionType(exerciseType), arguments = GraphQLArguments.exIdArgument :: Nil, resolve = resolveExercise)
    )
  )

}
