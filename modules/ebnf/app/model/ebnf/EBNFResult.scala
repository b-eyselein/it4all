package model.ebnf

import model.result.EvaluationResult
import model.exercise.Success
import model.result.CompleteResult

import scala.collection.JavaConverters._

case class EBNFResult(grammar: Grammar) extends EvaluationResult(EBNFResult.analyze(grammar)) {

}

case class EBNFCompleteResult(result: EBNFResult) extends CompleteResult[EBNFResult]("EBNF", List(result).asJava) {

  override def renderLearnerSolution = views.html.ebnfGrammar.render(result.grammar)

}

object EBNFResult {

  def analyze(grammar: Grammar): Success = Success.NONE

}