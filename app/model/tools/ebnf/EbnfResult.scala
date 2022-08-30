package model.tools.ebnf

import model.AbstractCorrectionResult
import model.points._

final case class EbnfResult(
  x: String
) extends AbstractCorrectionResult {

  override def points: Points = ???

  override def maxPoints: Points = ???

  override def isCompletelyCorrect: Boolean = ???

}
