package model.tools.collectionTools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.{LongText, LongTextJsonProtocol}
import model.points._
import model.tools.collectionTools.ToolJsonProtocol
import model.{SemanticVersion, SemanticVersionHelper}
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlExPart, XmlExerciseContent, XmlSolution, XmlSampleSolution, XmlUserSolution, XmlCompleteResult] {

  // Sample solution

  private val solutionFormat = Json.format[XmlSolution]

  override val sampleSolutionFormat: Format[XmlSampleSolution] = {
    implicit val xsf: Format[XmlSolution] = solutionFormat

    Json.format[XmlSampleSolution]
  }

  override val userSolutionFormat: Format[XmlUserSolution] = {
    implicit val xepf: Format[XmlExPart]   = XmlExParts.jsonFormat
    implicit val pf  : Format[Points]      = ToolJsonProtocol.pointsFormat
    implicit val xsf : Format[XmlSolution] = solutionFormat

    Json.format[XmlUserSolution]
  }

  // Exercise

  override val exerciseContentFormat: Format[XmlExerciseContent] = {
    implicit val ltf : Format[LongText]          = LongTextJsonProtocol.format
    implicit val xssf: Format[XmlSampleSolution] = sampleSolutionFormat

    Json.format[XmlExerciseContent]
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
