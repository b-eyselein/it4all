package model.graphql

import model._
import model.mongo.MongoClientQueries
import model.tools.CollectionTool
import model.tools.Helper.UntypedExercise
import sangria.schema._

trait CollectionGraphQLModel
    extends BasicGraphQLModels
    with ExerciseGraphQLModels
    with GraphQLArguments
    with MongoClientQueries {

  protected def untypedExercises(exercises: Seq[Exercise[_ <: ExerciseContent]]): Seq[UntypedExercise] = exercises

  private def untypedMaybeExercise(exercise: Option[Exercise[_ <: ExerciseContent]]): Option[UntypedExercise] = exercise

  protected val collectionType: ObjectType[Unit, ((LoggedInUser, CollectionTool), ExerciseCollection)] = ObjectType(
    "ExerciseCollection",
    fields[Unit, ((LoggedInUser, CollectionTool), ExerciseCollection)](
      Field("collectionId", IntType, resolve = _.value._2.collectionId),
      Field("title", StringType, resolve = _.value._2.title),
      Field("authors", ListType(StringType), resolve = _.value._2.authors),
      Field(
        "exerciseCount",
        LongType,
        resolve = context => futureExerciseCountForCollection(context.value._1._2.id, context.value._2.collectionId)
      ),
      Field(
        "exercises",
        ListType(exerciseType),
        resolve = context => {
          val tool = context.value._1._2

          futureExercisesForCollection(context.value._1._2, context.value._2.collectionId)
            .map(exes => exes.map(ex => (context.value._1._1, ex.asInstanceOf[UntypedExercise])))
          //            .map(exes => untypedExercises(exes))
        }
      ),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context => {
          val tool         = context.value._1._2
          val collectionId = context.value._2.collectionId

          futureExerciseById(tool.id, collectionId, context.arg(exIdArgument), tool.jsonFormats.exerciseFormat)
            .map(maybeEx => maybeEx.map(ex => (context.value._1._1, ex.asInstanceOf[UntypedExercise])))
        }
      )
    )
  )

}
