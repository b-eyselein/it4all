package model.tools.nary

import model.tools.nary.NumberBase._
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat

class NAryNumberTest {

  private val TO_TEST = Map(
    234 -> Map(HexaDecimal -> "ea", Octal -> "352", Binary -> "1110 1010"),
    127 -> Map(HexaDecimal -> "7f", Octal -> "177", Binary -> "0111 1111"),
    87 -> Map(HexaDecimal -> "57", Octal -> "127", Binary -> "0101 0111"),
    63 -> Map(HexaDecimal -> "3f", Octal -> "77", Binary -> "0011 1111"))

  def checkBase(number: NAryNumber, base: NumberBase): Unit = {
    assertThat("Basis von " + number + " sollte " + base + " sein!", number.base, equalTo(base))
  }

  def checkPaddedString(toPad: String, expected: String): Unit = {
    assertThat("Expecting that padded binary string of " + toPad + " is " + expected, NAryNumber.padBinary(toPad),
      equalTo(expected))
  }

  def checkParsingWithoutMark(decValue: Int, base: NumberBase, toParse: String): Unit = {
    val posLowerHex = NAryNumber.parseNaryNumber(toParse, base)
    posLowerHex foreach { posLowerHex =>
      checkBase(posLowerHex, base)
      checkValue(posLowerHex, decValue)
    }
  }

  def checkValue(posNary: NAryNumber, decimalvalue: Int): Unit = {
    assertThat("Dezimalwert von " + posNary + " sollte " + decimalvalue + " sein!", posNary.decimalValue,
      equalTo(decimalvalue))
  }

  //  @Test
  //  public void testAddNArys() {
  //    NAryNumber n0_8 = new NAryNumber(NumberBase.OCTAL)
  //    NAryNumber n234_2 = new NAryNumber(234, NumberBase.BINARY)
  //    NAryNumber n234_8 = n234_2 + n0_8
  //    checkBase(n234_8, NumberBase.OCTAL)
  //    checkValue(n234_8, 234)
  //  }
  //
  //  @Test(expected = IllegalArgumentException.class)
  //  public void testCharNotInNumberBase() {
  //    NAryNumber.parse("2", NumberBase.BINARY)
  //  }
  //
  //  @Test
  //  public void testConstructor() {
  //    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
  //      baseAndToParse.forEach((base, toParse) -> {
  //        NAryNumber posNary = new NAryNumber(decimalvalue, base)
  //        checkValue(posNary, decimalvalue)
  //        checkBase(posNary, base)
  //      })
  //    })
  //
  //    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
  //      baseAndToParse.forEach((base, toParse) -> {
  //        NAryNumber negNary = new NAryNumber(-decimalvalue, base)
  //        checkValue(negNary, -decimalvalue)
  //        checkBase(negNary, base)
  //      })
  //    })
  //  }
  //
  //  @Test
  //  public void testPadBinary() {
  //    checkPaddedString("0", "0000 0000")
  //    checkPaddedString("011010", "0001 1010")
  //    checkPaddedString("00011010", "0001 1010")
  //    checkPaddedString("0001 1010", "0001 1010")
  //  }
  //
  //  @Test
  //  public void testParseTwoComplement() {
  //    NAryNumber pos0 = NAryNumber.parseTwoComplement("0000 0000")
  //    checkBase(pos0, NumberBase.BINARY)
  //    checkValue(pos0, 0)
  //
  //    NAryNumber pos1 = NAryNumber.parseTwoComplement("0000 0001")
  //    checkBase(pos1, NumberBase.BINARY)
  //    checkValue(pos1, 1)
  //
  //    NAryNumber neg1 = NAryNumber.parseTwoComplement("1111 1111")
  //    checkBase(neg1, NumberBase.BINARY)
  //    checkValue(neg1, -1)
  //
  //    NAryNumber pos26 = NAryNumber.parseTwoComplement("0001 1010")
  //    checkBase(pos26, NumberBase.BINARY)
  //    checkValue(pos26, 26)
  //
  //    NAryNumber neg26 = NAryNumber.parseTwoComplement("1110 0110")
  //    checkBase(neg26, NumberBase.BINARY)
  //    checkValue(neg26, -26)
  //
  //    NAryNumber pos127 = NAryNumber.parseTwoComplement("0111 1111")
  //    checkBase(pos127, NumberBase.BINARY)
  //    checkValue(pos127, 127)
  //
  //    NAryNumber neg127 = NAryNumber.parseTwoComplement("1000 0001")
  //    checkBase(neg127, NumberBase.BINARY)
  //    checkValue(neg127, -127)
  //  }
  //
  //  @Test
  //  public void testParseWithoutMark() {
  //    TO_TEST.forEach((decValue, baseAndToParse) -> {
  //      baseAndToParse.forEach((base, toParse) -> {
  //        checkParsingWithoutMark(decValue, base, toParse.toLowerCase())
  //        checkParsingWithoutMark(decValue, base, toParse.toUpperCase())
  //
  //        checkParsingWithoutMark(-decValue, base, "-" + toParse.toLowerCase())
  //        checkParsingWithoutMark(-decValue, base, "-" + toParse.toUpperCase())
  //      })
  //    })
  //  }
  //
  //  @Test
  //  public void testToString() {
  //    NAryNumber posZeroBinary = new NAryNumber(NumberBase.BINARY)
  //    assertThat(posZeroBinary.toString(), equalTo("0000 0000"))
  //    assertThat(posZeroBinary.toString(true), equalTo("0000 0000_2"))
  //
  //    NAryNumber negZeroBinary = new NAryNumber(-0, NumberBase.BINARY)
  //    assertThat(negZeroBinary.toString(), equalTo("0000 0000"))
  //    assertThat(negZeroBinary.toString(true), equalTo("0000 0000_2"))
  //
  //    NAryNumber posZeroOctal = new NAryNumber(NumberBase.OCTAL)
  //    assertThat(posZeroOctal.toString(), equalTo("0"))
  //    assertThat(posZeroOctal.toString(true), equalTo("0_8"))
  //
  //    NAryNumber negZeroOctal = new NAryNumber(-0, NumberBase.OCTAL)
  //    assertThat(negZeroOctal.toString(), equalTo("0"))
  //    assertThat(negZeroOctal.toString(true), equalTo("0_8"))
  //
  //    NAryNumber posZeroHexa = new NAryNumber(NumberBase.HEXADECIMAL)
  //    assertThat(posZeroHexa.toString(), equalTo("0"))
  //    assertThat(posZeroHexa.toString(true), equalTo("0_16"))
  //
  //    NAryNumber negZeroHexa = new NAryNumber(-0, NumberBase.HEXADECIMAL)
  //    assertThat(negZeroHexa.toString(), equalTo("0"))
  //    assertThat(negZeroHexa.toString(true), equalTo("0_16"))
  //
  //    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
  //      baseAndToParse.forEach((base, toParse) -> {
  //        NAryNumber posNary = new NAryNumber(decimalvalue, base)
  //        assertThat(posNary.toString(), equalTo(toParse))
  //        assertThat(posNary.toString(true), equalTo(toParse + "_" + base.getBase()))
  //
  //        NAryNumber negNary = new NAryNumber(-decimalvalue, base)
  //        assertThat(negNary.toString(), equalTo("-" + toParse))
  //        assertThat(negNary.toString(true), equalTo("-" + toParse + "_" + base.getBase()))
  //      })
  //    })
  //  }
  //
  //  @Test
  //  public void testToTwoComplement() {
  //    assertThat(new NAryNumber(0, NumberBase.BINARY).toTwoComp(), equalTo("0000 0000"))
  //    assertThat(new NAryNumber(-26, NumberBase.BINARY).toTwoComp(), equalTo("1110 0110"))
  //    assertThat(new NAryNumber(-127, NumberBase.BINARY).toTwoComp(), equalTo("1000 0001"))
  //    assertThat(new NAryNumber(-140, NumberBase.BINARY).toTwoComp(), equalTo("0111 0100"))
  //  }

}
