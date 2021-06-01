package model.tools.ebnf

import model.points._
import model.result.AbstractCorrectionResult

final case class EbnfResult(
  x: String
) extends AbstractCorrectionResult {

  override def points: Points = ???

  override def maxPoints: Points = ???

  override def isCompletelyCorrect: Boolean = ???

}
