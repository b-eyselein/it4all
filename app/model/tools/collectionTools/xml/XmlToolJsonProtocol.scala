package model.tools.collectionTools.xml

import model.tools.collectionTools.{SampleSolution, ToolJsonProtocol}
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlExerciseContent, XmlSolution, XmlExPart] {

  override val solutionFormat: Format[XmlSolution] = Json.format[XmlSolution]

  override val exerciseContentFormat: Format[XmlExerciseContent] = {
    implicit val xsf: Format[XmlSolution] = solutionFormat

    implicit val xssf: Format[SampleSolution[XmlSolution]] = Json.format

    Json.format
  }

  override val partTypeFormat: Format[XmlExPart] = XmlExParts.jsonFormat

}
