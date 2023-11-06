package model.tools.xml

import model.tools.ToolWithPartsJsonProtocol
import play.api.libs.json._

import scala.annotation.unused

object XmlToolJsonProtocol extends ToolWithPartsJsonProtocol[XmlSolution, XmlExerciseContent, XmlExPart] {

  override val partTypeFormat: Format[XmlExPart] = XmlExPart.jsonFormat

  private val xmlSolutionFormat = Json.format[XmlSolution]

  override val solutionInputFormat: Format[XmlSolution] = xmlSolutionFormat

  override val exerciseContentFormat: OFormat[XmlExerciseContent] = {
    @unused implicit val ssf: Format[XmlSolution] = xmlSolutionFormat

    Json.format
  }

}
