package model.tools.collectionTools.xml

import de.uniwue.dtd.model.{AttributeList, ElementDefinition, ElementLine}
import de.uniwue.dtd.parser.DTDParseException
import model.points.Points
import model.tools.collectionTools.xml.XmlToolMain.ElementLineComparison
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

  private val elementLineMatchWrites: Writes[ElementLineMatch] = {
    implicit val elw  : Writes[ElementLine]               = {
      implicit val edw: Writes[ElementDefinition] = l => JsString(l.asString)
      implicit val alw: Writes[AttributeList]     = l => JsString(l.asString)

      Json.writes
    }
    implicit val elarw: Writes[ElementLineAnalysisResult] = Json.writes

    Json.writes
  }

  private val xmlGrammarResultWrites: Writes[XmlGrammarResult] = {
    implicit val dpew: Writes[DTDParseException] = Json.writes

    implicit val elementLineComparisonWrites: Writes[ElementLineComparison] =
      matchingResultWrites(elementLineMatchWrites)


    Json.writes
  }

  // Xml Document correction

  private val xmlErrorWrites: Writes[XmlError] = {
    implicit val etf: Format[XmlErrorType] = XmlErrorType.jsonFormat

    Json.writes
  }

  // Complete Result

  override val completeResultWrites: Writes[XmlCompleteResult] = {
    implicit val xew : Writes[XmlError]         = xmlErrorWrites
    implicit val xgrw: Writes[XmlGrammarResult] = xmlGrammarResultWrites
    implicit val pw  : Writes[Points]           = ToolJsonProtocol.pointsFormat

    Json.writes
  }

}
