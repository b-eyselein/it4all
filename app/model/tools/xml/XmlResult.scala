package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.matching.MatchType
import model.points._
import model.result.AbstractCorrectionResult
import model.tools.xml.XmlTool.ElementLineComparison

final case class XmlResult(
  points: Points,
  maxPoints: Points,
  documentResult: Option[XmlDocumentResult] = None,
  grammarResult: Option[XmlGrammarResult] = None
) extends AbstractCorrectionResult {

  override def isCompletelyCorrect: Boolean = {
    val documentResultCorrect = documentResult.forall(_.errors.isEmpty)

    val grammarResultCorrect = grammarResult.forall { gr =>
      gr.parseErrors.isEmpty && gr.results.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH)
    }

    documentResultCorrect && grammarResultCorrect
  }

}

// Single result

final case class XmlDocumentResult(
  errors: Seq[XmlError]
)

final case class XmlGrammarResult(
  parseErrors: Seq[DTDParseException] = Seq.empty,
  results: ElementLineComparison
)
