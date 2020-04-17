package model.tools.xml

import model.json.JsonProtocols
import model.tools.{SampleSolution, ToolJsonProtocol, Topic}
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlSolution, XmlExerciseContent, XmlExercise, XmlExPart] {

  override val solutionFormat: Format[XmlSolution] = Json.format

  override val exerciseContentFormat: Format[XmlExerciseContent] = Json.format

  override val exerciseFormat: Format[XmlExercise] = {
    implicit val tf: Format[Topic]                        = JsonProtocols.topicFormat
    implicit val ssf: Format[SampleSolution[XmlSolution]] = sampleSolutionFormat
    implicit val ecf: Format[XmlExerciseContent]          = exerciseContentFormat

    Json.format
  }

  override val partTypeFormat: Format[XmlExPart] = XmlExParts.jsonFormat

}
