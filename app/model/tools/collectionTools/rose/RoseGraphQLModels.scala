package model.tools.collectionTools.rose

import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, deriveObjectType}
import sangria.schema._

object RoseGraphQLModels extends ToolGraphQLModelBasics[RoseExerciseContent, String, RoseExPart] {

  override val ExContentTypeType: ObjectType[Unit, RoseExerciseContent] = {
    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType(ExcludeFields("inputTypes"))
  }

  override val SolTypeInputType: InputType[String] = StringType

  override val AbstractResultTypeType: OutputType[Any] = {

    deriveObjectType[Unit, RoseCompleteResult](
      ExcludeFields("points", "maxPoints", "result")
    )
  }

  override val PartTypeInputType: EnumType[RoseExPart] = EnumType(
    "RoseExPart",
    values = RoseExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
