package model.tools.rose

import model.tools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, deriveObjectType}
import sangria.schema._

object RoseGraphQLModels extends ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExPart] {

  override val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] =
    buildSampleSolutionType("Rose", StringType)

  override val exerciseContentType: ObjectType[Unit, RoseExerciseContent] = {
    implicit val sst: ObjectType[Unit, SampleSolution[String]] = sampleSolutionType

    deriveObjectType(
      ExcludeFields("inputTypes")
    )
  }

  override val SolTypeInputType: InputType[String] = StringType

  override val AbstractResultTypeType: OutputType[Any] = deriveObjectType[Unit, RoseCompleteResult](
    ExcludeFields("points", "maxPoints", "result")
  )

  override val PartTypeInputType: EnumType[RoseExPart] = EnumType(
    "RoseExPart",
    values = RoseExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
