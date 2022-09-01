package model.tools.ebnf

import model.tools.ToolWithoutPartsJsonProtocol
import model.tools.ebnf.EbnfTool.SolInputType
import play.api.libs.json._

object EbnfToolJsonProtocol extends ToolWithoutPartsJsonProtocol[SolInputType, EbnfExerciseContent] {

  private val ebnfRuleFormat: OFormat[EbnfRule] = Json.format

  private val ebnfGrammarFormat: OFormat[EbnfGrammar] = {
    implicit val erf: OFormat[EbnfRule] = ebnfRuleFormat

    Json.format
  }

  override val solutionInputFormat: Format[EbnfGrammar] = ebnfGrammarFormat

  override val exerciseContentFormat: OFormat[EbnfExerciseContent] = {
    implicit val egf: OFormat[EbnfGrammar] = ebnfGrammarFormat

    Json.format
  }

}
