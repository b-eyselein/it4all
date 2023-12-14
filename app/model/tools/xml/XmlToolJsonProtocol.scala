package model.tools.xml

import model.tools.ToolJsonProtocol
import play.api.libs.json._

import scala.annotation.unused

object XmlToolJsonProtocol extends ToolJsonProtocol[XmlSolution, XmlExerciseContent] {

  private val xmlSolutionFormat = Json.format[XmlSolution]

  override val solutionInputFormat: Format[XmlSolution] = xmlSolutionFormat

  override val exerciseContentFormat: OFormat[XmlExerciseContent] = {
    @unused implicit val ssf: Format[XmlSolution] = xmlSolutionFormat

    Json.format
  }

}
