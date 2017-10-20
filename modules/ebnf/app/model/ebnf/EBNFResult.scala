package model.ebnf

import model.result.{CompleteResult, EvaluationResult, SuccessType}
import play.twirl.api.Html

class EBNFCompleteResult(result: EBNFResult) extends CompleteResult[EBNFResult]("EBNF", List(result)) {

  override def renderLearnerSolution: Html = views.html.ebnfGrammar.render(result.grammar)

}

case class EBNFResult(grammar: Grammar) extends EvaluationResult(EBNFResult.analyze(grammar))

object EBNFResult {

  def analyze(grammar: Grammar): SuccessType = SuccessType.NONE

}