package model.graphql

import model._
import model.matching.{Match, MatchType, MatchingResult}
import model.result.AbstractCorrectionResult
import sangria.macros.derive._
import sangria.schema._

import scala.reflect.ClassTag

trait ToolGraphQLModelBasics[S, C <: ExerciseContent, PT <: ExPart, ResType <: AbstractCorrectionResult]
    extends BasicGraphQLModels {

  // Arguments

  val SolTypeInputType: InputType[S]

  val partEnumType: EnumType[PT]

  protected def buildSampleSolutionType(
    name: String,
    solTypeType: OutputType[S]
  ): ObjectType[Unit, SampleSolution[S]] =
    deriveObjectType(
      ObjectTypeName(s"${name}SampleSolution"),
      ReplaceField("sample", Field("sample", solTypeType, resolve = _.value.sample))
    )

  // Matching types

  protected val matchTypeType: EnumType[MatchType] = deriveEnumType()

  private def matchingResultInterface[T, M <: Match[T]]: InterfaceType[Unit, MatchingResult[T, M]] =
    InterfaceType(
      "MatchingResult",
      () =>
        fields(
          Field("points", FloatType, resolve = _.value.points.asDouble),
          Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble),
          Field("allMatches", ListType(newMatchInterface), resolve = _.value.allMatches)
        )
    )

  protected val newMatchInterface: InterfaceType[Unit, Match[_]] = InterfaceType(
    "NewMatch",
    fields[Unit, Match[_]](
      Field("matchType", matchTypeType, resolve = _.value.matchType),
      Field("userArgDescription", OptionType(StringType), resolve = _.value.userArgDescription),
      Field("sampleArgDescription", OptionType(StringType), resolve = _.value.sampleArgDescription)
    )
  )

  protected def buildStringMatchTypeType[T, M <: Match[T]](
    name: String
  )(implicit _x: ClassTag[M]): ObjectType[Unit, M] =
    ObjectType(
      name,
      interfaces[Unit, M](newMatchInterface),
      fields[Unit, M](
        Field("sampleArg", OptionType(StringType), resolve = _.value.sampleArgDescription),
        Field("userArg", OptionType(StringType), resolve = _.value.userArgDescription)
      )
    )

  protected def matchingResultType[T, M <: Match[T]](
    name: String,
    mType: OutputType[M]
  ): ObjectType[Unit, MatchingResult[T, M]] =
    deriveObjectType(
      ObjectTypeName(s"${name}MatchingResult"),
      Interfaces(matchingResultInterface[T, M]),
      ExcludeFields("points", "maxPoints"),
      ReplaceField("allMatches", Field("allMatches", ListType(mType), resolve = _.value.allMatches))
    )

  protected val abstractResultInterfaceType: InterfaceType[Unit, AbstractCorrectionResult] = InterfaceType(
    "AbstractCorrectionResult",
    fields[Unit, AbstractCorrectionResult](
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    )
  )

  val exerciseContentType: ObjectType[Unit, C]

  val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, ResType]

  val sampleSolutionType: ObjectType[Unit, SampleSolution[S]]

}

trait FilesSolutionToolGraphQLModelBasics[C <: ExerciseContent, PT <: ExPart, ResType <: AbstractCorrectionResult]
    extends ToolGraphQLModelBasics[FilesSolution, C, PT, ResType] {

  override val SolTypeInputType: InputObjectType[FilesSolution] = {
    implicit val efit: InputObjectType[ExerciseFile] = exerciseFileInputType

    deriveInputObjectType(
      InputObjectTypeName("FilesSolutionInput")
    )
  }

  private val solutionType: ObjectType[Unit, FilesSolution] = {
    implicit val exFileType: ObjectType[Unit, ExerciseFile] = exerciseFileType

    deriveObjectType()
  }

  override val sampleSolutionType: ObjectType[Unit, SampleSolution[FilesSolution]] =
    buildSampleSolutionType("Files", solutionType)

}
