package model

import com.google.common.base.Splitter
import com.google.common.base.Strings

case class NAryNumber(decimalValue: Int = 0, base: NumberBase) {

  val FOUR_SPLITTER = Splitter.fixedLength(4)

  def +(that: NAryNumber) = new NAryNumber(this.decimalValue + that.decimalValue, this.base)

  //  @Override
  //  def equals(obj Object) {
  //    return obj instanceof NAryNumber && hashCode() == obj.hashCode()
  //  }

  //  @Override
  //  public int hashCode() {
  //    return decimalValue
  //  }

  //  @Override
  //  public String toString() {
  //    return toString(false)
  //  }

  //  public String toString(boolean withBase) {
  //    String result = Integer.toString(Math.abs(decimalValue), base.getBase())
  //
  //    if(base == NumberBase.BINARY)
  //      result = padBinary(result)
  //
  //    if(decimalValue < 0)
  //      result = '-' + result
  //
  //    return withBase ? result + "_" + base.getBase() : result
  //  }

  def toTwoComp(): String = {
    val binString = Integer.toBinaryString(decimalValue)
    NAryNumber.padBinary(binString.substring(Math.max(0, binString.length() - 8)))
  }

}

object NAryNumber {
  def parse(input: String, base: NumberBase): NAryNumber = {
    val value = Integer.parseInt(input.trim().replaceAll("\\s", ""), base.base)
    new NAryNumber(value, base)
  }

  def parseTwoComplement(input: String): NAryNumber = {
    val trimmedInput = input.trim().replaceAll("\\s", "")

    if (trimmedInput.charAt(0) != '1')
      // Positive number...
      return new NAryNumber(Integer.parseInt(trimmedInput, 2), BINARY)

    val invertedInt = invertDigits(trimmedInput)

    return new NAryNumber(-1 * (Integer.parseInt(invertedInt, 2) + 1), BINARY)
  }

  def invertDigits(binaryInt: String) = binaryInt.replace("0", "a").replace("1", "0").replace("a", "1")

  def padBinary(binary: String): String = {
    val ungrouped = binary.trim().replaceAll("\\s", "")
    val padLength = 4 * Math.max(2, Math.ceil(ungrouped.length / 4).toInt)
    val newResult = Strings.padStart(ungrouped, padLength, '0')
    newResult.grouped(4).mkString(" ")
    //    String.join(" ", FOUR_SPLITTER.splitToList(newResult))
  }

}