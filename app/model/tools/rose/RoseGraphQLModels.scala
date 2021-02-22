package model.tools.rose

import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object RoseGraphQLModels
    extends ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExPart, RoseResult]
    with GraphQLArguments {

  override val partEnumType: EnumType[RoseExPart] = EnumType(
    "RoseExPart",
    values = RoseExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  override val exerciseContentType: ObjectType[Unit, RoseExerciseContent] = deriveObjectType()

  override val solutionInputType: InputType[String] = StringType

  // Abstract result

  override val resultType: OutputType[RoseResult] = deriveObjectType[Unit, RoseResult](
    ExcludeFields("result"),
    ReplaceField("points", Field("points", FloatType, resolve = _.value.points.asDouble)),
    ReplaceField("maxPoints", Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble))
  )

}
