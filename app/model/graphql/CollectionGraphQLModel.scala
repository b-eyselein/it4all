package model.graphql

import model._
import model.mongo.MongoClientQueries
import model.tools.Tool
import sangria.schema._

trait CollectionGraphQLModel extends BasicGraphQLModels with ExerciseGraphQLModels with GraphQLArguments with MongoClientQueries {

  protected val collectionType: ObjectType[GraphQLContext, (Tool, ExerciseCollection)] = ObjectType(
    "ExerciseCollection",
    fields[GraphQLContext, (Tool, ExerciseCollection)](
      Field("collectionId", IntType, resolve = _.value._2.collectionId),
      Field("title", StringType, resolve = _.value._2.title),
      Field("authors", ListType(StringType), resolve = _.value._2.authors),
      Field(
        "exerciseCount",
        LongType,
        resolve = context => futureExerciseCountForCollection(context.value._1.id, context.value._2.collectionId)
      ),
      Field(
        "exercises",
        ListType(exerciseType),
        resolve = context => futureExercisesForCollection(context.value._2.toolId, context.value._2.collectionId)
      ),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context => futureExerciseById(context.value._1, context.value._2.collectionId, context.arg(exIdArgument))
      )
    )
  )

}
