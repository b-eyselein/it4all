package model.tools.ebnf

import model.tools.ToolJsonProtocol
import model.tools.ebnf.EbnfTool.SolutionInputType
import play.api.libs.json._

object EbnfToolJsonProtocol extends ToolJsonProtocol[SolutionInputType, EbnfExerciseContent, EbnfExercisePart] {

  override val partTypeFormat: Format[EbnfExercisePart] = EbnfExercisePart.jsonFormat

  override val solutionInputFormat: Format[SolutionInputType] = Json.format

  override protected val exerciseContentFormat: OFormat[EbnfExerciseContent] = Json.format

}
