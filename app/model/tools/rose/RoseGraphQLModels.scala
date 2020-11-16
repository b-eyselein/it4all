package model.tools.rose

import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

object RoseGraphQLModels
    extends ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExPart, RoseAbstractResult]
    with GraphQLArguments {

  override val partEnumType: EnumType[RoseExPart] = EnumType(
    "RoseExPart",
    values = RoseExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  override val exerciseContentType: ObjectType[Unit, RoseExerciseContent] = deriveObjectType()

  override val SolTypeInputType: InputType[String] = StringType

  // Abstract result

  private val roseAbstractResultType: InterfaceType[Unit, RoseAbstractResult] = InterfaceType(
    "RoseAbstractResult",
    fields[Unit, RoseAbstractResult](
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    ),
    interfaces[Unit, RoseAbstractResult](abstractResultInterfaceType)
  ).withPossibleTypes(() => List(roseResultType, roseInternalErrorResultType))

  private val roseResultType: ObjectType[Unit, RoseResult] = deriveObjectType[Unit, RoseResult](
    Interfaces(roseAbstractResultType),
    ExcludeFields("points", "maxPoints", "result"),
    AddFields(
      Field("_x", BooleanType, resolve = _ => false)
    )
  )

  private val roseInternalErrorResultType: ObjectType[Unit, RoseInternalErrorResult] = deriveObjectType(
    Interfaces(roseAbstractResultType),
    ExcludeFields("maxPoints")
  )

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, RoseAbstractResult] = roseAbstractResultType

}
