package model.tools.collectionTools.rose

import model.points
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, deriveObjectType}
import sangria.schema.{InputType, ObjectType, StringType}

object RoseGraphQLModels extends ToolGraphQLModelBasics[RoseExerciseContent, String, RoseCompleteResult] {

  override val ExContentTypeType: ObjectType[Unit, RoseExerciseContent] = {
    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType(ExcludeFields("inputTypes"))
  }

  override val SolTypeInputType: InputType[String] = StringType

  override val CompResultTypeType: ObjectType[Unit, RoseCompleteResult] = {
    implicit val pt: ObjectType[Unit, points.Points] = pointsType

    deriveObjectType(
      ExcludeFields("result")
    )
  }

}
