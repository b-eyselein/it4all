package model.graphql

import model._
import model.matching.StringMatcher.StringMatchingResult
import model.matching.{Match, MatchType, MatchingResult, StringMatch}
import model.result.AbstractCorrectionResult
import sangria.macros.derive._
import sangria.schema._

import scala.reflect.ClassTag

trait ToolGraphQLModelBasics[SolutionInputType, C <: ExerciseContent, PT <: ExPart, ResType <: AbstractCorrectionResult]
    extends BasicGraphQLModels {

  // Arguments

  val solutionInputType: InputType[SolutionInputType]

  val partEnumType: EnumType[PT]

  // Matching types

  protected val matchTypeType: EnumType[MatchType] = deriveEnumType()

  private def matchingResultInterface[T, M <: Match[T]](
    describeArg: T => String
  ): InterfaceType[Unit, MatchingResult[T, M]] =
    InterfaceType(
      "MatchingResult",
      () =>
        fields(
          Field("points", FloatType, resolve = _.value.points.asDouble),
          Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble),
          Field("allMatches", ListType(newMatchInterface), resolve = _.value.allMatches),
          Field("notMatchedForUserString", ListType(StringType), resolve = _.value.notMatchedForUser.map(describeArg)),
          Field(
            "notMatchedForSampleString",
            ListType(StringType),
            resolve = _.value.notMatchedForSample.map(describeArg)
          )
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
  )(implicit _x: ClassTag[M]): ObjectType[Unit, M] = ObjectType(
    name,
    interfaces[Unit, M](newMatchInterface),
    fields[Unit, M](
      Field("sampleArg", OptionType(StringType), resolve = _.value.sampleArgDescription),
      Field("userArg", OptionType(StringType), resolve = _.value.userArgDescription)
    )
  )

  protected def matchingResultType[T, M <: Match[T], TO](
    name: String,
    mType: OutputType[M],
    tType: OutputType[TO],
    t2to: T => TO,
    describeArg: T => String = (t: T) => t.toString
  ): ObjectType[Unit, MatchingResult[T, M]] = deriveObjectType(
    ObjectTypeName(s"${name}MatchingResult"),
    Interfaces(matchingResultInterface[T, M](describeArg)),
    ExcludeFields("points", "maxPoints"),
    ReplaceField("allMatches", Field("allMatches", ListType(mType), resolve = _.value.allMatches)),
    ReplaceField(
      "notMatchedForUser",
      Field("notMatchedForUser", ListType(tType), resolve = _.value.notMatchedForUser.map(t2to))
    ),
    ReplaceField(
      "notMatchedForSample",
      Field("notMatchedForSample", ListType(tType), resolve = _.value.notMatchedForUser.map(t2to))
    )
  )

  private val stringMatchType: ObjectType[Unit, StringMatch] = {
    implicit val mtt: EnumType[MatchType] = matchTypeType

    deriveObjectType()
  }

  protected val stringMatchingResultType: ObjectType[Unit, StringMatchingResult] =
    ObjectType(
      "StringMatchingResult",
      fields[Unit, StringMatchingResult](
        Field("points", FloatType, resolve = _.value.points.asDouble),
        Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble),
        Field("allMatches", ListType(stringMatchType), resolve = _.value.allMatches),
        Field("notMatchedForUser", ListType(StringType), resolve = _.value.notMatchedForUser),
        Field("notMatchedForSample", ListType(StringType), resolve = _.value.notMatchedForSample)
      )
    )

  // Result

  protected val abstractResultInterfaceType: InterfaceType[Unit, AbstractCorrectionResult] = InterfaceType(
    "AbstractCorrectionResult",
    fields[Unit, AbstractCorrectionResult](
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    )
  )

  val exerciseContentType: ObjectType[Unit, C]

  val resultType: OutputType[ResType]

}
