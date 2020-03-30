package model.tools.collectionTools.regex

import model.points
import model.tools.collectionTools.{SampleSolution, ToolGraphQLModelBasics}
import sangria.macros.derive.{ExcludeFields, Interfaces, deriveEnumType, deriveObjectType}
import sangria.schema._

object RegexGraphQLModels
    extends ToolGraphQLModelBasics[RegexExerciseContent, String, AbstractRegexResult, RegexExPart] {

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

  /*
  override val CompResultTypeType: ObjectType[Unit, RegexCompleteResult] = {
    implicit val rctt: EnumType[RegexCorrectionType]                      = regexCorrectionTypeType
    implicit val rmert: ObjectType[Unit, RegexMatchingEvaluationResult]   = regexMatchingEvaluationResultType
    implicit val reert: ObjectType[Unit, RegexExtractionEvaluationResult] = regexExtractionEvaluationResultType
    implicit val pt: ObjectType[Unit, Points]                             = pointsType

    deriveObjectType()
  }
   */

  private val IllegalRegexResultType: ObjectType[Unit, IllegalRegexResult] = {
    implicit val pt: ObjectType[Unit, points.Points] = pointsType

    deriveObjectType(
      Interfaces[Unit, IllegalRegexResult](AbstractResultTypeType)
    )
  }

  override val AbstractResultTypeType: OutputType[AbstractRegexResult] = InterfaceType[Unit, AbstractRegexResult](
    "AbstractRegexResult",
    () =>
      fields[Unit, AbstractRegexResult](
        Field("solutionSaved", BooleanType, resolve = _.value.solutionSaved),
        Field("points", pointsType, resolve = _.value.points),
        Field("maxPoints", pointsType, resolve = _.value.maxPoints)
      )
  )

  // Part type

  override val PartTypeInputType: EnumType[RegexExPart] = EnumType(
    "RegexExPart",
    values = RegexExParts.values.map(exPart => EnumValue(exPart.entryName, value = exPart)).toList
  )

}
