package model.tools.uml

import model.json.JsonProtocols
import model.tools.{SampleSolution, ToolJsonProtocol, Topic}
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlClassDiagram, UmlExerciseContent, UmlExercise, UmlExPart] {

  override val solutionFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

  override val exerciseContentFormat: Format[UmlExerciseContent] = {
    implicit val mf: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format
  }

  override val exerciseFormat: Format[UmlExercise] = {
    implicit val tf: Format[Topic]                             = JsonProtocols.topicFormat
    implicit val ussf: Format[SampleSolution[UmlClassDiagram]] = sampleSolutionFormat
    implicit val ecf: Format[UmlExerciseContent]               = exerciseContentFormat

    Json.format
  }

  override val partTypeFormat: Format[UmlExPart] = UmlExParts.jsonFormat

}
