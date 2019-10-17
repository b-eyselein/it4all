package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.CompleteResultJsonProtocol
import model.points._
import model.{SemanticVersion, SemanticVersionHelper}
import play.api.libs.json._

object XmlCompleteResultJsonProtocol extends CompleteResultJsonProtocol[XmlEvaluationResult, XmlCompleteResult] {

  // Sample solution

  val xmlSampleSolutionJsonFormat: Format[XmlSampleSolution] = {
    implicit val xsf: Format[XmlSolution] = Json.format[XmlSolution]

    Json.format[XmlSampleSolution]
  }

  // Exercise

  val exerciseFormat: Format[XmlExercise] = {
    implicit val svf: Format[SemanticVersion] = SemanticVersionHelper.format

    implicit val xssf: Format[XmlSampleSolution] = xmlSampleSolutionJsonFormat

    Json.format[XmlExercise]
  }

  // Results

  val elementLineAnalysisResultWrites: Writes[ElementLineAnalysisResult] = Json.writes[ElementLineAnalysisResult]

  // Xml Grammar correction

  private val xmlGrammarCompleteResultWrites: Writes[XmlGrammarCompleteResult] = {
    implicit val dpew: Writes[DTDParseException] = Json.format[DTDParseException]

    implicit val elmw: Writes[ElementLineMatch] = _.toJson

    implicit val pw: Writes[Points] = pointsJsonWrites

    Json.writes[XmlGrammarCompleteResult]
  }

  // Xml Document correction

  private val xmlDocumentCompleteResultWrites: Writes[XmlDocumentCompleteResult] = {
    implicit val etf: Format[XmlErrorType] = XmlErrorType.jsonFormat

    implicit val xew: Writes[XmlError] = Json.writes[XmlError]

    implicit val pw: Writes[Points] = pointsJsonWrites

    Json.writes[XmlDocumentCompleteResult]
  }

  // Complete Result

  override val completeResultWrites: Writes[XmlCompleteResult] = {
    case xmlGrammarCompleteResult: XmlGrammarCompleteResult   => xmlGrammarCompleteResultWrites.writes(xmlGrammarCompleteResult)
    case xmlDocumentCompleteResult: XmlDocumentCompleteResult => xmlDocumentCompleteResultWrites.writes(xmlDocumentCompleteResult)
  }

}
