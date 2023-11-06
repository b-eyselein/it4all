package model.graphql

import model._
import model.matching.StringMatcher.StringMatchingResult
import model.matching.{Match, MatchType, MatchingResult, StringMatch}
import sangria.macros.derive._
import sangria.schema._

import scala.annotation.unused
import scala.reflect.ClassTag

trait ToolGraphQLModel[SolInputType, EC <: ExerciseContent, ResType <: AbstractCorrectionResult] extends BasicGraphQLModels {

  // Arguments

  val solutionInputType: InputType[SolInputType]

  // Matching types

  protected val matchTypeType: EnumType[MatchType] = deriveEnumType()

  protected def buildStringMatchTypeType[T, M <: Match[T]](name: String)(implicit _x: ClassTag[M]): ObjectType[Unit, M] = ObjectType(
    name,
    fields[Unit, M](
      Field("matchType", matchTypeType, resolve = _.value.matchType),
      Field("sampleArg", StringType, resolve = _.value.sampleArgDescription),
      Field("userArg", StringType, resolve = _.value.userArgDescription)
    )
  )

  protected def matchingResultType[T, M <: Match[T], TO](
    name: String,
    mType: OutputType[M],
    tType: OutputType[TO],
    t2to: T => TO
  ): ObjectType[Unit, MatchingResult[T, M]] = ObjectType(
    s"${name}MatchingResult",
    fields[Unit, MatchingResult[T, M]](
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble),
      Field("allMatches", ListType(mType), resolve = _.value.allMatches),
      Field("notMatchedForUser", ListType(tType), resolve = _.value.notMatchedForUser.map(t2to)),
      Field("notMatchedForSample", ListType(tType), resolve = _.value.notMatchedForSample.map(t2to))
    )
  )

  private val stringMatchType: ObjectType[Unit, StringMatch] = {
    @unused implicit val mtt: EnumType[MatchType] = matchTypeType

    deriveObjectType()
  }

  protected val stringMatchingResultType: ObjectType[Unit, StringMatchingResult] = ObjectType(
    "StringMatchingResult",
    fields[Unit, StringMatchingResult](
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble),
      Field("allMatches", ListType(stringMatchType), resolve = _.value.allMatches),
      Field("notMatchedForUser", ListType(StringType), resolve = _.value.notMatchedForUser),
      Field("notMatchedForSample", ListType(StringType), resolve = _.value.notMatchedForSample)
    )
  )

  val exerciseContentType: ObjectType[Unit, EC]

  val resultType: OutputType[ResType]

}

trait ToolWithPartsGraphQLModel[SolInputType, EC <: ExerciseContent, ResType <: AbstractCorrectionResult, P <: ExPart]
    extends ToolGraphQLModel[SolInputType, EC, ResType] {

  val partEnumType: EnumType[P]

}
