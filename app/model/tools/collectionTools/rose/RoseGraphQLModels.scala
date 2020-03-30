package model.tools.collectionTools.rose

import model.points
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, deriveObjectType}
import sangria.schema.{EnumType, EnumValue, InputType, ObjectType, StringType}

object RoseGraphQLModels extends ToolGraphQLModelBasics[RoseExerciseContent, String, RoseCompleteResult, RoseExPart] {

  override val ExContentTypeType: ObjectType[Unit, RoseExerciseContent] = {
    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType(ExcludeFields("inputTypes"))
  }

  override val SolTypeInputType: InputType[String] = StringType

  override val AbstractResultTypeType: ObjectType[Unit, RoseCompleteResult] = {
    implicit val pt: ObjectType[Unit, points.Points] = pointsType

    deriveObjectType(
      ExcludeFields("result")
    )
  }

  override val PartTypeInputType: EnumType[RoseExPart] = EnumType(
    "RoseExPart",
    values = RoseExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
