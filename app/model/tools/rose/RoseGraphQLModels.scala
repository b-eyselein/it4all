package model.tools.rose

import model.tools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, Interfaces, deriveObjectType}
import sangria.schema._

object RoseGraphQLModels extends ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExercise, RoseExPart] {

  override val exerciseContentType: ObjectType[Unit, RoseExerciseContent] = {
    deriveObjectType(
      ExcludeFields("inputTypes")
    )
  }

  override val exerciseType: ObjectType[Unit, RoseExercise] = {
    implicit val ect: ObjectType[Unit, RoseExerciseContent]    = exerciseContentType
    implicit val sst: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType(
      Interfaces(exerciseInterfaceType),
      ExcludeFields("topics")
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
