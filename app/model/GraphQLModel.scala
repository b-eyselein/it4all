package model

import model.persistence.ExerciseTableDefs
import model.tools.collectionTools._
import model.tools.randomTools.RandomExerciseToolMain
import model.tools.{ToolList, ToolState}
import play.api.libs.json.Format
import sangria.macros.derive._
import sangria.schema._
import sangria.marshalling.playJson._

object GraphQLModel {

  // Values

  private val toolValues: List[CollectionToolMain] = ToolList.toolMains.flatMap {
    case _: RandomExerciseToolMain => None
    case x: CollectionToolMain     => Some(x)
  }.toList

  // Arguments

  private val toolIdArgument = Argument("toolId", StringType)

  private val lessonIdArgument = Argument("lessonId", IntType)

  private val collIdArgument = Argument("collId", IntType)

  private val exIdArgument = Argument("exId", IntType)

  // Types

  private val ToolStateType: EnumType[ToolState] = deriveEnumType()

  private implicit val SemanticVersionType: ObjectType[Unit, SemanticVersion] = deriveObjectType()

  private implicit val ExTagType: ObjectType[Unit, ExTag] = deriveObjectType()

  private val ExContentType = UnionType(
    "ExContent",
    types = toolValues.map(_.graphQlModels.ExContentTypeType)
  )

  private val ExerciseType: ObjectType[Unit, Exercise] = deriveObjectType(
    ExcludeFields("content")
  )

  private val CollectionType = ObjectType(
    "Collection",
    fields[ExerciseTableDefs, ExerciseCollection](
      Field("id", IntType, resolve = _.value.id),
      Field("title", StringType, resolve = _.value.title),
      Field("authors", ListType(StringType), resolve = _.value.authors),
      Field("text", StringType, resolve = _.value.text),
      Field("shortName", StringType, resolve = _.value.shortName),
      Field(
        "exerciseCount",
        IntType,
        resolve = context => context.ctx.futureExerciseCountInColl(context.value.toolId, context.value.id)
      ),
      Field(
        "exercises",
        ListType(ExerciseType),
        resolve = context => context.ctx.futureExercisesInColl(context.value.toolId, context.value.id)
      ),
      Field(
        "exercise",
        OptionType(ExerciseType),
        arguments = exIdArgument :: Nil,
        resolve =
          context => context.ctx.futureExerciseById(context.value.toolId, context.value.id, context.arg(exIdArgument))
      )
    )
  )

  private val ToolType: ObjectType[ExerciseTableDefs, CollectionToolMain] = ObjectType(
    "Tool",
    fields[ExerciseTableDefs, CollectionToolMain](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.toolName),
      Field("state", ToolStateType, resolve = _.value.toolState),
      // Fields for lessons
      Field("lessonCount", IntType, resolve = context => context.ctx.futureLessonCount(context.value.id)),
      Field(
        "lessons",
        ListType(LessonGraphQLModel.LessonType),
        resolve = context => context.ctx.futureAllLessons(context.value.id)
      ),
      Field(
        "lesson",
        OptionType(LessonGraphQLModel.LessonType),
        arguments = lessonIdArgument :: Nil,
        resolve = context => context.ctx.futureLessonById(context.value.id, context.arg(lessonIdArgument))
      ),
      // Fields for collections
      Field("collectionCount", IntType, resolve = context => context.ctx.futureCollectionCount(context.value.id)),
      Field(
        "collections",
        ListType(CollectionType),
        resolve = context => context.ctx.futureAllCollections(context.value.id)
      ),
      Field(
        "collection",
        OptionType(CollectionType),
        arguments = collIdArgument :: Nil,
        resolve = context => context.ctx.futureCollById(context.value.id, context.arg(collIdArgument))
      ),
      // Special fields for exercises
      Field("exerciseCount", IntType, resolve = context => context.ctx.futureExerciseCountForTool(context.value.id)),
      Field(
        "allExerciseMetaData",
        ListType(ExerciseType),
        resolve = context => context.ctx.futureExercisesForTool(context.value.id)
      ),
      Field(
        "exerciseContent",
        OptionType(ExContentType),
        arguments = collIdArgument :: exIdArgument :: Nil,
        resolve = context =>
          context.ctx.futureExerciseContentById(
            context.value.id,
            context.arg(collIdArgument),
            context.arg(exIdArgument),
            context.value.toolJsonProtocol.exerciseContentFormat
          )
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
        arguments = toolIdArgument :: Nil,
        resolve = ctx => toolValues.find(_.id == ctx.arg(toolIdArgument))
      )
    )
  )

  private val MutationType = ObjectType(
    "Mutation",
    fields = toolValues.map[Field[Unit, Unit]](
      toolMain => {

        val capitalisedId = toolMain.id.capitalize

        implicit val solTypeFormat: Format[toolMain.SolType] = toolMain.toolJsonProtocol.solutionFormat

        val SolTypeInputArg = Argument(s"solution", toolMain.graphQlModels.SolTypeInputType)

        Field(
          s"correct$capitalisedId",
          OptionType(toolMain.graphQlModels.CompResultTypeType),
          arguments = collIdArgument :: exIdArgument :: SolTypeInputArg :: Nil,
          resolve = _ => None
        )
      }
    )
  )

  val schema: Schema[ExerciseTableDefs, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
