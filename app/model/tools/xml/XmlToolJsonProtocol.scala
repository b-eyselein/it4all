package model.tools.xml

import model.tools.ToolJsonProtocol
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlSolution, XmlExerciseContent, XmlExPart] {

  override val solutionFormat: Format[XmlSolution] = Json.format

  override val exerciseContentFormat: Format[XmlExerciseContent] = Json.format

  override val partTypeFormat: Format[XmlExPart] = XmlExParts.jsonFormat

}
