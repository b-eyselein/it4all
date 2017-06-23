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
            NumberBase.HEXADECIMAL, "3F",
            NumberBase.OCTAL, "77",
            NumberBase.BINARY, "0011 1111")
      );
  //@formatter:on

  private static void checkParsingWithMark(Integer decValue, NumberBase base, String toParse) {
    NAryNumber posLowerHex = NAryNumber.parse(toParse);
    assertThat(posLowerHex.getBase(), equalTo(base));
    assertThat(posLowerHex.getValue(), equalTo(decValue));
  }

  private static void checkParsingWithoutMark(Integer decValue, NumberBase base, String toParse) {
    NAryNumber posLowerHex = NAryNumber.parse(toParse, base);
    assertThat(posLowerHex.getBase(), equalTo(base));
    assertThat(posLowerHex.getValue(), equalTo(decValue));
  }

  @Test
  public void testAddNArys() {
    NAryNumber n0_8 = new NAryNumber(NumberBase.OCTAL);
    NAryNumber n234_2 = new NAryNumber(234, NumberBase.BINARY);
    NAryNumber n234_8 = NAryNumber.addNArys(n234_2, n0_8);
    assertThat(n234_8.getBase(), equalTo(NumberBase.OCTAL));
    assertThat(n234_8.getValue(), equalTo(234));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCharNotInNumberBase() {
    NAryNumber.parse("2", NumberBase.BINARY);
  }

  @Test
  public void testConstructor() {
    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
      baseAndToParse.forEach((base, toParse) -> {
        NAryNumber nary = new NAryNumber(decimalvalue, base);
        assertThat(nary.getValue(), equalTo(decimalvalue));
        assertThat(nary.getBase(), equalTo(base));
      });
    });

    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
      baseAndToParse.forEach((base, toParse) -> {
        NAryNumber nary = new NAryNumber(-decimalvalue, base);
        assertThat(nary.getValue(), equalTo(-decimalvalue));
        assertThat(nary.getBase(), equalTo(base));
      });
    });
  }

  @Test(expected = IllegalArgumentException.class)
  public void testParseMissingPrefix() {
    NAryNumber.parse("ea");
  }

  @Test
  public void testParseWithMark() {
    TO_TEST.forEach((decValue, baseAndToParse) -> {
      baseAndToParse.forEach((base, toParse) -> {
        checkParsingWithMark(decValue, base, base.getMark() + toParse.toLowerCase());
        checkParsingWithMark(decValue, base, base.getMark() + toParse.toUpperCase());

        checkParsingWithMark(-decValue, base, "-" + base.getMark() + toParse.toLowerCase());
        checkParsingWithMark(-decValue, base, "-" + base.getMark() + toParse.toUpperCase());
      });
    });
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

  @Test(expected = IllegalArgumentException.class)
  public void testParseWrongCharacter() {
    NAryNumber.parse("eaZ");
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
        assertThat(posNary.toString(), equalTo(toParse.toUpperCase()));
        assertThat(posNary.toString(true), equalTo(toParse.toUpperCase() + "_" + base.getBase()));

        NAryNumber negNary = new NAryNumber(-decimalvalue, base);
        assertThat(negNary.toString(), equalTo("-" + toParse.toUpperCase()));
        assertThat(negNary.toString(true), equalTo("-" + toParse.toUpperCase() + "_" + base.getBase()));
      });
    });
  }

}
