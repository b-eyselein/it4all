package model.tools.collectionTools.regex

import model.points.Points
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, deriveEnumType, deriveObjectType}
import sangria.schema.{EnumType, EnumValue, InputType, ObjectType, StringType}

object RegexGraphQLModels
    extends ToolGraphQLModelBasics[RegexExerciseContent, String, RegexCompleteResult, RegexExPart] {

  private val regexCorrectionTypeType: EnumType[RegexCorrectionType] = deriveEnumType()

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

  private val binaryClassificationResultTypeType: EnumType[BinaryClassificationResultType] = deriveEnumType()

  private val regexMatchingEvaluationResultType: ObjectType[Unit, RegexMatchingEvaluationResult] = {
    implicit val bcrtt: EnumType[BinaryClassificationResultType] = binaryClassificationResultTypeType

    deriveObjectType()
  }

  private val regexExtractionEvaluationResultType: ObjectType[Unit, RegexExtractionEvaluationResult] = {

    deriveObjectType(
      // FIXME: do not exclude fields anymore!
      ExcludeFields(
        "extractionMatchingResult"
      )
    )
  }

  override val CompResultTypeType: ObjectType[Unit, RegexCompleteResult] = {
    implicit val rctt: EnumType[RegexCorrectionType]                      = regexCorrectionTypeType
    implicit val rmert: ObjectType[Unit, RegexMatchingEvaluationResult]   = regexMatchingEvaluationResultType
    implicit val reert: ObjectType[Unit, RegexExtractionEvaluationResult] = regexExtractionEvaluationResultType
    implicit val pt: ObjectType[Unit, Points]                             = pointsType

    deriveObjectType()
  }

  // Part type

  override val PartTypeInputType: EnumType[RegexExPart] = EnumType(
    "RegexExPart",
    values = RegexExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
