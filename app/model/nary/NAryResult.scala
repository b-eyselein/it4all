package model.nary

import play.api.data.Form
import play.api.data.Forms._

import scala.util.Random

sealed abstract class NAryResult(val targetNumber: NAryNumber, val learnerSolution: NAryNumber) {

  def checkSolution: Boolean = targetNumber.decimalValue == learnerSolution.decimalValue

}

// Mappings for request

case class AdditionSolution(sum1: String, sum2: String, solutionNary: String, base: String)

case class ConversionSolution(startingValueNary: String, solutionNary: String, startingNB: String, targetNB: String)

case class TwoCompSolution(startingValueDec: Int, binaryAbs: Option[String], inverted: Option[String], solutionBinary: String)

// Result classes

case class NAryAddResult(base: NumberBase, firstSummand: NAryNumber, secondSummand: NAryNumber, learnerSol: NAryNumber)
  extends NAryResult(firstSummand + secondSummand, learnerSol)

case class NAryConvResult(startingValue: NAryNumber, startingBase: NumberBase, targetBase: NumberBase, learnerSol: NAryNumber)
  extends NAryResult(new NAryNumber(startingValue.decimalValue, targetBase), learnerSol)

case class TwoCompResult(targetNum: Int, learnerSol: NAryNumber, binaryAbs: String, invertedAbs: String)
  extends NAryResult(new NAryNumber(targetNum, NumberBase.BINARY), learnerSol) {

  def binaryAbsCorrect: Boolean = NAryNumber.parse(binaryAbs, NumberBase.BINARY).decimalValue == Math.abs(targetNumber.decimalValue)

  def invertedAbsCorrect: Boolean = NAryNumber.invertDigits(binaryAbs).equals(invertedAbs)
}

object NAryResult {

  val generator = new Random

  val zero = "0"

  val VALUE_NAME  = "value"
  val LEARNER_SOL = "learnerSolution"

  val SUMMAND_1 = "summand1"
  val SUMMAND_2 = "summand2"

  val BASE_NAME = "base"

  val STARTING_NB = "startingNB"
  val TARGET_NB   = "targetNB"

  val BINARY_ABS   = "binaryAbs"
  val INVERTED_ABS = "inverted"

  def addResultFromFormValue(form: AdditionSolution): NAryAddResult = {
    val base: NumberBase = NumberBase.valueOf(form.base)
    NAryAddResult(base, NAryNumber.parse(form.sum1, base), NAryNumber.parse(form.sum2, base), NAryNumber.parse(form.solutionNary.reverse, base))
  }

  def convResultFromFormValue(form: ConversionSolution): NAryConvResult = {
    val startingBase = NumberBase.valueOf(form.startingNB)
    val targetBase = NumberBase.valueOf(form.targetNB)

    val startingValue: NAryNumber = NAryNumber.parse(form.startingValueNary, startingBase)
    val learnerSol: NAryNumber = NAryNumber.parse(form.solutionNary, targetBase)

    NAryConvResult(startingValue, startingBase, targetBase, learnerSol)
  }

  def twoCompResultFromFormValue(form: TwoCompSolution): TwoCompResult =
  // FIXME: optionals!
    TwoCompResult(form.startingValueDec, NAryNumber.parse(form.solutionBinary, NumberBase.BINARY), form.binaryAbs.getOrElse(""), form.inverted.getOrElse(""))

  val additionSolution = Form(mapping(
    SUMMAND_1 -> nonEmptyText,
    SUMMAND_2 -> nonEmptyText,
    LEARNER_SOL -> nonEmptyText,
    BASE_NAME -> nonEmptyText
  )(AdditionSolution.apply)(AdditionSolution.unapply))

  val conversionSolution = Form(mapping(
    VALUE_NAME -> nonEmptyText,
    LEARNER_SOL -> nonEmptyText,
    STARTING_NB -> nonEmptyText,
    TARGET_NB -> nonEmptyText
  )(ConversionSolution.apply)(ConversionSolution.unapply))

  val twoComplementSolution = Form(mapping(
    VALUE_NAME -> number,
    BINARY_ABS -> optional(nonEmptyText),
    INVERTED_ABS -> optional(nonEmptyText),
    LEARNER_SOL -> nonEmptyText
  )(TwoCompSolution.apply)(TwoCompSolution.unapply))

}