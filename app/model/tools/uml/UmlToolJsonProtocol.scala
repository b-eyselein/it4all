package model.tools.uml

import model.json.JsonProtocols
import model.tools.{ReadExercisesMessage, SampleSolution, ToolJsonProtocol, Topic}
import play.api.libs.json._

object UmlToolJsonProtocol extends ToolJsonProtocol[UmlExercise, UmlClassDiagram, UmlExPart] {

  override val solutionFormat: Format[UmlClassDiagram] = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat

  private val sampleSolutionFormat: Format[SampleSolution[UmlClassDiagram]] = {
    implicit val ucdf: Format[UmlClassDiagram] = solutionFormat

    Json.format
  }

  override val exerciseFormat: Format[UmlExercise] = {
    implicit val tf: Format[Topic]                             = JsonProtocols.topicFormat
    implicit val ussf: Format[SampleSolution[UmlClassDiagram]] = sampleSolutionFormat
    implicit val mf: Format[Map[String, String]]               = keyValueObjectMapFormat

    Json.format
  }

  override val partTypeFormat: Format[UmlExPart] = UmlExParts.jsonFormat

  override val readExercisesMessageReads: Reads[ReadExercisesMessage[UmlExercise]] = {
    implicit val ef: Format[UmlExercise] = exerciseFormat

    Json.reads
  }
}
