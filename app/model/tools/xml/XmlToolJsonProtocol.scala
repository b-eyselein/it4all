package model.tools.xml

import model.tools.{SampleSolution, SemanticVersion, ToolJsonProtocol}
import play.api.libs.json._

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlExercise, XmlSolution, XmlExPart] {

  override val solutionFormat: Format[XmlSolution] = Json.format[XmlSolution]

  override val exerciseFormat: Format[XmlExercise] = {
    implicit val scf: Format[SemanticVersion]              = ToolJsonProtocol.semanticVersionFormat
    implicit val xsf: Format[XmlSolution]                  = solutionFormat
    implicit val xssf: Format[SampleSolution[XmlSolution]] = Json.format

    Json.format
  }

  override val partTypeFormat: Format[XmlExPart] = XmlExParts.jsonFormat

}
