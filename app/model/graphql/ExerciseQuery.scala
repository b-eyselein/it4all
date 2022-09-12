package model.graphql

import model._
import model.tools.Helper.UntypedExercise
import model.tools.ToolList
import sangria.macros.derive._
import sangria.schema._

import scala.concurrent.Future

final case class GraphQLExPart(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  part: ExPart
)

trait ExPartQuery extends GraphQLBasics with GraphQLArguments {

  private val resolveSolved: Resolver[GraphQLExPart, Boolean] = context =>
    context.ctx.loggedInUser match {
      case None => Future.failed(new Exception(s"User is not logged in!"))
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

trait ExerciseQuery extends BasicGraphQLModels with ExPartQuery {

  private val exerciseContentUnionType = UnionType(
    "ExerciseContentUnionType",
    types = ToolList.tools.map(_.graphQlModels.exerciseContentType)
  )

  private val resolveTopicsWithLevels: Resolver[UntypedExercise, Seq[TopicWithLevel]] = context =>
    context.ctx.tableDefs.futureTopicsForExercise(context.value.toolId, context.value.collectionId, context.value.exerciseId)

  private val resolveParts: Resolver[UntypedExercise, Seq[GraphQLExPart]] = context =>
    Future.successful {
      context.value.content match {
        case exContent: ExerciseContentWithParts =>
          exContent.parts.map { exPart => GraphQLExPart(context.value.toolId, context.value.collectionId, context.value.exerciseId, exPart) }
        case _ => Seq.empty
      }
    }

  protected val exerciseType: ObjectType[GraphQLContext, UntypedExercise] = {

    implicit val x0: ObjectType[Unit, Level] = levelType

    val contentField: Field[GraphQLContext, UntypedExercise] = Field("content", exerciseContentUnionType, resolve = _.value.content)

    deriveObjectType(
      ReplaceField("content", contentField),
      AddFields(
        Field("topicsWithLevels", ListType(topicWithLevelType), resolve = resolveTopicsWithLevels),
        // FIXME: only for exercises with parts!
        Field("parts", ListType(exPartType), resolve = resolveParts)
      )
    )
  }

}
