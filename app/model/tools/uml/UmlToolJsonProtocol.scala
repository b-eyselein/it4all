package model.tools.uml

import model.tools.ToolJsonProtocol
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExPart] {

  override val solutionFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

  override val exerciseContentFormat: Format[UmlExerciseContent] = {
    implicit val mf: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format
  }

  override val partTypeFormat: Format[UmlExPart] = UmlExParts.jsonFormat

}
