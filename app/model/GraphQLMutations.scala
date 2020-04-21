package model

import model.json.JsonProtocols
import model.tools.{Exercise, ExerciseCollection, ToolList}
import play.api.libs.json._
import sangria.marshalling.playJson._
import sangria.schema.{Argument, BooleanType, Field, ObjectType, OptionType, fields}

import scala.concurrent.{ExecutionContext, Future}

trait GraphQLMutations extends GraphQLArguments {

  protected implicit val ec: ExecutionContext

  private val correctionFields = ToolList.tools.map[Field[GraphQLContext, Unit]] { toolMain =>
    implicit val solTypeFormat: Format[toolMain.SolType] = toolMain.toolJsonProtocol.solutionFormat

    val SolTypeInputArg  = Argument("solution", toolMain.graphQlModels.SolTypeInputType)
    val PartTypeInputArg = Argument("part", toolMain.graphQlModels.partEnumType)

    Field(
      s"correct${toolMain.id.capitalize}",
      OptionType(toolMain.graphQlModels.AbstractResultTypeType),
      arguments = collIdArgument :: exIdArgument :: PartTypeInputArg :: SolTypeInputArg :: Nil,
      resolve = context =>
        context.ctx.user match {
          case None => Future.successful(None)
          case Some(user) =>
            val collId   = context.arg(collIdArgument)
            val exId     = context.arg(exIdArgument)
            val part     = context.arg(PartTypeInputArg)
            val solution = context.arg(SolTypeInputArg)

            val correctionDbValues
              : Future[Option[(ExerciseCollection, Exercise[toolMain.SolType, toolMain.ExContentType])]] = for {
              maybeCollection: Option[ExerciseCollection] <- context.ctx.tables.futureCollById(toolMain.id, collId)
              maybeExercise: Option[Exercise[toolMain.SolType, toolMain.ExContentType]] <- toolMain
                .futureExerciseTypeById(context.ctx.tables, collId, exId)
            } yield (maybeCollection zip maybeExercise)

            correctionDbValues.flatMap {
              case Some((collection, exercise)) =>
                for {
                  solutionSaved <- context.ctx.tables.futureInsertSolution(
                    user.username,
                    exId,
                    collId,
                    toolMain.id,
                    part,
                    toolMain.toolJsonProtocol.solutionFormat.writes(solution)
                  )

                  result <- toolMain
                    .correctAbstract(
                      user,
                      solution,
                      collection,
                      exercise,
                      part,
                      solutionSaved
                    )
                    .map(_.toOption)
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
        BooleanType,
        arguments = toolIdArgument :: contentArgument :: Nil,
        resolve = context => {
          val jsonCollection: JsValue = Json.parse(context.arg(contentArgument))

          JsonProtocols.collectionFormat.reads(jsonCollection) match {
            case JsError(_)               => Future.successful(false)
            case JsSuccess(collection, _) => context.ctx.tables.futureUpsertCollection(collection)
          }
        }
      ),
      Field(
        "upsertExercise",
        BooleanType,
        arguments = toolIdArgument :: contentArgument :: Nil,
        resolve = context => {
          ToolList.tools.find(_.id == context.arg(toolIdArgument)) match {
            case None => Future.successful(false)
            case Some(tool) =>
              val jsonExercise: JsValue = Json.parse(context.arg(contentArgument))

              tool.toolJsonProtocol.exerciseFormat.reads(jsonExercise) match {
                case JsError(_)             => Future.successful(false)
                case JsSuccess(exercise, _) => tool.futureUpsertExercise(context.ctx.tables, exercise)
              }
          }
        }
      )
    ) ++ correctionFields
  )

}
