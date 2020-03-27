package model.tools.collectionTools.regex

import model.core.matching.MatchType
import model.tools.collectionTools.ToolTSInterfaceTypes
import model.tools.collectionTools.regex.RegexToolMain.ExtractedValuesComparison
import nl.codestar.scalatsi.TypescriptType.{TSNumber, TSString, TypescriptNamedType}
import nl.codestar.scalatsi.{TSIType, TSNamedType, TSType}

import scala.util.matching.Regex.{Match => RegexMatch}

object RegexTSTypes extends ToolTSInterfaceTypes {

  private val regexCorrectionTypeTsType: TSType[RegexCorrectionType] = enumTsType(RegexCorrectionTypes)

  private val regexMatchingEvaluationResultTSI: TSIType[RegexMatchingEvaluationResult] = {
    implicit val bcrt: TSType[BinaryClassificationResultType] = enumTsType(BinaryClassificationResultTypes)

    TSType.fromCaseClass
  }

  private val regexExtractionEvaluationResultTSI: TSIType[RegexExtractionEvaluationResult] = {
    implicit val todo: TSNamedType[ExtractedValuesComparison] = matchingResultTSI(
      "RegexExtraction", {
        implicit val mtt: TSType[MatchType] = enumTsType(MatchType)
        implicit val rmt: TSType[RegexMatch] = TSType.interface(
          "start"   -> TSNumber,
          "end"     -> TSNumber,
          "content" -> TSString
        )

        TSType.fromCaseClass[RegexMatchMatch]
      }
    )

    TSType.fromCaseClass
  }

  private val regexCompleteResultTSI: TSIType[RegexCompleteResult] = {
    implicit val rctt: TSType[RegexCorrectionType]               = regexCorrectionTypeTsType
    implicit val reert: TSIType[RegexExtractionEvaluationResult] = regexExtractionEvaluationResultTSI
    implicit val rmrt: TSIType[RegexMatchingEvaluationResult]    = regexMatchingEvaluationResultTSI

    TSType.fromCaseClass
  }

  val exported: Seq[TypescriptNamedType] = Seq(
    regexCompleteResultTSI.get
  )

}
