package model.tools.collectionTools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.{CompleteResult, SuccessType}
import model.points._


final case class XmlCompleteResult(
  successType: SuccessType,
  result: Either[XmlDocumentResult, XmlGrammarResult],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends CompleteResult[XmlEvaluationResult] {

  override def results: Seq[XmlEvaluationResult] = result match {
    case Left(documentResult) => documentResult.results
    case Right(grammarResult) => grammarResult.results
  }

}

// Document result

final case class XmlDocumentResult(results: Seq[XmlError])

// Grammar result

final case class XmlGrammarResult(
  parseErrors: Seq[DTDParseException] = Seq.empty,
  results: Seq[ElementLineMatch],
)
