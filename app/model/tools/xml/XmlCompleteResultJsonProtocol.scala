package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.CompleteResultJsonProtocol
import model.points._
import play.api.libs.json._

object XmlCompleteResultJsonProtocol extends CompleteResultJsonProtocol[XmlEvaluationResult, XmlCompleteResult] {

  // Sample solution

  private val xmlSolutionFormat: Format[XmlSolution] = Json.format[XmlSolution]

  val xmlSampleSolutionJsonFormat: Format[XmlSampleSolution] = {
    implicit val xsf: Format[XmlSolution] = xmlSolutionFormat

    Json.format[XmlSampleSolution]
  }

  // Results

  val elementLineAnalysisResultWrites: Writes[ElementLineAnalysisResult] = Json.writes[ElementLineAnalysisResult]

  // Xml Grammar correction

  private val elementLineMatchWrites: Writes[ElementLineMatch] = _.toJson

  private val dtdParseExceptionWrites: Format[DTDParseException] = Json.format[DTDParseException]

  private val xmlGrammarCompleteResultWrites: Writes[XmlGrammarCompleteResult] = {
    implicit val dpew: Writes[DTDParseException] = dtdParseExceptionWrites

    implicit val elmw: Writes[ElementLineMatch] = elementLineMatchWrites

    implicit val pw: Writes[Points] = pointsJsonWrites

    Json.writes[XmlGrammarCompleteResult]
  }

  // Xml Document correction

  private val errorTypeJsonFormat: Format[XmlErrorType] = XmlErrorType.jsonFormat

  private val xmlErrorWrites: Writes[XmlError] = Json.writes[XmlError]

  private val xmlDocumentCompleteResultWrites: Writes[XmlDocumentCompleteResult] = {
    implicit val etf: Format[XmlErrorType] = errorTypeJsonFormat

    implicit val xew: Writes[XmlError] = xmlErrorWrites

    implicit val pw: Writes[Points] = pointsJsonWrites

    Json.writes[XmlDocumentCompleteResult]
  }

  // Complete Result

  override val completeResultWrites: Writes[XmlCompleteResult] = {
    case xmlGrammarCompleteResult: XmlGrammarCompleteResult   => xmlGrammarCompleteResultWrites.writes(xmlGrammarCompleteResult)
    case xmlDocumentCompleteResult: XmlDocumentCompleteResult => xmlDocumentCompleteResultWrites.writes(xmlDocumentCompleteResult)
  }

}
