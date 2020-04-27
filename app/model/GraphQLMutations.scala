package model

import model.json.JsonProtocols
import model.tools.ToolList
import play.api.libs.json._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait GraphQLMutations extends CollectionGraphQLModel with GraphQLArguments {

  protected implicit val ec: ExecutionContext

  private val correctionFields = ToolList.tools.map[Field[GraphQLContext, Unit]] { toolMain =>
    implicit val solTypeFormat: Format[toolMain.SolType] = toolMain.toolJsonProtocol.solutionFormat

    val SolTypeInputArg  = Argument("solution", toolMain.graphQlModels.SolTypeInputType)
    val PartTypeInputArg = Argument("part", toolMain.graphQlModels.partEnumType)

    Field(
      s"correct${toolMain.id.capitalize}",
      toolMain.graphQlModels.toolAbstractResultTypeInterfaceType,
      arguments = collIdArgument :: exIdArgument :: PartTypeInputArg :: SolTypeInputArg :: Nil,
      resolve = context =>
        context.ctx.user match {
          case None => ??? // Future.successful(None)
          case Some(user) =>
            val collId   = context.arg(collIdArgument)
            val exId     = context.arg(exIdArgument)
            val part     = context.arg(PartTypeInputArg)
            val solution = context.arg(SolTypeInputArg)

            toolMain.futureExerciseTypeById(context.ctx.tables, collId, exId).flatMap {
              case Some(exercise) =>
                val solutionJson = toolMain.toolJsonProtocol.solutionFormat.writes(solution)

                for {
                  solutionSaved <- context.ctx.tables
                    .futureInsertSolution(user.username, exId, collId, toolMain.id, part, solutionJson)

                  result <- toolMain.correctAbstract(user, solution, exercise, part, solutionSaved)
                } yield result

              case _ => ???
            }

        }
    )
  }

  protected val MutationType: ObjectType[GraphQLContext, Unit] = ObjectType(
    "Mutation",
    fields = fields[GraphQLContext, Unit](
      Field(
        "upsertCollection",
        OptionType(CollectionType),
        arguments = toolIdArgument :: contentArgument :: Nil,
        resolve = context =>
          Try(Json.parse(context.arg(contentArgument))).fold(
            _ => Future.successful(None),
            jsonCollection =>
              JsonProtocols.collectionFormat.reads(jsonCollection) match {
                case JsError(_) => Future.successful(None)
                case JsSuccess(collection, _) =>
                  context.ctx.tables.futureUpsertCollection(collection).map {
                    case false => None
                    case true  => Some(collection)
                  }
              }
          )
      ),
      Field(
        "upsertExercise",
        OptionType(exerciseType),
        arguments = toolIdArgument :: contentArgument :: Nil,
        resolve = context => {
          ToolList.tools.find(_.id == context.arg(toolIdArgument)) match {
            case None => Future.successful(None)
            case Some(tool) =>
              val jsonExercise: JsValue = Json.parse(context.arg(contentArgument))

              tool.toolJsonProtocol.exerciseFormat.reads(jsonExercise) match {
                case JsError(_) => Future.successful(None)
                case JsSuccess(exercise, _) =>
                  tool.futureUpsertExercise(context.ctx.tables, exercise).map {
                    case false => None
                    case true  => Some(exercise)
                  }
              }
          }
        }
      )
    ) ++ correctionFields
  )

}
