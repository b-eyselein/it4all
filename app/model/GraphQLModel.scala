package model

import model.persistence.ExerciseTableDefs
import model.tools.collectionTools._
import model.tools.randomTools.RandomExerciseToolMain
import model.tools.{ToolList, ToolState}
import sangria.macros.derive._
import sangria.schema._

object GraphQLModel {

  // Values

  private val toolValues: Seq[CollectionToolMain] = ToolList.toolMains
    .flatMap {
      case _: RandomExerciseToolMain => None
      case x: CollectionToolMain     => Some(x)
    }

  // Arguments

  private val toolId = Argument("toolId", StringType)

  private val IdArgument = Argument("id", IntType)

  // Types

  private val ToolStateType: EnumType[ToolState] = deriveEnumType()

  private implicit val SemanticVersionType: ObjectType[Unit, SemanticVersion] = deriveObjectType()

  private implicit val ExTagType: ObjectType[Unit, ExTag] = deriveObjectType()

  private val ExerciseType: ObjectType[Unit, Exercise] = deriveObjectType(
    ExcludeFields("content")
  )

  private val ExerciseMetaDataType: ObjectType[Unit, ExerciseMetaData] = deriveObjectType()

  private val CollectionType = ObjectType(
    "Collection",
    fields[ExerciseTableDefs, ExerciseCollection](
      Field("id", IntType, resolve = _.value.id),
      Field("title", StringType, resolve = _.value.title),
      Field("authors", ListType(StringType), resolve = _.value.authors),
      Field("text", StringType, resolve = _.value.text),
      Field("shortName", StringType, resolve = _.value.shortName),
      Field(
        "exercises",
        ListType(ExerciseMetaDataType),
        resolve = context => context.ctx.futureExerciseMetaDataForCollection(context.value.toolId, context.value.id)
      ),
      Field(
        "exercise",
        OptionType(ExerciseType),
        arguments = IdArgument :: Nil,
        resolve =
          context => context.ctx.futureExerciseById(context.value.toolId, context.value.id, context.arg(IdArgument))
      )
    )
  )

  private val ToolType: ObjectType[ExerciseTableDefs, CollectionToolMain] = ObjectType(
    "Tool",
    fields[ExerciseTableDefs, CollectionToolMain](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.toolName),
      Field("state", ToolStateType, resolve = _.value.toolState),
      Field(
        "collections",
        ListType(CollectionType),
        resolve = context => context.ctx.futureAllCollections(context.value.id)
      ),
      Field(
        "collection",
        OptionType(CollectionType),
        arguments = IdArgument :: Nil,
        resolve = context => context.ctx.futureCollById(context.value.id, context.arg(IdArgument))
      ),
      Field(
        "allExerciseMetaData",
        ListType(ExerciseMetaDataType),
        resolve = context => context.ctx.futureExerciseMetaDataForTool(context.value.id)
      )
    )
  )

  private val QueryType: ObjectType[ExerciseTableDefs, Unit] = ObjectType(
    "Query",
    fields[ExerciseTableDefs, Unit](
      Field("tools", ListType(ToolType), resolve = _ => toolValues),
      Field(
        "tool",
        OptionType(ToolType),
        arguments = toolId :: Nil,
        resolve = ctx => toolValues.find(_.id == ctx.arg(toolId))
      )
    )
  )

  val schema: Schema[ExerciseTableDefs, Unit] = Schema(QueryType)

}
