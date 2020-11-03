package model.graphql

import model.mongo.MongoExercisePartResultQueries
import model.tools.Helper.UntypedExercise
import model.tools.{Tool, ToolList}
import model.{ExPart, LoggedInUser}
import sangria.schema._

trait ExerciseGraphQLModels extends BasicGraphQLModels with GraphQLArguments {
  self: MongoExercisePartResultQueries =>

  private val exPartType: ObjectType[Unit, (LoggedInUser, UntypedExercise, ExPart)] = ObjectType(
    "ExPart",
    fields[Unit, (LoggedInUser, UntypedExercise, ExPart)](
      Field("id", StringType, resolve = _.value._3.id),
      Field("name", StringType, resolve = _.value._3.partName),
      Field("isEntryPart", BooleanType, resolve = _.value._3.isEntryPart),
      Field(
        "solved",
        BooleanType,
        resolve = context =>
          futureExerciseResultById(context.value._1.username, context.value._2, context.value._3.id)
            .map {
              case None                          => false
              case Some(basicExercisePartResult) => basicExercisePartResult.isCorrect
            }
      )
    )
  )

  private val exerciseContentUnionType = UnionType(
    "ExerciseContentUnionType",
    types = ToolList.tools.map(_.graphQlModels.exerciseContentType)
  )

  protected val exerciseType: ObjectType[Unit, (LoggedInUser, Tool, UntypedExercise)] = ObjectType(
    "Exercise",
    fields[Unit, (LoggedInUser, Tool, UntypedExercise)](
      Field("exerciseId", IntType, resolve = _.value._3.exerciseId),
      Field("collectionId", IntType, resolve = _.value._3.collectionId),
      Field("toolId", StringType, resolve = _.value._3.toolId),
      Field("title", StringType, resolve = _.value._3.title),
      Field("authors", ListType(StringType), resolve = _.value._3.authors),
      Field("text", StringType, resolve = _.value._3.text),
      Field("topicsWithLevels", ListType(topicWithLevelType), resolve = _.value._3.topicsWithLevels),
      Field("difficulty", IntType, resolve = _.value._3.difficulty),
      Field("content", exerciseContentUnionType, resolve = _.value._3.content),
      Field(
        "parts",
        ListType(exPartType),
        resolve = context =>
          context.value._3.content.parts
            .map { exPart => (context.value._1, context.value._3, exPart) }
      )
    )
  )

}
