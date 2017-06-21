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
    TO_TEST.forEach((decimalvalue, baseAndToParse) -> {
      baseAndToParse.forEach((base, toParse) -> {
        NAryNumber nary = new NAryNumber(decimalvalue, base);
        assertThat(nary.toString(), equalTo(toParse.toUpperCase()));
        assertThat(nary.toString(true), equalTo(toParse.toUpperCase() + "_" + base.getBase()));
      });
    });
  }

}
