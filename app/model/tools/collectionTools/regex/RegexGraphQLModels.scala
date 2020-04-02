package model.tools.collectionTools.regex

import model.points.Points
import model.tools.collectionTools.regex.RegexToolMain.ExtractedValuesComparison
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{Interfaces, deriveEnumType, deriveObjectType}
import sangria.schema._

import scala.util.matching.Regex.{Match => RegexMatch}

object RegexGraphQLModels extends ToolGraphQLModelBasics[RegexExerciseContent, String, RegexExPart] {

  // Enum types

  private val regexCorrectionTypeType: EnumType[RegexCorrectionType] = deriveEnumType()

  private val binaryClassificationResultTypeType: EnumType[BinaryClassificationResultType] = deriveEnumType()

  // Exercise content types

  override val ExContentTypeType: ObjectType[Unit, RegexExerciseContent] = {
    implicit val rctt: EnumType[RegexCorrectionType]                          = regexCorrectionTypeType
    implicit val sampleSolutionType: ObjectType[Unit, SampleSolution[String]] = stringSampleSolutionType
    implicit val rmtdt: ObjectType[Unit, RegexMatchTestData]                  = deriveObjectType()
    implicit val retdt: ObjectType[Unit, RegexExtractionTestData]             = deriveObjectType()

    deriveObjectType()
  }

  // Solution types

  override val SolTypeInputType: InputType[String] = StringType

  // Result types

  private val regexMatchingEvaluationResultType: ObjectType[Unit, RegexMatchingSingleResult] = {
    implicit val bcrtt: EnumType[BinaryClassificationResultType] = binaryClassificationResultTypeType

    deriveObjectType()
  }

  private val regexMatchMatchType: ObjectType[Unit, RegexMatchMatch] =
    buildStringMatchTypeType("RegexMatchMatch", argDescription = (m: RegexMatch) => m.group(0))

  private val regexExtractionEvaluationResultType: ObjectType[Unit, RegexExtractionSingleResult] = {
    implicit val extractedValuesComparisonType: ObjectType[Unit, ExtractedValuesComparison] =
      matchingResultType("RegexExtractedValuesComparison", regexMatchMatchType)

    deriveObjectType()
  }

  private val regexIllegalRegexResultType: ObjectType[Unit, RegexIllegalRegexResult] = {
    implicit val pt: ObjectType[Unit, Points] = pointsType

    deriveObjectType(
      Interfaces(abstractResultTypeType)
    )
  }

  private val regexMatchingResultType: ObjectType[Unit, RegexMatchingResult] = {
    implicit val rmert: ObjectType[Unit, RegexMatchingSingleResult] = regexMatchingEvaluationResultType
    implicit val pt: ObjectType[Unit, Points]                       = pointsType

    deriveObjectType(
      Interfaces(abstractResultTypeType)
    )
  }

  private val regexExtractionResultType: ObjectType[Unit, RegexExtractionResult] = {
    implicit val reert: ObjectType[Unit, RegexExtractionSingleResult] = regexExtractionEvaluationResultType
    implicit val pt: ObjectType[Unit, Points]                         = pointsType

    deriveObjectType(
      Interfaces(abstractResultTypeType)
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
