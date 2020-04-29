package model.tools.xml

import model.tools.{SampleSolution, ToolJsonProtocol}
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlSolution, XmlExerciseContent, XmlExPart] {

  override val solutionFormat: Format[XmlSolution] = Json.format

  override val exerciseContentFormat: Format[XmlExerciseContent] = {
    implicit val ssf: Format[SampleSolution[XmlSolution]] = sampleSolutionFormat

    Json.format
  }

}
