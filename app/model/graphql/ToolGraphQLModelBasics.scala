package model.graphql

import model.core.matching.{Match, MatchType, MatchingResult}
import model.core.result.AbstractCorrectionResult
import model.tools._
import sangria.macros.derive._
import sangria.schema._

import scala.reflect.ClassTag

trait ToolGraphQLModelBasics[SolType, ContentType, PartType <: ExPart, ResType <: AbstractCorrectionResult]
    extends BasicGraphQLModels {

  protected def buildSampleSolutionType[S](
    name: String,
    solTypeType: OutputType[S]
  ): ObjectType[Unit, SampleSolution[S]] = deriveObjectType(
    ObjectTypeName(s"${name}SampleSolution"),
    ReplaceField("sample", Field("sample", solTypeType, resolve = _.value.sample))
  )

  // Matching types

  protected val matchTypeType: EnumType[MatchType] = deriveEnumType()

  private def matchingResultInterface[T, M <: Match[T]]: InterfaceType[Unit, MatchingResult[T, M]] = InterfaceType(
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

  protected def buildStringMatchTypeType[T, M <: Match[T]](name: String)(
    implicit _x: ClassTag[M]
  ): ObjectType[Unit, M] = ObjectType(
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
  ): ObjectType[Unit, MatchingResult[T, M]] = deriveObjectType(
    ObjectTypeName(s"${name}MatchingResult"),
    Interfaces(matchingResultInterface[T, M]),
    ExcludeFields("points", "maxPoints"),
    ReplaceField("allMatches", Field("allMatches", ListType(mType), resolve = _.value.allMatches))
  )

  protected val abstractResultInterfaceType: InterfaceType[Unit, AbstractCorrectionResult] = InterfaceType(
    "AbstractCorrectionResult",
    fields[Unit, AbstractCorrectionResult](
      Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    )
  )

  val exerciseContentType: OutputType[ContentType]

  val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, ResType]

  val sampleSolutionType: ObjectType[Unit, SampleSolution[SolType]]

  val SolTypeInputType: InputType[SolType]

  val partEnumType: EnumType[PartType]

}
