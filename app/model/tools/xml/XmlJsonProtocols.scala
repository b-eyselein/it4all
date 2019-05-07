package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.CompleteResultJsonProtocol
import model.points._
import play.api.libs.json._

object XmlSampleSolutionJsonProtocol {

  private implicit val xmlSolutionFormat: Format[XmlSolution] = Json.format[XmlSolution]

  val xmlSampleSolutionJsonFormat: Format[XmlSampleSolution] = Json.format[XmlSampleSolution]

}

object XmlCompleteResultJsonProtocol extends CompleteResultJsonProtocol[XmlEvaluationResult, XmlCompleteResult] {

  val elementLineAnalysisResultWrites: Writes[ElementLineAnalysisResult] = Json.writes[ElementLineAnalysisResult]

  override val completeResultWrites: Writes[XmlCompleteResult] = {
    case xmlGrammarCompleteResult: XmlGrammarCompleteResult   => xmlGrammarCompleteResultWrites.writes(xmlGrammarCompleteResult)
    case xmlDocumentCompleteResult: XmlDocumentCompleteResult => xmlDocumentCompleteResultWrites.writes(xmlDocumentCompleteResult)
  }

  private implicit val pointsWrites: Writes[Points] = pointsJsonWrites

  // Xml Grammar correction

  private implicit val elementLineMatchWrites: Writes[ElementLineMatch] = _.toJson

  private implicit val dtdParseExceptionWrites: Format[DTDParseException] = Json.format[DTDParseException]

  private val xmlGrammarCompleteResultWrites: Writes[XmlGrammarCompleteResult] = Json.writes[XmlGrammarCompleteResult]

  // Xml Document correction

  private implicit val errorTypeJsonFormat: Format[XmlErrorType] = XmlErrorType.jsonFormat

  private implicit val xmlErrorWrites: Writes[XmlError] = Json.writes[XmlError]

  private val xmlDocumentCompleteResultWrites: Writes[XmlDocumentCompleteResult] = Json.writes[XmlDocumentCompleteResult]

}
