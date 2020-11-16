package model.tools.uml

import model.tools.ToolJsonProtocol
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExPart] {

  override val partTypeFormat: Format[UmlExPart] = UmlExPart.jsonFormat

  override val solutionFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

  override val exerciseContentFormat: OFormat[UmlExerciseContent] = {
    implicit val mf: Format[Map[String, String]] = keyValueObjectMapFormat
    implicit val ssf: Format[UmlClassDiagram]    = solutionFormat

    Json.format
  }

}
