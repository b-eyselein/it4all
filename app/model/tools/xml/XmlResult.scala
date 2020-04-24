package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.{AbstractCorrectionResult, SuccessType}
import model.points._
import model.tools.xml.XmlTool.ElementLineComparison

trait XmlAbstractResult extends AbstractCorrectionResult

final case class XmlInternalErrorResult(
  solutionSaved: Boolean,
  maxPoints: Points
) extends XmlAbstractResult {

  override def points: Points = (-1).points

}

final case class XmlCompleteResult(
  successType: SuccessType,
  documentResult: Seq[XmlError],
  grammarResult: Option[XmlGrammarResult],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends XmlAbstractResult

// Grammar result

final case class XmlGrammarResult(
  parseErrors: Seq[DTDParseException] = Seq.empty,
  results: ElementLineComparison
)
