package model;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public class NAryNumber {

  private static final Splitter FOUR_SPLITTER = Splitter.fixedLength(4);

  private int decimalValue;
  private NumberBase base;

  public NAryNumber(int theDecimalValue, NumberBase theBase) {
    decimalValue = theDecimalValue;
    base = theBase;
  }

  public NAryNumber(NumberBase base) {
    this(0, base);
  }

  public static NAryNumber addNArys(NAryNumber number1, NAryNumber number2) {
    return new NAryNumber(number1.getValue() + number2.getValue(), number2.getBase());
  }

  public static NAryNumber parse(String input, NumberBase numberBase) {
    int value = Integer.parseInt(input.trim().replaceAll("\\s", ""), numberBase.getBase());
    return new NAryNumber(value, numberBase);
  }

  public static NAryNumber parseTwoComplement(String input) {
    Integer num = Integer.valueOf(input, 2);
    int value = (num > 32767) ? num - 65536 : num;
    return new NAryNumber(value, NumberBase.BINARY);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof NAryNumber && hashCode() == obj.hashCode();
  }

  public NumberBase getBase() {
    return base;
  }

  public int getValue() {
    return decimalValue;
  }

  @Override
  public int hashCode() {
    return decimalValue;
  }

  @Override
  public String toString() {
    return toString(false);
  }

  public String toString(boolean withBase) {
    String result = Integer.toString(Math.abs(decimalValue), base.getBase());

    if(base == NumberBase.BINARY) {
      result = Strings.padStart(result, 4 * Math.max(2, (int) Math.ceil((double) result.length() / 4)), '0');
      result = String.join(" ", FOUR_SPLITTER.splitToList(result));
    }

    if(decimalValue < 0)
      result = '-' + result;

    return withBase ? result + "_" + base.getBase() : result;
  }

}
