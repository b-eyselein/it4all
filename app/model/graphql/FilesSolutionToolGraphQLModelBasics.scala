package model.graphql

import model._
import sangria.macros.derive._
import sangria.schema._

trait FilesSolutionToolGraphQLModelBasics[C <: ExerciseContent, PT <: ExPart, ResType <: AbstractCorrectionResult]
    extends ToolGraphQLModelBasics[FilesSolutionInput, C, PT, ResType] {

  // Files solution input

  private val exerciseFileInputType: InputObjectType[ContentExerciseFile] = deriveInputObjectType(
    InputObjectTypeName("ExerciseFileInput")
  )

  override val solutionInputType: InputObjectType[FilesSolutionInput] = {
    implicit val efit: InputObjectType[ContentExerciseFile] = exerciseFileInputType

    deriveInputObjectType()
  }

  // Files solution output

  protected val exerciseFileType: ObjectType[Unit, ExerciseFile] = ObjectType(
    "ExerciseFile",
    fields[Unit, ExerciseFile](
      Field("name", StringType, resolve = _.value.name),
      Field("editable", BooleanType, resolve = _.value.editable),
      Field("content", StringType, resolve = _.value.content)
    )
  )

  protected val solutionOutputType: ObjectType[Unit, FilesSolution] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

}
