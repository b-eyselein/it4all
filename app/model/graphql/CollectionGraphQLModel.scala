package model.graphql

import model._
import model.mongo.MongoClientQueries
import model.tools.Helper.UntypedExercise
import model.tools.Tool
import sangria.schema._

trait CollectionGraphQLModel
    extends BasicGraphQLModels
    with ExerciseGraphQLModels
    with GraphQLArguments
    with MongoClientQueries {

  protected val collectionType: ObjectType[Unit, ((LoggedInUser, Tool), ExerciseCollection)] = ObjectType(
    "ExerciseCollection",
    fields[Unit, ((LoggedInUser, Tool), ExerciseCollection)](
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
        resolve = context =>
          futureExercisesForCollection(context.value._1._2, context.value._2.collectionId)
            .map(exes => exes.map(ex => (context.value._1._1, context.value._1._2, ex.asInstanceOf[UntypedExercise])))
      ),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context => {
          val collectionId = context.value._2.collectionId

          futureExerciseById(context.value._1._2, collectionId, context.arg(exIdArgument))
            .map(maybeEx =>
              maybeEx.map(ex => (context.value._1._1, context.value._1._2, ex.asInstanceOf[UntypedExercise]))
            )
        }
      )
    )
  )

}
