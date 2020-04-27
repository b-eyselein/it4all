package model

import model.json.JsonProtocols
import model.tools.{ExerciseCollection, ToolGraphQLModels, ToolList}
import play.api.libs.json.Json
import sangria.macros.derive.{AddFields, deriveObjectType}
import sangria.schema.{Field, IDType, IntType, ListType, ObjectType, OptionType, StringType}

trait CollectionGraphQLModel extends ToolGraphQLModels with ExerciseGraphQLModels with GraphQLArguments {

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
        IntType,
        resolve =
          context => context.ctx.tables.futureExerciseCountInColl(context.value.toolId, context.value.collectionId)
      ),
      Field(
        "exercises",
        ListType(exerciseType),
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None       => ???
            case Some(tool) => tool.futureExercisesInCollection(context.ctx.tables, context.value.collectionId)
          }
      ),
      Field(
        "exercise",
        OptionType(exerciseType),
        arguments = exIdArgument :: Nil,
        resolve = context =>
          ToolList.tools.find(_.id == context.value.toolId) match {
            case None => ???
            case Some(tool) =>
              tool.futureExerciseById(context.ctx.tables, context.value.collectionId, context.arg(exIdArgument))
          }
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

}
