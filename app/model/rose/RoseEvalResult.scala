package model.rose

import model.Enums
import model.core.{CompleteResult, EvaluationResult}
import play.twirl.api.Html

class RoseEvalResult extends EvaluationResult {

  override def success: Enums.SuccessType = ???

}


case class RoseCompleteResult() extends CompleteResult[RoseEvalResult] {

  override type SolType = this.type

  override def learnerSolution: RoseCompleteResult.this.type = ???

  override def results: Seq[RoseEvalResult] = ???

  override def renderLearnerSolution: Html = ???

}