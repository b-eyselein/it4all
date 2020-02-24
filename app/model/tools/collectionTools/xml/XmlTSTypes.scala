package model.tools.collectionTools.xml

import de.uniwue.dtd.model.ElementLine
import de.uniwue.dtd.parser.DTDParseException
import model.core.matching.MatchType
import model.core.result.SuccessType
import model.tools.collectionTools.xml.XmlToolMain.ElementLineComparison
import model.tools.collectionTools.{SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.TypescriptType.{TSArray, TSObject, TSString, TypescriptNamedType}
import nl.codestar.scalatsi.{TSIType, TSNamedType, TSType}

object XmlTSTypes extends ToolTSInterfaceTypes {

  private val xmlExerciseContentTSI: TSIType[XmlExerciseContent] = {
    implicit val xsst: TSIType[SampleSolution[XmlSolution]] =
      sampleSolutionTSI("Xml", TSType.fromCaseClass[XmlSolution])

    TSType.fromCaseClass
  }

  private val xmlSolutionTSI: TSIType[XmlSolution] = TSType.fromCaseClass

  private val xmlErrorTSI: TSIType[XmlError] = {
    implicit val sts: TSType[SuccessType]                 = successTypeTS
    implicit val xmlErrorTypeTSType: TSType[XmlErrorType] = enumTsType(XmlErrorType)

    TSType.fromCaseClass
  }

  private val elementLineComparisonTSI: TSIType[ElementLineComparison] = matchingResultTSI(
    "ElementLine", {
      implicit val mtt: TSType[MatchType] = matchTypeTS
      implicit val elementLineTSI: TSNamedType[ElementLine] = TSType.interface(
        "elementName"       -> TSString,
        "elementDefinition" -> TSString,
        "attributeLists"    -> TSArray(TSObject)
      )

      TSType.fromCaseClass[ElementLineMatch]
    }
  )

  private val xmlGrammarResultTSI: TSIType[XmlGrammarResult] = {
    implicit val dtdParseExceptionTSIType: TSIType[DTDParseException] = TSType.fromCaseClass
    implicit val elmTSI: TSIType[ElementLineComparison]               = elementLineComparisonTSI

    TSType.fromCaseClass
  }

  private val xmlCompleteResultTSI: TSIType[XmlCompleteResult] = {
    implicit val sts: TSType[SuccessType]        = successTypeTS
    implicit val xdrt: TSIType[XmlError]         = xmlErrorTSI
    implicit val xgrt: TSIType[XmlGrammarResult] = xmlGrammarResultTSI

    TSType.fromCaseClass
  }

  val exported: Seq[TypescriptNamedType] = Seq(
    xmlExerciseContentTSI.get,
    xmlSolutionTSI.get,
    xmlCompleteResultTSI.get
  )

}
