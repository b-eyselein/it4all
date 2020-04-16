package model.tools.rose

import model.tools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, Interfaces, deriveObjectType}
import sangria.schema._

object RoseGraphQLModels extends ToolGraphQLModelBasics[RoseExercise, String, RoseExPart] {

  override val ExerciseType: ObjectType[Unit, RoseExercise] = {
    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType(
      Interfaces(exerciseInterfaceType),
      ExcludeFields("topics", "inputTypes")
    )
  }

  override val SolTypeInputType: InputType[String] = StringType

  override val AbstractResultTypeType: OutputType[Any] = {

    deriveObjectType[Unit, RoseCompleteResult](
      ExcludeFields("points", "maxPoints", "result")
    )
  }

  override val PartTypeInputType: EnumType[RoseExPart] = EnumType(
    "RoseExPart",
    values = RoseExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
