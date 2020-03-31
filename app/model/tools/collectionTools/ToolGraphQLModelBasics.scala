package model.tools.collectionTools

import model.core.result.{AbstractCorrectionResult, SuccessType}
import model.points.Points
import sangria.macros.derive.{InputObjectTypeName, deriveEnumType, deriveInputObjectType, deriveObjectType}
import sangria.schema._

trait ToolGraphQLModelBasics[
  ExContentType <: ExerciseContent,
  SolType,
  CompResultType <: AbstractCorrectionResult,
  PartType <: ExPart
] {

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

  protected val abstractResultTypeType: InterfaceType[Unit, AbstractCorrectionResult] = InterfaceType(
    "AbstractCorrectionResult",
    fields[Unit, AbstractCorrectionResult](
      Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
      Field("points", pointsType, resolve = _.value.points),
      Field("maxPoints", pointsType, resolve = _.value.maxPoints)
    )
  )

  val ExContentTypeType: ObjectType[Unit, ExContentType]

  val AbstractResultTypeType: OutputType[Any]

  val SolTypeInputType: InputType[SolType]

  val PartTypeInputType: EnumType[PartType]

}
