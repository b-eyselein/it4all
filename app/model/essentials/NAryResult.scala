package model.essentials

import model.essentials.NAryNumber._
import model.essentials.NumberBase._

sealed abstract class NAryResult(val targetNumber: NAryNumber, val learnerSolution: NAryNumber) {

  val solutionCorrect: Boolean = targetNumber.decimalValue == learnerSolution.decimalValue

}

case class NAryAddResult(base: NumberBase, firstSummand: NAryNumber, secondSummand: NAryNumber, learnerSol: NAryNumber)
  extends NAryResult(firstSummand + secondSummand, learnerSol)

case class NAryConvResult(startingValue: NAryNumber, startingBase: NumberBase, targetBase: NumberBase, learnerSol: NAryNumber)
  extends NAryResult(new NAryNumber(startingValue.decimalValue, targetBase), learnerSol)

case class TwoCompResult(targetNum: Int, learnerSol: NAryNumber, maybeBinaryAbs: Option[String], maybeInvertedAbs: Option[String])
  extends NAryResult(new NAryNumber(targetNum, BINARY), learnerSol) {

  def verbose: Boolean = maybeBinaryAbs.isDefined && maybeInvertedAbs.isDefined

  def binaryAbsCorrect: Boolean = maybeBinaryAbs flatMap (parseNaryNumber(_, BINARY)) exists (_.decimalValue == Math.abs(targetNumber.decimalValue))

  def invertedAbsCorrect: Boolean = (maybeInvertedAbs zip maybeBinaryAbs).headOption exists {
    case (binaryAbs, invertedAbs) => invertDigits(binaryAbs) == invertedAbs
  }

  private def invertDigits(binaryInt: String): String = binaryInt.replace("0", "a").replace("1", "0").replace("a", "1")

}
