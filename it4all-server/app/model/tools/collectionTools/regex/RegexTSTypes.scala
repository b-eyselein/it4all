package model.tools.collectionTools.regex

import model.tools.collectionTools.{SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.{TSIType, TSType}
import nl.codestar.scalatsi.TypescriptType.{TSInterface, TSString}

trait RegexTSTypes extends ToolTSInterfaceTypes {

  val regexExerciseContentTSI: TSIType[RegexExerciseContent] = {
    implicit val ssst : TSIType[SampleSolution[String]]  = sampleSolutionTSI[String](TSType(TSString))
    implicit val rmtdt: TSIType[RegexMatchTestData]      = TSType.fromCaseClass[RegexMatchTestData]
    implicit val retdt: TSIType[RegexExtractionTestData] = TSType.fromCaseClass[RegexExtractionTestData]
    implicit val rctt : TSType[RegexCorrectionType]      = enumTsType(RegexCorrectionTypes)

    TSType.fromCaseClass[RegexExerciseContent]
  }

}
