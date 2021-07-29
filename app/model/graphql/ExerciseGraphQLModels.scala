package model.graphql

import model.mongo.MongoExercisePartResultQueries
import model.tools.Helper.UntypedExercise
import model.tools.ToolList
import model.{ExPart, LoggedInUser}
import sangria.schema._

import scala.concurrent.Future

trait ExerciseGraphQLModels extends BasicGraphQLModels with GraphQLArguments {
  self: MongoExercisePartResultQueries =>

  private val exPartType: ObjectType[GraphQLContext, (UntypedExercise, ExPart)] = ObjectType(
    "ExPart",
    fields[GraphQLContext, (UntypedExercise, ExPart)](
      Field("id", StringType, resolve = _.value._2.id),
      Field("name", StringType, resolve = _.value._2.partName),
      Field("isEntryPart", BooleanType, resolve = _.value._2.isEntryPart),
      Field(
        "solved",
        OptionType(BooleanType),
        resolve = context => {
          context.ctx.loggedInUser match {
            case None => Future.successful(None)
            case Some(LoggedInUser(username, _)) =>
              futureExerciseResultById(username, context.value._1, context.value._2.id)
                .map {
                  case None                          => Some(false)
                  case Some(basicExercisePartResult) => Some(basicExercisePartResult.isCorrect)
                }
          }
        }
      )
    )
  )

  private val exerciseContentUnionType = UnionType(
    "ExerciseContentUnionType",
    types = ToolList.tools.map(_.graphQlModels.exerciseContentType)
  )

  protected val exerciseType: ObjectType[GraphQLContext, UntypedExercise] = ObjectType(
    "Exercise",
    fields[GraphQLContext, UntypedExercise](
      Field("exerciseId", IntType, resolve = _.value.exerciseId),
      Field("collectionId", IntType, resolve = _.value.collectionId),
      Field("toolId", StringType, resolve = _.value.toolId),
      Field("title", StringType, resolve = _.value.title),
      Field("authors", ListType(StringType), resolve = _.value.authors),
      Field("text", StringType, resolve = _.value.text),
      Field("topicsWithLevels", ListType(topicWithLevelType), resolve = _.value.topicsWithLevels),
      Field("difficulty", IntType, resolve = _.value.difficulty),
      Field("content", exerciseContentUnionType, resolve = _.value.content),
      Field(
        "parts",
        ListType(exPartType),
        resolve = context => context.value.content.parts.map { exPart => (context.value, exPart) }
      )
    )
  )

}
