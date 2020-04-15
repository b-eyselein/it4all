package model.tools.uml

import model.tools.{SampleSolution, SemanticVersion, ToolJsonProtocol}
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlExercise, UmlClassDiagram, UmlExPart] {

  override val solutionFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

  private val sampleSolutionFormat: Format[SampleSolution[UmlClassDiagram]] = {
    implicit val ucdf: Format[UmlClassDiagram] = solutionFormat

    Json.format
  }

  override val exerciseFormat: Format[UmlExercise] = {
    implicit val svf: Format[SemanticVersion]                  = ToolJsonProtocol.semanticVersionFormat
    implicit val ussf: Format[SampleSolution[UmlClassDiagram]] = sampleSolutionFormat

    implicit val mf: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format
  }

  override val partTypeFormat: Format[UmlExPart] = UmlExParts.jsonFormat

}
