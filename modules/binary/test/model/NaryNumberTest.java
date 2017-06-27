package model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class NaryNumberTest {

  // @formatter:off
  private static final Map<Integer, Map<NumberBase, String>> TO_TEST =
      ImmutableMap.of(
        234, ImmutableMap.of(
            NumberBase.HEXADECIMAL, "ea",
            NumberBase.OCTAL, "352",
            NumberBase.BINARY, "1110 1010"),
        127, ImmutableMap.of(
            NumberBase.HEXADECIMAL, "7f",
            NumberBase.OCTAL, "177",
            NumberBase.BINARY, "0111 1111"),
        87, ImmutableMap.of(
            NumberBase.HEXADECIMAL, "57",
            NumberBase.OCTAL, "127",
            NumberBase.BINARY, "0101 0111"),
        63, ImmutableMap.of(
            NumberBase.HEXADECIMAL, "3f",
            NumberBase.OCTAL, "77",
            NumberBase.BINARY, "0011 1111")
      );
  //@formatter:on

  private static void checkBase(NAryNumber number, NumberBase base) {
    assertThat("Basis von " + number + " sollte " + base + " sein!", number.getBase(), equalTo(base));
  }

  private static void checkParsingWithoutMark(Integer decValue, NumberBase base, String toParse) {
    NAryNumber posLowerHex = NAryNumber.parse(toParse, base);
    checkBase(posLowerHex, base);
    checkValue(posLowerHex, decValue);
  }

  private static void checkValue(NAryNumber posNary, Integer decimalvalue) {
    assertThat("Dezimalwert von " + posNary + " sollte " + decimalvalue + " sein!", posNary.getValue(),
        equalTo(decimalvalue));
  }

  @Test
  public void testAddNArys() {
    NAryNumber n0_8 = new NAryNumber(NumberBase.OCTAL);
    NAryNumber n234_2 = new NAryNumber(234, NumberBase.BINARY);
    NAryNumber n234_8 = NAryNumber.addNArys(n234_2, n0_8);
    checkBase(n234_8, NumberBase.OCTAL);
    checkValue(n234_8, 234);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCharNotInNumberBase() {
    NAryNumber.parse("2", NumberBase.BINARY);
  }

  @Test
  public void testConstructor() {
    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
      baseAndToParse.forEach((base, toParse) -> {
        NAryNumber posNary = new NAryNumber(decimalvalue, base);
        checkValue(posNary, decimalvalue);
        checkBase(posNary, base);
      });
    });

    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
      baseAndToParse.forEach((base, toParse) -> {
        NAryNumber negNary = new NAryNumber(-decimalvalue, base);
        checkValue(negNary, -decimalvalue);
        checkBase(negNary, base);
      });
    });
  }

  @Test
  public void testParseTwoComplement() throws Exception {
    NAryNumber pos0 = NAryNumber.parseTwoComplement("0000 0000");
    checkBase(pos0, NumberBase.BINARY);
    checkValue(pos0, 0);

    NAryNumber pos1 = NAryNumber.parseTwoComplement("0000 0001");
    checkBase(pos1, NumberBase.BINARY);
    checkValue(pos1, 1);

    NAryNumber neg1 = NAryNumber.parseTwoComplement("1111 1111");
    checkBase(neg1, NumberBase.BINARY);
    checkValue(neg1, -1);

    NAryNumber pos26 = NAryNumber.parseTwoComplement("0001 1010");
    checkBase(pos26, NumberBase.BINARY);
    checkValue(pos26, 26);

    NAryNumber neg26 = NAryNumber.parseTwoComplement("1110 0110");
    checkBase(neg26, NumberBase.BINARY);
    checkValue(neg26, -26);

    NAryNumber pos127 = NAryNumber.parseTwoComplement("0111 1111");
    checkBase(pos127, NumberBase.BINARY);
    checkValue(pos127, 127);

    NAryNumber neg127 = NAryNumber.parseTwoComplement("1000 0001");
    checkBase(neg127, NumberBase.BINARY);
    checkValue(neg127, -127);
  }

  @Test
  public void testParseWithoutMark() {
    TO_TEST.forEach((decValue, baseAndToParse) -> {
      baseAndToParse.forEach((base, toParse) -> {
        checkParsingWithoutMark(decValue, base, toParse.toLowerCase());
        checkParsingWithoutMark(decValue, base, toParse.toUpperCase());

        checkParsingWithoutMark(-decValue, base, "-" + toParse.toLowerCase());
        checkParsingWithoutMark(-decValue, base, "-" + toParse.toUpperCase());
      });
    });
  }

  @Test
  public void testToString() {
    NAryNumber posZeroBinary = new NAryNumber(NumberBase.BINARY);
    assertThat(posZeroBinary.toString(), equalTo("0000 0000"));
    assertThat(posZeroBinary.toString(true), equalTo("0000 0000_2"));

    NAryNumber negZeroBinary = new NAryNumber(-0, NumberBase.BINARY);
    assertThat(negZeroBinary.toString(), equalTo("0000 0000"));
    assertThat(negZeroBinary.toString(true), equalTo("0000 0000_2"));

    NAryNumber posZeroOctal = new NAryNumber(NumberBase.OCTAL);
    assertThat(posZeroOctal.toString(), equalTo("0"));
    assertThat(posZeroOctal.toString(true), equalTo("0_8"));

    NAryNumber negZeroOctal = new NAryNumber(-0, NumberBase.OCTAL);
    assertThat(negZeroOctal.toString(), equalTo("0"));
    assertThat(negZeroOctal.toString(true), equalTo("0_8"));

    NAryNumber posZeroHexa = new NAryNumber(NumberBase.HEXADECIMAL);
    assertThat(posZeroHexa.toString(), equalTo("0"));
    assertThat(posZeroHexa.toString(true), equalTo("0_16"));

    NAryNumber negZeroHexa = new NAryNumber(-0, NumberBase.HEXADECIMAL);
    assertThat(negZeroHexa.toString(), equalTo("0"));
    assertThat(negZeroHexa.toString(true), equalTo("0_16"));

    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
      baseAndToParse.forEach((base, toParse) -> {
        NAryNumber posNary = new NAryNumber(decimalvalue, base);
        assertThat(posNary.toString(), equalTo(toParse));
        assertThat(posNary.toString(true), equalTo(toParse + "_" + base.getBase()));

        NAryNumber negNary = new NAryNumber(-decimalvalue, base);
        assertThat(negNary.toString(), equalTo("-" + toParse));
        assertThat(negNary.toString(true), equalTo("-" + toParse + "_" + base.getBase()));
      });
    });
  }

}
