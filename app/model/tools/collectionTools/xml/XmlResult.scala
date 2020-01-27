package model.tools.collectionTools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.{CompleteResult, SuccessType}
import model.points._


final case class XmlCompleteResult(
  successType: SuccessType,
  documentResult: Seq[XmlError],
  grammarResult: Option[XmlGrammarResult],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends CompleteResult

// Grammar result

final case class XmlGrammarResult(
  parseErrors: Seq[DTDParseException] = Seq.empty,
  results: Seq[ElementLineMatch],
)
