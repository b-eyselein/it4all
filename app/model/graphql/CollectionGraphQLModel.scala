package model.graphql

import model.MongoClientQueries
import model.json.JsonProtocols
import model.tools._
import play.api.libs.json.Json
import sangria.macros.derive.{AddFields, deriveObjectType}
import sangria.schema.{Context, Field, IDType, ListType, LongType, ObjectType, OptionType, StringType}

import scala.concurrent.Future

trait CollectionGraphQLModel
    extends ToolGraphQLModels
    with ExerciseGraphQLModels
    with GraphQLArguments
    with MongoClientQueries {

  private def untypedExercises(exercises: Seq[Exercise[_, _ <: ExerciseContent[_]]]): Seq[UntypedExercise] = exercises

  private def untypedMaybeExercise(exercise: Option[Exercise[_, _ <: ExerciseContent[_]]]): Option[UntypedExercise] =
    exercise

  protected val CollectionType: ObjectType[GraphQLContext, ExerciseCollection] = deriveObjectType(
    AddFields(
      Field(
        "completeId",
        IDType,
        resolve = context => s"${context.value.collectionId}_${context.value.toolId}"
      ),
      Field(
        "asJsonString",
        StringType,
        resolve = context => Json.stringify(JsonProtocols.collectionFormat.writes(context.value))
      ),
      Field(
        "exerciseCount",
        LongType,
        resolve = context =>
          getExerciseCountForCollection(context.ctx.mongoDB, context.value.toolId, context.value.collectionId)
      ),
      Field(
        "exercises",
        ListType(exerciseType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None => ???
            case Some(tool) =>
              getExercisesForCollection(context.ctx.mongoDB, tool, context.value.collectionId)
                .map(untypedExercises)
          }
      ),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context => getExerciseUntyped(context)
      ),
      Field(
        "readExercises",
        ListType(StringType),
        resolve = context => {
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None => ???
            case Some(tool) =>
              ws.url(
                  s"$resourcesServerBaseUrl/${context.value.toolId}/collections/${context.value.collectionId}/exercises"
                )
                .get()
                .map(request => tool.toolJsonProtocol.validateAndWriteReadExerciseMessage(request.json))
          }
        }
      )
    )
  )

  private def getExerciseUntyped(
    context: Context[GraphQLContext, ExerciseCollection]
  ): Future[Option[UntypedExercise]] = ToolList.tools.find(_.id == context.value.toolId) match {
    case None => ???
    case Some(tool) =>
      getExercise(
        context.ctx.mongoDB,
        tool,
        context.value.collectionId,
        context.arg(exIdArgument),
        tool.toolJsonProtocol.exerciseFormat
      ).map(untypedMaybeExercise)
  }
}
