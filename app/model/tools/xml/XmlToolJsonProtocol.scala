package model.tools.xml

import model.json.JsonProtocols
import model.tools.{ReadExercisesMessage, SampleSolution, ToolJsonProtocol, Topic}
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlExercise, XmlSolution, XmlExPart] {

  override val solutionFormat: Format[XmlSolution] = Json.format

  override val exerciseFormat: Format[XmlExercise] = {
    implicit val tf: Format[Topic]                         = JsonProtocols.topicFormat
    implicit val xsf: Format[XmlSolution]                  = solutionFormat
    implicit val xssf: Format[SampleSolution[XmlSolution]] = Json.format

    Json.format
  }

  override val partTypeFormat: Format[XmlExPart] = XmlExParts.jsonFormat

  override val readExercisesMessageReads: Reads[ReadExercisesMessage[XmlExercise]] = {
    implicit val ef: Format[XmlExercise] = exerciseFormat

    Json.reads
  }

}
