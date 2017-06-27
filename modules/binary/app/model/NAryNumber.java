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
    String trimmedInput = input.trim().replaceAll("\\s", "");
    
    if(trimmedInput.charAt(0) != '1')
      // Positive number...
      return new NAryNumber(Integer.parseInt(trimmedInput, 2), NumberBase.BINARY);
    
    String invertedInt = invertDigits(trimmedInput);
    
    return new NAryNumber(-1 * (Integer.parseInt(invertedInt, 2) + 1), NumberBase.BINARY);
  }
  
  protected static String invertDigits(String binaryInt) {
    return binaryInt.replace("0", "a").replace("1", "0").replace("a", "1");
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
