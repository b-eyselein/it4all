package model.essentials

import com.google.common.base.Strings
import model.essentials.NumberBase._

import scala.util.Try

case class NAryNumber(decimalValue: Int = 0, base: NumberBase) {

  def +(that: NAryNumber) = new NAryNumber(this.decimalValue + that.decimalValue, this.base)

  def toTwoComp: String = {
    val binString = Integer.toBinaryString(decimalValue)
    NAryNumber.padBinary(binString substring Math.max(0, binString.length() - 8))
  }

  override def toString: String = toString(false)

  def toString(withBase: Boolean): String = {
    var result = Integer.toString(Math.abs(decimalValue), base.base)

    if (base == BINARY)
      result = NAryNumber.padBinary(result)

    if (decimalValue < 0)
      result = '-' + result

    if (withBase) result + "_" + base.base else result
  }

}

object NAryNumber {

  def parse(input: String, base: NumberBase): Option[NAryNumber] = {
    val decimalValue = Try(Some(Integer.parseInt(input.trim().replaceAll("\\s", ""), base.base))) getOrElse None

    decimalValue map (NAryNumber(_, base))
  }

  def parseTwoComplement(input: String): NAryNumber = {
    val trimmedInput = input.trim().replaceAll("\\s", "")

    if (trimmedInput.charAt(0) != '1')
    // Positive number...
      new NAryNumber(Integer.parseInt(trimmedInput, 2), BINARY)
    else {
      val invertedInt = invertDigits(trimmedInput)
      new NAryNumber(-1 * (Integer.parseInt(invertedInt, 2) + 1), BINARY)
    }
  }

  def invertDigits(binaryInt: String): String = binaryInt.replace("0", "a").replace("1", "0").replace("a", "1")

  def padBinary(binary: String): String = {
    val ungrouped = binary.trim().replaceAll("\\s", "")
    val padLength = 4 * Math.max(2, Math.ceil(ungrouped.length / 4).toInt)
    val newResult = Strings.padStart(ungrouped, padLength, '0')
    newResult.grouped(4).mkString(" ")
    //    String.join(" ", FOUR_SPLITTER.splitToList(newResult))
  }

}