package model.nary

import com.google.common.base.{Strings => GStrings}
import model.nary.NAryNumber._
import model.nary.NumberBase._

import scala.util.Try

final case class NAryNumber(decimalValue: Int = 0, base: NumberBase) {

  def +(that: NAryNumber): NAryNumber = new NAryNumber(this.decimalValue + that.decimalValue, this.base)

  def toTwoComp: String = {
    val binString = Integer.toBinaryString(decimalValue)
    padBinary(binString substring Math.max(0, binString.length - 8))
  }


  override def toString: String = {
    val result = Integer.toString(Math.abs(decimalValue), base.base)

    val paddedRes = if (base == Binary) padBinary(result) else result

    if (decimalValue < 0) '-' + paddedRes else paddedRes
  }

}

object NAryNumber {

  def parseNaryNumber(input: String, base: NumberBase): Option[NAryNumber] = {
    val decimalValue: Option[Int] = Try(Option(Integer.parseInt(input.trim().replaceAll("\\s", ""), base.base))) getOrElse None

    decimalValue map (NAryNumber(_, base))
  }

  def parseTwoComplement(input: String): Option[NAryNumber] = {
    val trimmedInput = input.trim().replaceAll("\\s", "")

    val firstBit = trimmedInput(0).asDigit
    val maybeParsedRest = Try(Option(Integer.parseInt(trimmedInput.substring(1), 2))) getOrElse None

    maybeParsedRest map (parsedRest => NAryNumber(if (firstBit == 0) parsedRest else -128 + parsedRest, Binary))
  }

  def padBinary(binary: String): String = {
    val ungrouped = binary.trim().replaceAll("\\s", "")
    val padLength = 4 * Math.max(2, Math.ceil(ungrouped.length.toDouble / 4).toInt)
    val newResult = GStrings.padStart(ungrouped, padLength, '0')
    newResult grouped 4 mkString " "
  }

}
