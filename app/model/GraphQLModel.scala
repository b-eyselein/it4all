package model

import model.persistence.ExerciseTableDefs
import model.tools._
import model.tools.sql.SqlGraphQLModels
import play.api.libs.json.{Format, JsObject, Json}
import sangria.macros.derive._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.concurrent.{ExecutionContext, Future}

final case class GraphQLRequest(
  query: String,
  operationName: Option[String],
  variables: Option[JsObject]
)

final case class GraphQLContext(
  tables: ExerciseTableDefs,
  ec: ExecutionContext,
  user: Option[User]
)

object GraphQLModel extends ToolGraphQLModels {

  val graphQLRequestFormat: Format[GraphQLRequest] = Json.format

  // Arguments

  private val toolIdArgument = Argument("toolId", StringType)

  private val lessonIdArgument = Argument("lessonId", IntType)

  private val collIdArgument = Argument("collId", IntType)

  private val exIdArgument = Argument("exId", IntType)

  private val contentArgument = Argument("content", StringType)

  // Types

  private val toolStateType: EnumType[ToolState] = deriveEnumType()

  /*
  private val exerciseUnionType: ObjectType[Unit, Exercise[_]] = deriveObjectType(
    ExcludeFields("content"),
    AddFields(
      Field("asJson", StringType, resolve = context => ToolJsonProtocol.exerciseFormat.writes(context.value).toString())
    )
  )
   */

  private val CollectionType: ObjectType[GraphQLContext, ExerciseCollection] = deriveObjectType(
    AddFields(
      Field(
        "asJson",
        StringType,
        resolve = context => ToolJsonProtocol.collectionFormat.writes(context.value).toString()
      ),
      Field(
        "exerciseCount",
        IntType,
        resolve = context => context.ctx.tables.futureExerciseCountInColl(context.value.toolId, context.value.id)
      ),
      Field(
        "exercises",
        ListType(exerciseInterfaceType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None       => ???
            case Some(tool) => tool.futureExercisesInCollection(context.ctx.tables, context.value.id)(context.ctx.ec)
          }
      ),
      Field(
        "exercise",
        OptionType(exerciseInterfaceType),
        arguments = exIdArgument :: Nil,
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None => ???
            case Some(tool) =>
              implicit val ec: ExecutionContext = context.ctx.ec

              tool
                .futureExerciseById(context.ctx.tables, context.value.id, context.arg(exIdArgument))
                .map(_.asInstanceOf[Exercise])
          }
      )
    )
  )

  private val ToolType: ObjectType[GraphQLContext, CollectionTool] = ObjectType(
    "CollectionTol",
    fields[GraphQLContext, CollectionTool](
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("state", toolStateType, resolve = _.value.toolState),
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
      Field(
        "readCollections",
        ListType(StringType),
        resolve = _ => List()
      ),
      // Special fields for exercises
      Field(
        "exerciseCount",
        IntType,
        resolve = context => context.ctx.tables.futureExerciseCountForTool(context.value.id)
      ),
      Field(
        "allExerciseMetaData",
        ListType(exerciseInterfaceType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.id) match {
            case None       => ???
            case Some(tool) => tool.futureAllExercises(context.ctx.tables)(context.ctx.ec)
          }
      ) /*,
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
     */
    )
  )

  private val QueryType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Query",
    fields[GraphQLContext, Unit](
      Field("tools", ListType(ToolType), resolve = _ => ToolList.tools),
      Field(
        "tool",
        OptionType(ToolType),
        arguments = toolIdArgument :: Nil,
        resolve = ctx => ToolList.tools.find(_.id == ctx.arg(toolIdArgument))
      ),
      SqlGraphQLModels.dbContentQueryField
    )
  )

  private val correctionFields = ToolList.tools.map[Field[GraphQLContext, Unit]] { toolMain =>
    implicit val solTypeFormat: Format[toolMain.SolType]   = toolMain.toolJsonProtocol.solutionFormat
    implicit val partTypeFormat: Format[toolMain.PartType] = toolMain.toolJsonProtocol.partTypeFormat

    val SolTypeInputArg  = Argument("solution", toolMain.graphQlModels.SolTypeInputType)
    val PartTypeInputArg = Argument("part", toolMain.graphQlModels.PartTypeInputType)

    Field(
      s"correct${toolMain.id.capitalize}",
      OptionType(toolMain.graphQlModels.AbstractResultTypeType),
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

            val correctionDbValues: Future[Option[(ExerciseCollection, toolMain.ExerciseType)]] = for {
              maybeCollection <- context.ctx.tables.futureCollById(toolMain.id, collId)
              maybeExercise   <- toolMain.futureExerciseTypeById(context.ctx.tables, collId, exId)
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

  private val MutationType = ObjectType(
    "Mutation",
    fields = fields[GraphQLContext, Unit](
      Field(
        "upsertCollection",
        OptionType(CollectionType),
        arguments = toolIdArgument :: collIdArgument :: contentArgument :: Nil,
        resolve = _ => None
      )
    ) ++ correctionFields
  )

  val schema: Schema[GraphQLContext, Unit] = Schema(QueryType, mutation = Some(MutationType))

}
