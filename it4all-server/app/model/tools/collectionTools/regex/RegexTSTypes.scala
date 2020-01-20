package model.tools.collectionTools.regex

import model.core.matching.MatchType
import model.tools.collectionTools.regex.RegexToolMain.ExtractedValuesComparison
import model.tools.collectionTools.{SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.{TSIType, TSNamedType, TSType}
import nl.codestar.scalatsi.TypescriptType.{TSNumber, TSString, TypescriptNamedType}

import scala.util.matching.Regex.{Match => RegexMatch}

object RegexTSTypes extends ToolTSInterfaceTypes {

  import nl.codestar.scalatsi.dsl._

  private val regexCorrectionTypeTsType: TSType[RegexCorrectionType] = enumTsType(RegexCorrectionTypes)

  private val regexExerciseContentTSI: TSIType[RegexExerciseContent] = {
    implicit val ssst : TSIType[SampleSolution[String]]  = sampleSolutionTSI[String](TSType(TSString))
    implicit val rmtdt: TSIType[RegexMatchTestData]      = TSType.fromCaseClass
    implicit val retdt: TSIType[RegexExtractionTestData] = TSType.fromCaseClass
    implicit val rctt : TSType[RegexCorrectionType]      = regexCorrectionTypeTsType

    TSType.fromCaseClass
  }

  private val regexMatchingEvaluationResultTSI: TSIType[RegexMatchingEvaluationResult] = {
    implicit val bcrt: TSType[BinaryClassificationResultType] = enumTsType(BinaryClassificationResultTypes)

    TSType.fromCaseClass
  }

  private val regexExtractionEvaluationResultTSI: TSIType[RegexExtractionEvaluationResult] = {
    implicit val todo: TSNamedType[ExtractedValuesComparison] = matchingResultTSI("RegexExtraction", {
      implicit val mtt: TSType[MatchType]  = enumTsType(MatchType)
      implicit val rmt: TSType[RegexMatch] = TSType.interface(
        "start" -> TSNumber,
        "end" -> TSNumber,
        "content" -> TSString
      )

      TSType.fromCaseClass[RegexMatchMatch]
    })

    TSType.fromCaseClass
  }

  private val regexCompleteResultTSI: TSIType[RegexCompleteResult] = {
    implicit val rctt : TSType[RegexCorrectionType]              = regexCorrectionTypeTsType
    implicit val reert: TSIType[RegexExtractionEvaluationResult] = regexExtractionEvaluationResultTSI
    implicit val rmrt : TSIType[RegexMatchingEvaluationResult]   = regexMatchingEvaluationResultTSI

    TSType.fromCaseClass
  }

  val exported: Seq[TypescriptNamedType] = Seq(
    regexExerciseContentTSI.get,
    regexCompleteResultTSI.get
  )

}
