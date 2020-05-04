package model.graphql

import model.MongoClientQueries
import model.tools._
import play.modules.reactivemongo.MongoController
import sangria.macros.derive.{AddFields, deriveObjectType}
import sangria.schema.{Context, Field, IDType, ListType, LongType, ObjectType, OptionType}

import scala.concurrent.Future

trait CollectionGraphQLModel
    extends BasicGraphQLModels
    with ExerciseGraphQLModels
    with GraphQLArguments
    with MongoClientQueries {
  self: MongoController =>

  private def untypedExercises(exercises: Seq[Exercise[_, _ <: ExerciseContent[_]]]): Seq[UntypedExercise] = exercises

  private def untypedMaybeExercise(exercise: Option[Exercise[_, _ <: ExerciseContent[_]]]): Option[UntypedExercise] =
    exercise

  protected val CollectionType: ObjectType[GraphQLContext, ExerciseCollection] = deriveObjectType(
    AddFields(
      Field(
        "exerciseCount",
        LongType,
        resolve = context => getExerciseCountForCollection(context.value.toolId, context.value.collectionId)
      ),
      Field(
        "exercises",
        ListType(exerciseType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None       => ???
            case Some(tool) => getExercisesForCollection(tool, context.value.collectionId).map(untypedExercises)
          }
      ),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context => getExerciseUntyped(context)
      )
    )
  )

  private def getExerciseUntyped(
    context: Context[GraphQLContext, ExerciseCollection]
  ): Future[Option[UntypedExercise]] = ToolList.tools.find(_.id == context.value.toolId) match {
    case None => ???
    case Some(tool) =>
      getExercise(tool, context.value.collectionId, context.arg(exIdArgument), tool.toolJsonProtocol.exerciseFormat)
        .map(untypedMaybeExercise)
  }

}
