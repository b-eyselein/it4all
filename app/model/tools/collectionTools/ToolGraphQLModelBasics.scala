package model.tools.collectionTools

import model.core.matching.{Match, MatchType, MatchingResult}
import model.core.result.{AbstractCorrectionResult, SuccessType}
import model.points.Points
import sangria.macros.derive._
import sangria.schema._

trait ToolGraphQLModelBasics[
  ExContentType <: ExerciseContent,
  SolType,
  PartType <: ExPart
] {

  protected val ExerciseFileType: ObjectType[Unit, ExerciseFile] = deriveObjectType()

  protected val ExerciseFileInputType: InputObjectType[ExerciseFile] = deriveInputObjectType(
    InputObjectTypeName("ExerciseFileInput")
  )

  protected val KeyValueObjectType: ObjectType[Unit, KeyValueObject] = deriveObjectType()

  protected val successTypeType: EnumType[SuccessType] = deriveEnumType()

  protected val pointsType: ObjectType[Unit, Points] = deriveObjectType()

  // Sample solution types

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

  // Matching types

  protected val matchTypeType: EnumType[MatchType] = deriveEnumType()

  private def matchingResultInterface[T, M <: Match[T]]: InterfaceType[Unit, MatchingResult[T, M]] = InterfaceType(
    "MatchingResult",
    () =>
      fields(
        Field("allMatches", ListType(matchInterface[T, M]), resolve = _.value.allMatches),
        Field("points", pointsType, resolve = _.value.points),
        Field("maxPoints", pointsType, resolve = _.value.maxPoints)
      )
  )

  protected def matchInterface[T, M <: Match[T]]: InterfaceType[Unit, M] = InterfaceType(
    "Match",
    () =>
      fields(
        Field("matchType", OptionType(matchTypeType), resolve = _.value.matchType),
        Field("sampleArg", OptionType(StringType), resolve = _.value.sampleArgDescription),
        Field("userArg", OptionType(StringType), resolve = _.value.userArgDescription)
      )
  )

  protected def matchingResultType[T, M <: Match[T]](
    name: String,
    mType: OutputType[M]
  ): ObjectType[Unit, MatchingResult[T, M]] = {
    implicit val pt: ObjectType[Unit, Points] = pointsType

    deriveObjectType(
      ObjectTypeName(s"${name}MatchingResult"),
      Interfaces(matchingResultInterface[T, M]),
      ReplaceField("allMatches", Field("allMatches", ListType(mType), resolve = _.value.allMatches))
    )
  }

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
