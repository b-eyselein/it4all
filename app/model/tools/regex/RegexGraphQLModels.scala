package model.tools.regex

import model.graphql.{GraphQLArguments, ToolGraphQLModel}
import model.tools.regex.RegexTool.ExtractedValuesComparison
import sangria.macros.derive._
import sangria.schema._

import scala.annotation.unused
import scala.util.matching.Regex.{Match => RegexMatch}

object RegexGraphQLModels extends ToolGraphQLModel[String, RegexExerciseContent, RegexAbstractResult] with GraphQLArguments {

  // Enum types

  private val regexCorrectionTypeType: EnumType[RegexCorrectionType] = deriveEnumType()

  private val binaryClassificationResultTypeType: EnumType[BinaryClassificationResultType] = deriveEnumType()

  // Exercise content types

  override val exerciseContentType: ObjectType[Unit, RegexExerciseContent] = {
    @unused implicit val rctt: EnumType[RegexCorrectionType]              = regexCorrectionTypeType
    @unused implicit val rmtdt: ObjectType[Unit, RegexMatchTestData]      = deriveObjectType()
    @unused implicit val retdt: ObjectType[Unit, RegexExtractionTestData] = deriveObjectType()

    deriveObjectType()
  }

  // Solution types

  override val solutionInputType: InputType[String] = StringType

  // Result types

  private val regexMatchingEvaluationResultType: ObjectType[Unit, RegexMatchingSingleResult] = {
    @unused implicit val bcrtt: EnumType[BinaryClassificationResultType] = binaryClassificationResultTypeType

    deriveObjectType()
  }

  private val regexMatchMatchType: ObjectType[Unit, RegexMatchMatch] =
    buildStringMatchTypeType[RegexMatch, RegexMatchMatch]("RegexMatchMatch")

  private val regexExtractionEvaluationResultType: ObjectType[Unit, RegexExtractionSingleResult] = {
    @unused implicit val extractedValuesComparisonType: ObjectType[Unit, ExtractedValuesComparison] =
      matchingResultType("RegexExtractedValuesComparison", regexMatchMatchType, StringType, _.source.toString)

    deriveObjectType()
  }

  // Abstract result

  private val regexAbstractResultType: InterfaceType[Unit, RegexAbstractResult] = InterfaceType(
    "RegexAbstractResult",
    fields[Unit, RegexAbstractResult](
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    )
  ).withPossibleTypes(() => List(regexMatchingResultType, regexExtractionResultType))

  private val regexMatchingResultType: ObjectType[Unit, RegexMatchingResult] = {
    @unused implicit val rmert: ObjectType[Unit, RegexMatchingSingleResult] = regexMatchingEvaluationResultType

    deriveObjectType(
      Interfaces(regexAbstractResultType),
      ExcludeFields("points", "maxPoints")
    )
  }

  private val regexExtractionResultType: ObjectType[Unit, RegexExtractionResult] = {
    @unused implicit val reert: ObjectType[Unit, RegexExtractionSingleResult] = regexExtractionEvaluationResultType

    deriveObjectType(
      Interfaces(regexAbstractResultType),
      ExcludeFields("points", "maxPoints")
    )
  }

  override val resultType: OutputType[RegexAbstractResult] = regexAbstractResultType

}
