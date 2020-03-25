package model.tools.collectionTools.rose

import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, deriveObjectType}
import sangria.schema.ObjectType

object RoseGraphQLModels extends ToolGraphQLModelBasics[RoseExerciseContent] {

  override val ExContentTypeType: ObjectType[Unit, RoseExerciseContent] = {
    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType

    deriveObjectType(ExcludeFields("inputTypes"))
  }

}
