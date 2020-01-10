package model.tools.collectionTools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.points.Points
import model.tools.collectionTools.{SampleSolution, ToolJsonProtocol}
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlExerciseContent, XmlSolution, XmlCompleteResult] {

  override val solutionFormat: Format[XmlSolution] = Json.format[XmlSolution]

  override val exerciseContentFormat: Format[XmlExerciseContent] = {
    implicit val xsf: Format[XmlSolution] = solutionFormat

    implicit val xssf: Format[SampleSolution[XmlSolution]] = Json.format

    Json.format
  }

  // Results

  val elementLineAnalysisResultWrites: Writes[ElementLineAnalysisResult] = Json.writes

  // Xml Grammar correction

  private val xmlGrammarResultWrites: Writes[XmlGrammarResult] = {
    implicit val dpew: Writes[DTDParseException] = Json.writes
    implicit val elmw: Writes[ElementLineMatch]  = _.toJson

    Json.writes
  }

  // Xml Document correction

  private val xmlDocumentResultWrites: Writes[XmlDocumentResult] = {
    implicit val etf: Format[XmlErrorType] = XmlErrorType.jsonFormat
    implicit val xew: Writes[XmlError]     = Json.writes

    Json.writes
  }

  // Complete Result

  override val completeResultWrites: Writes[XmlCompleteResult] = {
    implicit val xw: Writes[Either[XmlDocumentResult, XmlGrammarResult]] = {
      case Left(value)  => xmlDocumentResultWrites.writes(value)
      case Right(value) => xmlGrammarResultWrites.writes(value)
    }

    implicit val pw: Writes[Points] = ToolJsonProtocol.pointsFormat

    Json.writes
  }

}
