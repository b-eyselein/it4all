package model.graphql

import model.tools.CollectionTool
import model.tools.Helper.UntypedExercise
import model.{Exercise, ExerciseCollection, ExerciseContent, MongoClientQueries}
import sangria.schema._

trait CollectionGraphQLModel
    extends BasicGraphQLModels
    with ExerciseGraphQLModels
    with GraphQLArguments
    with MongoClientQueries {

  protected def untypedExercises(exercises: Seq[Exercise[_, _ <: ExerciseContent[_]]]): Seq[UntypedExercise] = exercises

  private def untypedMaybeExercise(exercise: Option[Exercise[_, _ <: ExerciseContent[_]]]): Option[UntypedExercise] =
    exercise

  protected val collectionType: ObjectType[Unit, (CollectionTool, ExerciseCollection)] = ObjectType(
    "ExerciseCollection",
    fields[Unit, (CollectionTool, ExerciseCollection)](
      Field("collectionId", IntType, resolve = _.value._2.collectionId),
      Field("title", StringType, resolve = _.value._2.title),
      Field("authors", ListType(StringType), resolve = _.value._2.authors),
      Field("text", StringType, resolve = _.value._2.text),
      Field(
        "exerciseCount",
        LongType,
        resolve = context => getExerciseCountForCollection(context.value._1.id, context.value._2.collectionId)
      ),
      Field(
        "exercises",
        ListType(exerciseType),
        resolve = context =>
          getExercisesForCollection(context.value._1, context.value._2.collectionId)
            .map(untypedExercises)
      ),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context =>
          getExercise(
            context.value._1.id,
            context.value._2.collectionId,
            context.arg(exIdArgument),
            context.value._1.jsonFormats.exerciseFormat
          ).map(untypedMaybeExercise)
      )
    )
  )

}
