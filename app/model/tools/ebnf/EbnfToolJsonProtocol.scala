package model.tools.ebnf

import model.tools.ToolJsonProtocol
import model.tools.ebnf.EbnfTool.SolutionInputType
import play.api.libs.json._

object EbnfToolJsonProtocol extends ToolJsonProtocol[SolutionInputType, EbnfExerciseContent, EbnfExercisePart] {

  private val ebnfRuleFormat: OFormat[EbnfRule] = Json.format

  private val ebnfGrammarFormat: OFormat[EbnfGrammar] = {
    implicit val erf: OFormat[EbnfRule] = ebnfRuleFormat

    Json.format
  }

  override val partTypeFormat: Format[EbnfExercisePart] = EbnfExercisePart.jsonFormat

  override val solutionInputFormat: Format[EbnfGrammar] = ebnfGrammarFormat

  override /* protected */ val exerciseContentFormat: OFormat[EbnfExerciseContent] = {
    implicit val egf: OFormat[EbnfGrammar] = ebnfGrammarFormat

    Json.format
  }

}
