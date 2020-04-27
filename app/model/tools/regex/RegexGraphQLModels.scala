package model.tools.regex

import model.GraphQLArguments
import model.tools.regex.RegexTool.ExtractedValuesComparison
import model.tools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive._
import sangria.schema._

import scala.util.matching.Regex.{Match => RegexMatch}

object RegexGraphQLModels
    extends ToolGraphQLModelBasics[String, RegexExerciseContent, RegexExPart, RegexAbstractResult]
    with GraphQLArguments {

  override val partEnumType: EnumType[RegexExPart] = EnumType(
    "RegexExPart",
    values = RegexExPart.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

  override val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] =
    buildSampleSolutionType("Regex", StringType)

  // Enum types

  private val regexCorrectionTypeType: EnumType[RegexCorrectionType] = deriveEnumType()

  private val binaryClassificationResultTypeType: EnumType[BinaryClassificationResultType] = deriveEnumType()

  // Exercise content types

  override val exerciseContentType: ObjectType[Unit, RegexExerciseContent] = {
    implicit val rctt: EnumType[RegexCorrectionType]              = regexCorrectionTypeType
    implicit val rmtdt: ObjectType[Unit, RegexMatchTestData]      = deriveObjectType()
    implicit val retdt: ObjectType[Unit, RegexExtractionTestData] = deriveObjectType()
    implicit val sst: ObjectType[Unit, SampleSolution[String]]    = sampleSolutionType

    deriveObjectType(
      AddFields(
        Field(
          "part",
          OptionType(partEnumType),
          arguments = partIdArgument :: Nil,
          resolve = context => RegexExPart.values.find(_.id == context.arg(partIdArgument))
        )
      )
    )
  }

  // Solution types

  override val SolTypeInputType: InputType[String] = StringType

  // Result types

  private val regexMatchingEvaluationResultType: ObjectType[Unit, RegexMatchingSingleResult] = {
    implicit val bcrtt: EnumType[BinaryClassificationResultType] = binaryClassificationResultTypeType

    deriveObjectType()
  }

  private val regexMatchMatchType: ObjectType[Unit, RegexMatchMatch] =
    buildStringMatchTypeType[RegexMatch, RegexMatchMatch]("RegexMatchMatch")

  private val regexExtractionEvaluationResultType: ObjectType[Unit, RegexExtractionSingleResult] = {
    implicit val extractedValuesComparisonType: ObjectType[Unit, ExtractedValuesComparison] =
      matchingResultType("RegexExtractedValuesComparison", regexMatchMatchType)

    deriveObjectType()
  }

  // Abstract result

  private val regexAbstractResultType: InterfaceType[Unit, RegexAbstractResult] = InterfaceType(
    "RegexAbstractResult",
    fields[Unit, RegexAbstractResult](
      Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
      Field("points", FloatType, resolve = _.value.points.asDouble),
      Field("maxPoints", FloatType, resolve = _.value.maxPoints.asDouble)
    ),
    interfaces[Unit, RegexAbstractResult](abstractResultInterfaceType)
  ).withPossibleTypes(() =>
    List(regexInternalErrorResultType, regexIllegalRegexResultType, regexMatchingResultType, regexExtractionResultType)
  )

  private val regexInternalErrorResultType: ObjectType[Unit, RegexInternalErrorResult] = deriveObjectType(
    Interfaces(regexAbstractResultType),
    ExcludeFields("solutionSaved", "maxPoints")
  )

  private val regexIllegalRegexResultType: ObjectType[Unit, RegexIllegalRegexResult] = deriveObjectType(
    Interfaces(regexAbstractResultType),
    ExcludeFields("solutionSaved", "maxPoints")
  )

  private val regexMatchingResultType: ObjectType[Unit, RegexMatchingResult] = {
    implicit val rmert: ObjectType[Unit, RegexMatchingSingleResult] = regexMatchingEvaluationResultType

    deriveObjectType(
      Interfaces(regexAbstractResultType),
      ExcludeFields("solutionSaved", "points", "maxPoints")
    )
  }

  private val regexExtractionResultType: ObjectType[Unit, RegexExtractionResult] = {
    implicit val reert: ObjectType[Unit, RegexExtractionSingleResult] = regexExtractionEvaluationResultType

    deriveObjectType(
      Interfaces(regexAbstractResultType),
      ExcludeFields("solutionSaved", "points", "maxPoints")
    )
  }

  override val toolAbstractResultTypeInterfaceType: InterfaceType[Unit, RegexAbstractResult] = regexAbstractResultType

}
