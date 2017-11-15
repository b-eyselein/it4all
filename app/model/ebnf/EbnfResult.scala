package model.ebnf

import model.Enums.SuccessType
import model.core.{CompleteResult, EvaluationResult}
import play.twirl.api.Html

class EbnfCompleteResult(result: EbnfResult) extends CompleteResult[EbnfResult]("Ebnf", List(result)) {

  override def renderLearnerSolution: Html = views.html.ebnf.ebnfGrammar.render(result.grammar)

}

case class EbnfResult(grammar: Grammar) extends EvaluationResult(EbnfResult.analyze(grammar))

object EbnfResult {

  def analyze(grammar: Grammar): SuccessType = SuccessType.NONE

}