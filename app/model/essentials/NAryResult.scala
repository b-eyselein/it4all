package model.essentials

import model.essentials.EssentialsConsts._
import model.essentials.NumberBase._
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
  extends NAryResult(new NAryNumber(targetNum, BINARY), learnerSol) {

  def binaryAbsCorrect: Boolean = NAryNumber.parse(binaryAbs, BINARY).decimalValue == Math.abs(targetNumber.decimalValue)

  def invertedAbsCorrect: Boolean = NAryNumber.invertDigits(binaryAbs).equals(invertedAbs)
}

object NAryResult {

  val generator = new Random

  val zero = "0"


  def addResultFromFormValue(form: AdditionSolution): NAryAddResult = {
    val base: NumberBase = valueOf(form.base)
    NAryAddResult(base, NAryNumber.parse(form.sum1, base), NAryNumber.parse(form.sum2, base), NAryNumber.parse(form.solutionNary.reverse, base))
  }

  def convResultFromFormValue(form: ConversionSolution): NAryConvResult = {
    val startingBase = valueOf(form.startingNB)
    val targetBase = valueOf(form.targetNB)

    val startingValue: NAryNumber = NAryNumber.parse(form.startingValueNary, startingBase)
    val learnerSol: NAryNumber = NAryNumber.parse(form.solutionNary, targetBase)

    NAryConvResult(startingValue, startingBase, targetBase, learnerSol)
  }

  def twoCompResultFromFormValue(form: TwoCompSolution): TwoCompResult =
  // FIXME: optionals!
    TwoCompResult(form.startingValueDec, NAryNumber.parse(form.solutionBinary, BINARY), form.binaryAbs.getOrElse(""), form.inverted.getOrElse(""))

  val additionSolution = Form(mapping(
    FirstSummand -> nonEmptyText,
    SecondSummand -> nonEmptyText,
    LearnerSol -> nonEmptyText,
    BaseName -> nonEmptyText
  )(AdditionSolution.apply)(AdditionSolution.unapply))

  val conversionSolution = Form(mapping(
    VALUE_NAME -> nonEmptyText,
    LearnerSol -> nonEmptyText,
    StartingNumBase -> nonEmptyText,
    TargetNumBase -> nonEmptyText
  )(ConversionSolution.apply)(ConversionSolution.unapply))

  val twoComplementSolution = Form(mapping(
    VALUE_NAME -> number,
    BinaryAbs -> optional(nonEmptyText),
    InvertedAbs -> optional(nonEmptyText),
    LearnerSol -> nonEmptyText
  )(TwoCompSolution.apply)(TwoCompSolution.unapply))

}