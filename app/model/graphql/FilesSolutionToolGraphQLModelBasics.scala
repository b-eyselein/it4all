package model.graphql

import model._
import sangria.schema._

trait FilesSolutionToolGraphQLModelBasics[EC <: ExerciseContent, ResType <: AbstractCorrectionResult] {
  self: ToolGraphQLModel[FilesSolutionInput, EC, ResType] =>

  override val solutionInputType: InputObjectType[FilesSolutionInput] = InputObjectType[FilesSolutionInput](
    "FilesSolutionInput",
    List(
      InputField("files", ListInputType(ExerciseFile.inputType))
    )
  )

}
