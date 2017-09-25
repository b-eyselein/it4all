package model.ebnf

import scala.collection.JavaConverters.seqAsJavaListConverter

import model.exercise.Success
import model.result.{ CompleteResult, EvaluationResult }

case class EBNFCompleteResult(result: EBNFResult) extends CompleteResult[EBNFResult]("EBNF", List(result).asJava) {

  override def renderLearnerSolution = views.html.ebnfGrammar.render(result.grammar)

}

case class EBNFResult(grammar: Grammar) extends EvaluationResult(EBNFResult.analyze(grammar))

object EBNFResult {

  def analyze(grammar: Grammar): Success = Success.NONE

}