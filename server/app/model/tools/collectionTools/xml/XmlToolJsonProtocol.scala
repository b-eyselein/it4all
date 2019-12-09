package model.tools.collectionTools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.points._
import model.tools.collectionTools.{SampleSolution, ToolJsonProtocol}
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlExerciseContent, XmlSolution, XmlCompleteResult] {

  override val solutionFormat: Format[XmlSolution] = Json.format[XmlSolution]

  override val exerciseContentFormat: Format[XmlExerciseContent] = {
    implicit val xssf: Format[SampleSolution[XmlSolution]] = {
      implicit val xsf: Format[XmlSolution] = solutionFormat

      Json.format[SampleSolution[XmlSolution]]
    }

    Json.format[XmlExerciseContent]
  }

  // Results

  val elementLineAnalysisResultWrites: Writes[ElementLineAnalysisResult] = Json.writes[ElementLineAnalysisResult]

  // Xml Grammar correction

  private val xmlGrammarCompleteResultWrites: Writes[XmlGrammarCompleteResult] = {
    implicit val dpew: Writes[DTDParseException] = Json.format[DTDParseException]

    implicit val elmw: Writes[ElementLineMatch] = _.toJson

    implicit val pw: Writes[Points] = ToolJsonProtocol.pointsFormat

    Json.writes[XmlGrammarCompleteResult]
  }

  // Xml Document correction

  private val xmlDocumentCompleteResultWrites: Writes[XmlDocumentCompleteResult] = {
    implicit val etf: Format[XmlErrorType] = XmlErrorType.jsonFormat

    implicit val xew: Writes[XmlError] = Json.writes[XmlError]

    implicit val pw: Writes[Points] = ToolJsonProtocol.pointsFormat

    Json.writes[XmlDocumentCompleteResult]
  }

  // Complete Result

  override val completeResultWrites: Writes[XmlCompleteResult] = {
    case xmlGrammarCompleteResult: XmlGrammarCompleteResult   => xmlGrammarCompleteResultWrites.writes(xmlGrammarCompleteResult)
    case xmlDocumentCompleteResult: XmlDocumentCompleteResult => xmlDocumentCompleteResultWrites.writes(xmlDocumentCompleteResult)
  }

}
