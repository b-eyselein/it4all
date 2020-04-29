package model.graphql

import model.MongoClientQueries
import model.json.JsonProtocols
import model.tools.{ToolList, UserSolution}
import play.api.libs.json._
import sangria.marshalling.playJson._
import sangria.schema._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait GraphQLMutations extends CollectionGraphQLModel with GraphQLArguments with MongoClientQueries {

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

            getExercise(context.ctx.mongoDB, toolMain, collId, exId, toolMain.toolJsonProtocol.exerciseFormat).flatMap {
              case Some(exercise) =>
                for {
                  solutionSaved <- insertSolution(
                    context.ctx.mongoDB,
                    UserSolution(exId, collId, toolMain.id, user.username, solution),
                    toolMain.toolJsonProtocol.userSolutionFormat
                  )

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
                  upsertExerciseCollection(context.ctx.mongoDB, collection).map {
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
                case JsError(_)             => Future.successful(None)
                case JsSuccess(exercise, _) =>
                  /*
                  tool.futureUpsertExercise(context.ctx.tables, exercise).map {
                    case false => None
                    case true  => Some(exercise)
                  }

                   */
                  None
              }
          }
        }
      )
    ) ++ correctionFields
  )

}
