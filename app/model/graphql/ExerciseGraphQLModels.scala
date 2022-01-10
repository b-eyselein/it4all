package model.graphql

import model._
import model.mongo.MongoExercisePartResultQueries
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

trait ExerciseGraphQLModels extends BasicGraphQLModels with GraphQLArguments {
  self: MongoExercisePartResultQueries =>

  private val stringTextPartType: ObjectType[Unit, StringTextPart] = deriveObjectType()

  private val highlightedTextPartType: ObjectType[Unit, HighlightedTextPart] = deriveObjectType()

  private val textPartType: UnionType[Unit] = UnionType(
    "TextPart",
    types = List(stringTextPartType, highlightedTextPartType)
  )

  private val exerciseTextTextParagraphType: ObjectType[Unit, ExerciseTextTextParagraph] = deriveObjectType(
    ReplaceField(
      "textParts",
      Field(
        "textParts",
        ListType(textPartType),
        resolve = context => {
          println("resolving text textParts")
          context.value.textParts
        }
      )
    )
  )

  private val bulletListPointsType: ObjectType[Unit, BulletListPoint] = deriveObjectType(
    ReplaceField(
      "textParts",
      Field(
        "textParts",
        ListType(textPartType),
        resolve = context => {
          println("resolving bullet list textParts")
          context.value.textParts
        }
      )
    )
  )

  private val exerciseTextListParagraphType: ObjectType[Unit, ExerciseTextListParagraph] = {
    implicit val x: ObjectType[Unit, BulletListPoint] = bulletListPointsType

    deriveObjectType()
  }

  private val exerciseTextParagraphType: UnionType[Unit] = UnionType(
    "ExerciseTextParagraph",
    types = List(exerciseTextTextParagraphType, exerciseTextListParagraphType)
  )

  private val exPartType: ObjectType[GraphQLContext, GraphQLExPart] = ObjectType(
    "ExPart",
    fields[GraphQLContext, GraphQLExPart](
      Field("id", StringType, resolve = _.value.part.id),
      Field("name", StringType, resolve = _.value.part.partName),
      Field("isEntryPart", BooleanType, resolve = _.value.part.isEntryPart),
      Field(
        "solved",
        OptionType(BooleanType),
        resolve = context => {
          context.ctx.loggedInUser match {
            case None => Future.successful(None)
            case Some(LoggedInUser(username, _)) =>
              futureExerciseResultById(username, context.value.toolId, context.value.collectionId, context.value.exerciseId, context.value.part.id)
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

  protected val exerciseType: ObjectType[GraphQLContext, UntypedExercise] = {
    implicit val x0: ObjectType[Unit, TopicWithLevel] = topicWithLevelType

    val contentField: Field[GraphQLContext, UntypedExercise] = Field("content", exerciseContentUnionType, resolve = _.value.content)

    val newExerciseTextField: Field[GraphQLContext, UntypedExercise] = Field(
      "newExerciseText",
      ListType(exerciseTextParagraphType),
      resolve = context => {
        // FIXME: remove println!
        context.value.newExerciseText.foreach((x) => println("\t" + x))
        println("---------------------------------------")
        context.value.newExerciseText
      }
    )

    deriveObjectType(
      ReplaceField("content", contentField),
      ReplaceField("newExerciseText", newExerciseTextField),
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
