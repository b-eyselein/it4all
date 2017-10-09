package model.ebnf

import scala.collection.JavaConverters.seqAsJavaListConverter

import model.result.{ CompleteResult, EvaluationResult, SuccessType }

class EBNFCompleteResult(result: EBNFResult) extends CompleteResult[EBNFResult]("EBNF", List(result).asJava) {

  override def renderLearnerSolution = views.html.ebnfGrammar.render(result.grammar)

}

case class EBNFResult(grammar: Grammar) extends EvaluationResult(EBNFResult.analyze(grammar))

object EBNFResult {

  def analyze(grammar: Grammar): SuccessType = SuccessType.NONE

}