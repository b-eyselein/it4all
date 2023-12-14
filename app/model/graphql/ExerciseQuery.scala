package model.graphql

import model._
import model.tools.Helper.UntypedExercise
import model.tools.ToolList
import sangria.schema._

import scala.concurrent.Future

final case class GraphQLExPart(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  part: ExPart
)

trait ExPartQuery extends GraphQLBasics {

  private val resolveSolved: Resolver[GraphQLExPart, Boolean] = context =>
    context.ctx.loggedInUser match {
      case None => Future.failed(MyUserFacingGraphQLError(s"User is not logged in!"))
      case Some(user) =>
        context.ctx.tableDefs.futureUserHasCorrectExerciseResult(
          context.value.toolId,
          context.value.collectionId,
          context.value.exerciseId,
          user.username,
          // FIXME: how to get none?
          Some(context.value.part.id)
        )
    }

  protected val exPartType: ObjectType[GraphQLContext, GraphQLExPart] = ObjectType(
    "ExPart",
    fields[GraphQLContext, GraphQLExPart](
      Field("id", StringType, resolve = _.value.part.id),
      Field("name", StringType, resolve = _.value.part.partName),
      Field("isEntryPart", BooleanType, resolve = _.value.part.isEntryPart),
      Field("solved", BooleanType, resolve = resolveSolved)
    )
  )

}

trait ExerciseQuery extends ExPartQuery {

  private val exerciseContentUnionType = UnionType(
    "ExerciseContentUnionType",
    types = ToolList.tools.map(_.graphQlModels.exerciseContentType)
  )

  private val resolveTopicsWithLevels: Resolver[UntypedExercise, Seq[TopicWithLevel]] = context =>
    context.ctx.tableDefs.futureTopicsForExercise(context.value.toolId, context.value.collectionId, context.value.exerciseId)

  private val resolveParts: Resolver[UntypedExercise, Seq[GraphQLExPart]] = context =>
    context.value.content.parts.map { exPart => GraphQLExPart(context.value.toolId, context.value.collectionId, context.value.exerciseId, exPart) }

  protected val exerciseType: ObjectType[GraphQLContext, UntypedExercise] = {

    ObjectType(
      "Exercise",
      fields[GraphQLContext, UntypedExercise](
        Field("exerciseId", IntType, resolve = _.value.exerciseId),
        Field("collectionId", IntType, resolve = _.value.collectionId),
        Field("toolId", StringType, resolve = _.value.toolId),
        Field("text", StringType, resolve = _.value.text),
        Field("title", StringType, resolve = _.value.title),
        Field("difficulty", Level.queryType, resolve = _.value.difficulty),
        Field("content", exerciseContentUnionType, resolve = _.value.content),
        Field("topicsWithLevels", ListType(TopicWithLevel.queryType), resolve = resolveTopicsWithLevels),
        // FIXME: only for exercises with parts!
        Field("parts", ListType(exPartType), resolve = resolveParts)
      )
    )
  }
}
