package model

import model.persistence.ExerciseTableDefs
import model.tools.collectionTools._
import model.tools.randomTools.RandomExerciseToolMain
import model.tools.{ToolList, ToolState}
import play.api.libs.json.Format
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.concurrent.{ExecutionContext, Future}

case class GraphQLContext(
  tables: ExerciseTableDefs,
  ec: ExecutionContext,
  user: Option[User]
)

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
    fields[GraphQLContext, ExerciseCollection](
      Field("id", IntType, resolve = _.value.id),
      Field("title", StringType, resolve = _.value.title),
      Field("authors", ListType(StringType), resolve = _.value.authors),
      Field("text", StringType, resolve = _.value.text),
      Field("shortName", StringType, resolve = _.value.shortName),
      Field(
        "exerciseCount",
        IntType,
        resolve = context => context.ctx.tables.futureExerciseCountInColl(context.value.toolId, context.value.id)
      ),
      Field(
        "exercises",
        ListType(ExerciseType),
        resolve = context => context.ctx.tables.futureExercisesInColl(context.value.toolId, context.value.id)
      ),
      Field(
        "exercise",
        OptionType(ExerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context =>
          context.ctx.tables.futureExerciseById(context.value.toolId, context.value.id, context.arg(exIdArgument))
      )
    )
  )

  private val ToolType: ObjectType[GraphQLContext, CollectionToolMain] = ObjectType(
    "Tool",
    fields[GraphQLContext, CollectionToolMain](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.toolName),
      Field("state", ToolStateType, resolve = _.value.toolState),
      // Fields for lessons
      Field("lessonCount", IntType, resolve = context => context.ctx.tables.futureLessonCount(context.value.id)),
      Field(
        "lessons",
        ListType(LessonGraphQLModel.LessonType),
        resolve = context => context.ctx.tables.futureAllLessons(context.value.id)
      ),
      Field(
        "lesson",
        OptionType(LessonGraphQLModel.LessonType),
        arguments = lessonIdArgument :: Nil,
        resolve = context => context.ctx.tables.futureLessonById(context.value.id, context.arg(lessonIdArgument))
      ),
      // Fields for collections
      Field(
        "collectionCount",
        IntType,
        resolve = context => context.ctx.tables.futureCollectionCount(context.value.id)
      ),
      Field(
        "collections",
        ListType(CollectionType),
        resolve = context => context.ctx.tables.futureAllCollections(context.value.id)
      ),
      Field(
        "collection",
        OptionType(CollectionType),
        arguments = collIdArgument :: Nil,
        resolve = context => context.ctx.tables.futureCollById(context.value.id, context.arg(collIdArgument))
      ),
      // Special fields for exercises
      Field(
        "exerciseCount",
        IntType,
        resolve = context => context.ctx.tables.futureExerciseCountForTool(context.value.id)
      ),
      Field(
        "allExerciseMetaData",
        ListType(ExerciseType),
        resolve = context => context.ctx.tables.futureExercisesForTool(context.value.id)
      ),
      Field(
        "exerciseContent",
        OptionType(ExContentType),
        arguments = collIdArgument :: exIdArgument :: Nil,
        resolve = context =>
          context.ctx.tables.futureExerciseContentById(
            context.value.id,
            context.arg(collIdArgument),
            context.arg(exIdArgument),
            context.value.toolJsonProtocol.exerciseContentFormat
          )
      )
    )
  )

  private val QueryType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
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
    fields = toolValues.map[Field[GraphQLContext, Unit]] { toolMain =>
      implicit val solTypeFormat: Format[toolMain.SolType]   = toolMain.toolJsonProtocol.solutionFormat
      implicit val partTypeFormat: Format[toolMain.PartType] = toolMain.toolJsonProtocol.partTypeFormat

      val SolTypeInputArg  = Argument("solution", toolMain.graphQlModels.SolTypeInputType)
      val PartTypeInputArg = Argument("part", toolMain.graphQlModels.PartTypeInputType)

      Field(
        s"correct${toolMain.id.capitalize}",
        OptionType(toolMain.graphQlModels.CompResultTypeType),
        arguments = collIdArgument :: exIdArgument :: PartTypeInputArg :: SolTypeInputArg :: Nil,
        resolve = context =>
          context.ctx.user match {
            case None => Future.successful(None)
            case Some(user) =>
              implicit val executionContext: ExecutionContext = context.ctx.ec

              val collId   = context.arg(collIdArgument)
              val exId     = context.arg(exIdArgument)
              val part     = context.arg(PartTypeInputArg)
              val solution = context.arg(SolTypeInputArg)

              val correctionDbValues: Future[Option[(ExerciseCollection, Exercise)]] = for {
                maybeCollection <- context.ctx.tables.futureCollById(toolMain.id, collId)
                maybeExercise   <- context.ctx.tables.futureExerciseById(toolMain.id, collId, exId)

              } yield maybeCollection zip maybeExercise

              correctionDbValues.flatMap {
                case Some((collection, exercise)) =>
                  for {
                    solutionSaved <- context.ctx.tables.futureInsertSolution(
                      user.username,
                      exId,
                      exercise.semanticVersion,
                      collId,
                      toolMain.id,
                      part,
                      toolMain.toolJsonProtocol.solutionFormat.writes(solution)
                    )
                    result <- toolMain
                      .correctAbstract(user, solution, collection, exercise, part, solutionSaved)
                      .map(_.toOption)
                  } yield result

                case _ => ???
              }

          }
      )
    }
  )

  val schema: Schema[GraphQLContext, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
