package model.tools.regex

import model.tools.regex.RegexTool.ExtractedValuesComparison
import model.tools.{SampleSolution, SemanticVersion, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, Interfaces, deriveEnumType, deriveObjectType}
import sangria.schema._

import scala.util.matching.Regex.{Match => RegexMatch}

object RegexGraphQLModels extends ToolGraphQLModelBasics[RegexExercise, String, RegexExPart] {

  // Enum types

  private val regexCorrectionTypeType: EnumType[RegexCorrectionType] = deriveEnumType()

  private val binaryClassificationResultTypeType: EnumType[BinaryClassificationResultType] = deriveEnumType()

  private val regexExTagType: EnumType[RegexExTag] = deriveEnumType()

  // Exercise content types

  override val ExerciseType: ObjectType[Unit, RegexExercise] = {
    implicit val rett: EnumType[RegexExTag]                                   = regexExTagType
    implicit val rctt: EnumType[RegexCorrectionType]                          = regexCorrectionTypeType
    implicit val svt: ObjectType[Unit, SemanticVersion]                       = semanticVersionType
    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType
    implicit val rmtdt: ObjectType[Unit, RegexMatchTestData]                  = deriveObjectType()
    implicit val retdt: ObjectType[Unit, RegexExtractionTestData]             = deriveObjectType()

    deriveObjectType(
      Interfaces(exerciseInterfaceType)
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

  private val regexIllegalRegexResultType: ObjectType[Unit, RegexIllegalRegexResult] = deriveObjectType(
    Interfaces(abstractResultTypeType),
    ExcludeFields("solutionSaved", "maxPoints")
  )

  private val regexMatchingResultType: ObjectType[Unit, RegexMatchingResult] = {
    implicit val rmert: ObjectType[Unit, RegexMatchingSingleResult] = regexMatchingEvaluationResultType

    deriveObjectType(
      Interfaces(abstractResultTypeType),
      ExcludeFields("solutionSaved", "points", "maxPoints")
    )
  }

  private val regexExtractionResultType: ObjectType[Unit, RegexExtractionResult] = {
    implicit val reert: ObjectType[Unit, RegexExtractionSingleResult] = regexExtractionEvaluationResultType

    deriveObjectType(
      Interfaces(abstractResultTypeType),
      ExcludeFields("solutionSaved", "points", "maxPoints")
    )
  }

  val abstractRegexResultType: UnionType[Unit] = UnionType(
    "AbstractRegexResult",
    types = regexIllegalRegexResultType :: regexMatchingResultType :: regexExtractionResultType :: Nil
  )

  override val AbstractResultTypeType: OutputType[Any] = abstractRegexResultType

  // Part type

  override val PartTypeInputType: EnumType[RegexExPart] = EnumType(
    "RegexExPart",
    values = RegexExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}