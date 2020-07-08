package model.tools.rose

import model.SampleSolution
import model.graphql.{GraphQLArguments, ToolGraphQLModelBasics}
import model.tools.ToolJsonProtocol
import sangria.macros.derive.{ExcludeFields, Interfaces, deriveObjectType}
import sangria.schema._

object RoseGraphQLModels
    extends ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExPart, RoseAbstractResult]
    with GraphQLArguments {

  override protected val jsonFormats: ToolJsonProtocol[String, RoseExerciseContent, RoseExPart] = RoseToolJsonProtocol

  override val partEnumType: EnumType[RoseExPart] = EnumType(
    "RoseExPart",
    values = RoseExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  override val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] =
    buildSampleSolutionType("Rose", StringType)

  override val exerciseContentType: ObjectType[Unit, RoseExerciseContent] = {
    implicit val sst: ObjectType[Unit, SampleSolution[String]] = sampleSolutionType

    deriveObjectType(
      ExcludeFields("inputTypes")
    )
  }

  override val SolTypeInputType: InputType[String] = StringType

  // Abstract result

  private val roseAbstractResultType: InterfaceType[Unit, RoseAbstractResult] = InterfaceType(
    "RoseAbstractResult",
    fields[Unit, RoseAbstractResult](
      Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    ),
    interfaces[Unit, RoseAbstractResult](abstractResultInterfaceType)
  ).withPossibleTypes(() => List(roseResultType, roseInternalErrorResultType))

  private val roseResultType: ObjectType[Unit, RoseResult] = deriveObjectType[Unit, RoseResult](
    Interfaces(roseAbstractResultType),
    ExcludeFields("points", "maxPoints", "result")
  )

  private val roseInternalErrorResultType: ObjectType[Unit, RoseInternalErrorResult] = deriveObjectType(
    Interfaces(roseAbstractResultType),
    ExcludeFields("maxPoints")
  )

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, RoseAbstractResult] = roseAbstractResultType

}
