package model.graphql

import model.ExerciseCollection
import model.tools.Helper.UntypedExercise
import model.tools.ToolList
import sangria.macros.derive.{AddFields, deriveObjectType}
import sangria.schema.{Field, IntType, ListType, ObjectType, OptionType}

import scala.concurrent.Future

trait CollectionQuery extends ExerciseQuery {

  private val resolveExerciseCount: Resolver[ExerciseCollection, Int] = context =>
    context.ctx.tableDefs.futureExerciseCountForCollection(context.value.toolId, context.value.collectionId)

  private val resolveExercises: Resolver[ExerciseCollection, Seq[UntypedExercise]] = context =>
    context.ctx.tableDefs.futureExercisesForCollection(context.value.toolId, context.value.collectionId)

  private val resolveExercise: Resolver[ExerciseCollection, Option[UntypedExercise]] = context => {
    val toolId     = context.value.toolId
    val exerciseId = context.arg(exIdArgument)

    for {
      tool <- futureFromOption(ToolList.tools.find(_.id == toolId), MyUserFacingGraphQLError(s"No such tool with id $toolId"))

      exercise <- context.ctx.tableDefs
        .futureExerciseById(tool, context.value.collectionId, exerciseId)
        .asInstanceOf[Future[Option[UntypedExercise]]]
    } yield exercise
  }

  protected val collectionType: ObjectType[GraphQLContext, ExerciseCollection] = deriveObjectType(
    AddFields[GraphQLContext, ExerciseCollection](
      Field("exerciseCount", IntType, resolve = resolveExerciseCount),
      Field("exercises", ListType(exerciseType), resolve = resolveExercises),
      Field("exercise", OptionType(exerciseType), arguments = exIdArgument :: Nil, resolve = resolveExercise)
    )
  )

}
