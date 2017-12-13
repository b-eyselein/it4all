package model.ebnf

import model.Enums.SuccessType
import model.core.{CompleteResult, EvaluationResult}
import play.twirl.api.Html

class EbnfCompleteResult(result: EbnfResult) extends CompleteResult[EbnfResult] {

  override type SolType = Grammar

  override def learnerSolution: Grammar = result.grammar

  override def results: Seq[EbnfResult] = Seq(result)

  override def renderLearnerSolution: Html = views.html.ebnf.ebnfGrammar.render(result.grammar)

}

case class EbnfResult(grammar: Grammar) extends EvaluationResult {

  override val success = SuccessType.NONE // TODO: implement!

}
