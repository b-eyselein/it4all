package model.tools.collectionTools

import model.core.result.{CompleteResult, SuccessType}
import model.points.Points
import sangria.macros.derive.{InputObjectTypeName, deriveEnumType, deriveInputObjectType, deriveObjectType}
import sangria.schema.{EnumType, Field, InputObjectType, InputType, IntType, ObjectType, OutputType, StringType, fields}

trait ToolGraphQLModelBasics[ExContentType <: ExerciseContent, SolType, CompResultType <: CompleteResult, PartType <: ExPart] {

  protected val ExerciseFileType: ObjectType[Unit, ExerciseFile] = deriveObjectType()

  protected val ExerciseFileInputType: InputObjectType[ExerciseFile] = deriveInputObjectType(
    InputObjectTypeName("ExerciseFileInput")
  )

  protected val KeyValueObjectType: ObjectType[Unit, KeyValueObject] = deriveObjectType()

  protected val successTypeType: EnumType[SuccessType] = deriveEnumType()

  protected val pointsType: ObjectType[Unit, Points] = deriveObjectType()

  protected def sampleSolutionType[ASolType](
    name: String,
    SolTypeType: OutputType[ASolType]
  ): ObjectType[Unit, SampleSolution[ASolType]] =
    ObjectType(
      s"${name}SampleSolution",
      fields[Unit, SampleSolution[ASolType]](
        Field("id", IntType, resolve = _.value.id),
        Field("sample", SolTypeType, resolve = _.value.sample)
      )
    )

  protected val stringSampleSolutionType: ObjectType[Unit, SampleSolution[String]] =
    sampleSolutionType("String", StringType)

  val ExContentTypeType: ObjectType[Unit, ExContentType]

  val SolTypeInputType: InputType[SolType]

  val CompResultTypeType: ObjectType[Unit, CompResultType]

  val PartTypeInputType: EnumType[PartType]

}
