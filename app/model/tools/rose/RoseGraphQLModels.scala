package model.tools.rose

import model.tools.{SampleSolution, SemanticVersion, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, Interfaces, deriveEnumType, deriveObjectType}
import sangria.schema._

object RoseGraphQLModels extends ToolGraphQLModelBasics[RoseExercise, String, RoseExPart] {

  private val roseExTagType: EnumType[RoseExTag] = deriveEnumType()

  override val ExerciseType: ObjectType[Unit, RoseExercise] = {
    implicit val rett: EnumType[RoseExTag]                                    = roseExTagType
    implicit val svt: ObjectType[Unit, SemanticVersion]                       = semanticVersionType
    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType(
      Interfaces(exerciseInterfaceType),
      ExcludeFields("inputTypes")
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
