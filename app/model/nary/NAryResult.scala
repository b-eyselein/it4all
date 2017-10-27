package model.nary

sealed abstract class NAryResult(val targetNumber: NAryNumber, val learnerSolution: NAryNumber) {

  def checkSolution: Boolean = targetNumber.decimalValue == learnerSolution.decimalValue

}

case class NAryAddResult(base: NumberBase, firstSummand: NAryNumber, secondSummand: NAryNumber, learnerSol: NAryNumber)
  extends NAryResult(firstSummand + secondSummand, learnerSol)

case class NAryConvResult(startingValue: NAryNumber, startingBase: NumberBase, targetBase: NumberBase, learnerSol: NAryNumber)
  extends NAryResult(new NAryNumber(startingValue.decimalValue, targetBase), learnerSol)

case class TwoCompResult(targetNum: NAryNumber, learnerSol: NAryNumber, binaryAbs: String, invertedAbs: String)
  extends NAryResult(targetNum, learnerSol) {

  def binaryAbsCorrect: Boolean = NAryNumber.parse(binaryAbs, BINARY).decimalValue == Math.abs(targetNumber.decimalValue)

  def invertedAbsCorrect: Boolean = NAryNumber.invertDigits(binaryAbs).equals(invertedAbs)
}

object NAryResult {
  val zero = "0"

  val VALUE_NAME = "value"

  val SUMMAND_1 = "summand1"
  val SUMMAND_2 = "summand2"

  val BASE_NAME = "base"

  val STARTING_NB = "startingNB"
  val TARGET_NB   = "targetNB"

  val BINARY_ABS   = "binaryAbs"
  val INVERTED_ABS = "inverted"
}

object NAryAddResult {
  //  def parseFromForm(form: DynamicForm): NAryAddResult = {
  //    val base = NumberBase.valueOf(form.get(NAryResult.BASE_NAME))
  //
  //    val firstSummand = NAryNumber.parse(form.get(NAryResult.SUMMAND_1), base)
  //    val secondSummand = NAryNumber.parse(form.get(NAryResult.SUMMAND_2), base)
  //
  //    // Replace all spaces, reverse to compensate input from right to left!
  //    val learnerSolInNAry = form.get(FORM_VALUE).reverse.replaceAll("\\s", "")
  //    val learnerSol = NAryNumber.parse(learnerSolInNAry, base)
  //
  //    new NAryAddResult(base, firstSummand, secondSummand, learnerSol)
  //  }
}

object NAryConvResult {
  //  def parseFromForm(form: DynamicForm): NAryConvResult = {
  //    val startingNB = NumberBase.valueOf(form.get(NAryResult.STARTING_NB))
  //    val targetNB = NumberBase.valueOf(form.get(NAryResult.TARGET_NB))
  //
  //    val startingValue = NAryNumber.parse(form.get(NAryResult.VALUE_NAME), startingNB)
  //
  //    val learnerSolutionAsString = form.get(FORM_VALUE).replaceAll("\\s", "")
  //    val learnerSolution = NAryNumber.parse(learnerSolutionAsString, targetNB)
  //
  //    new NAryConvResult(startingValue, startingNB, targetNB, learnerSolution)
  //  }
}

object TwoCompResult {
  //  def parseFromForm(form: DynamicForm, isVerbose: Boolean): TwoCompResult = {
  //    val targetNumber = new NAryNumber(Integer.parseInt(form.get(NAryResult.VALUE_NAME)), BINARY)
  //    val learnerSolution = NAryNumber.parseTwoComplement(form.get(FORM_VALUE))
  //
  //    val binaryAbs = if (isVerbose) form.get(NAryResult.BINARY_ABS) else NAryResult.zero
  //    val invertedAbs = if (isVerbose) form.get(NAryResult.INVERTED_ABS) else NAryResult.zero
  //
  //    new TwoCompResult(targetNumber, learnerSolution, binaryAbs, invertedAbs)
  //  }
}