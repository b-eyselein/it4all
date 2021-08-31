package model.tools.xml

import model.tools.ToolJsonProtocol
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlSolution, XmlExerciseContent, XmlExPart] {

  private val xmlSolutionFormat = Json.format[XmlSolution]

  override val partTypeFormat: Format[XmlExPart] = XmlExPart.jsonFormat

  override val solutionInputFormat: Format[XmlSolution] = xmlSolutionFormat

  override val exerciseContentFormat: OFormat[XmlExerciseContent] = {
    implicit val ssf: Format[XmlSolution] = xmlSolutionFormat

    Json.format
  }

}
