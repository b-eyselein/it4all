package model.tools.uml

import model.tools.{SampleSolution, ToolJsonProtocol}
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlExerciseContent, UmlClassDiagram, UmlExPart] {

  override val solutionFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

  override val exerciseContentFormat: Format[UmlExerciseContent] = {
    implicit val ussf: Format[SampleSolution[UmlClassDiagram]] = {
      implicit val ucdf: Format[UmlClassDiagram] = solutionFormat

      Json.format[SampleSolution[UmlClassDiagram]]
    }

    implicit val mf: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format[UmlExerciseContent]
  }

  override val partTypeFormat: Format[UmlExPart] = UmlExParts.jsonFormat

}
