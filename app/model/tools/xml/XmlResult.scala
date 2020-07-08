package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.matching.MatchType
import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult, SuccessType}
import model.tools.xml.XmlTool.ElementLineComparison

trait XmlAbstractResult extends AbstractCorrectionResult[XmlAbstractResult]

final case class XmlInternalErrorResult(
  msg: String,
  maxPoints: Points,
  solutionSaved: Boolean = false
) extends XmlAbstractResult
    with InternalErrorResult[XmlAbstractResult] {

  override def updateSolutionSaved(solutionSaved: Boolean): XmlAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}

final case class XmlResult(
  successType: SuccessType,
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean = false,
  documentResult: Option[XmlDocumentResult] = None,
  grammarResult: Option[XmlGrammarResult] = None
) extends XmlAbstractResult {

  override def isCompletelyCorrect: Boolean = {
    val documentResultCorrect = documentResult.forall(_.errors.isEmpty)

    val grammarResultCorrect = grammarResult.forall { gr =>
      gr.parseErrors.isEmpty && gr.results.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH)
    }

    documentResultCorrect && grammarResultCorrect
  }

  override def updateSolutionSaved(solutionSaved: Boolean): XmlAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}

// Single result

final case class XmlDocumentResult(
  errors: Seq[XmlError]
)

final case class XmlGrammarResult(
  parseErrors: Seq[DTDParseException] = Seq.empty,
  results: ElementLineComparison
)
