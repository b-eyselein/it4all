package model.graphql

import model._
import model.tools.Helper.UntypedExercise
import model.tools.ToolList
import sangria.macros.derive._
import sangria.schema._

import scala.concurrent.{ExecutionContext, Future}

final case class GraphQLExPart(
  toolId: String,
  collectionId: Int,
  exerciseId: Int,
  part: ExPart
)

trait ExerciseGraphQLModels extends BasicGraphQLModels with GraphQLArguments {

  protected implicit val ec: ExecutionContext

  private val exPartType: ObjectType[GraphQLContext, GraphQLExPart] = ObjectType(
    "ExPart",
    fields[GraphQLContext, GraphQLExPart](
      Field("id", StringType, resolve = _.value.part.id),
      Field("name", StringType, resolve = _.value.part.partName),
      Field("isEntryPart", BooleanType, resolve = _.value.part.isEntryPart),
      Field(
        "solved",
        BooleanType,
        resolve = context =>
          context.ctx.loggedInUser match {
            case None => Future.successful(false)
            case Some(user) =>
              context.ctx.tableDefs.futureUserHasCorrectExerciseResult(
                context.value.toolId,
                context.value.collectionId,
                context.value.exerciseId,
                user.username,
                context.value.part.id
              )
          }
      )
    )
  )

  private val exerciseContentUnionType = UnionType(
    "ExerciseContentUnionType",
    types = ToolList.tools.map(_.graphQlModels.exerciseContentType)
  )

  protected val exerciseType: ObjectType[GraphQLContext, UntypedExercise] = {
    implicit val x0: ObjectType[Unit, TopicWithLevel] = topicWithLevelType

    val contentField: Field[GraphQLContext, UntypedExercise] = Field("content", exerciseContentUnionType, resolve = _.value.content)

    deriveObjectType(
      ReplaceField("content", contentField),
      AddFields(
        Field(
          "parts",
          ListType(exPartType),
          resolve = context =>
            context.value.content.parts
              .map { exPart => GraphQLExPart(context.value.toolId, context.value.collectionId, context.value.exerciseId, exPart) }
        )
      )
    )
  }

}
