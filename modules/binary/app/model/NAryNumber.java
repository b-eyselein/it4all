package model;

import java.util.stream.IntStream;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public class NAryNumber {
  
  private static final Splitter FOUR_SPLITTER = Splitter.fixedLength(4);
  
  private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV".toCharArray();
  
  protected int decimalValue;
  protected NumberBase base;
  
  public NAryNumber(int theDecimalValue, NumberBase theBase) throws IllegalArgumentException {
    decimalValue = theDecimalValue;
    base = theBase;
  }
  
  public NAryNumber(NumberBase base) throws IllegalArgumentException {
    this(0, base);
  }
  
  public static NAryNumber addNArys(NAryNumber number1, NAryNumber number2) {
    NumberBase base = number2.getBase();
    int value = number1.getValue() + number2.getValue();
    return new NAryNumber(value, base);
  }
  
  public static NAryNumber parse(String input) throws IllegalArgumentException {
    String trimmedInput = input.trim().replaceAll("\\s", "");
    boolean isNegative = trimmedInput.startsWith("-");
    
    for(NumberBase base: NumberBase.values())
      if(trimmedInput.matches(base.getRegex()))
        return parse(isNegative ? trimmedInput.substring(3) : trimmedInput.substring(2), base, isNegative);
      
    throw new IllegalArgumentException("The input \"" + input
        + "\" does not specify the base. It needs to start with 0b for binary, 0o for octal or 0x for hexadecimal numbers!");
  }
  
  public static NAryNumber parse(String input, NumberBase numberBase) {
    String trimmedInput = input.trim().replaceAll("\\s", "");
    boolean isNegative = trimmedInput.startsWith("-");
    return parse(isNegative ? trimmedInput.substring(1) : trimmedInput, numberBase, isNegative);
  }
  
  private static NAryNumber parse(String toParse, NumberBase numberBase, boolean isNegative) {
    int theValue = 0;
    int power = 0;
    
    String reversedToParse = new StringBuffer(toParse).reverse().toString();
    
    IntStream.range(0, toParse.length()).forEach(index -> testCharInNumberBase(numberBase, toParse.charAt(index)));
    
    for(char coeff: reversedToParse.toCharArray())
      theValue += Math.pow(numberBase.getBase(), power++) * Character.getNumericValue(coeff);
    
    if(isNegative)
      theValue *= -1;
    
    return new NAryNumber(theValue, numberBase);
  }
  
  private static void testCharInNumberBase(NumberBase numberBase, char coeff) {
    if(Character.getNumericValue(coeff) < numberBase.getBase())
      return;
    
    throw new IllegalArgumentException("The digit " + coeff + " has a numeric value of "
        + Character.getNumericValue(coeff) + ", which is greater than the base " + numberBase
        + ". \nOnly digits of value less than the given base are allowed.");
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
    String result = reversedHornerScheme(Math.abs(decimalValue));
    
    if(base == NumberBase.BINARY) {
      // Pad with '0'
      result = Strings.padStart(result, 4 * Math.max(2, (int) Math.ceil((double) result.length() / 4)), '0');
      
      // Group in blocks of four...
      result = String.join(" ", FOUR_SPLITTER.splitToList(result));
    }
    
    if(decimalValue < 0)
      result = '-' + result;
    
    if(withBase)
      result += "_" + base.getBase();
    
    return result;
  }
  
  /**
   * Reversed Horner's method
   *
   * @param absoluteValue
   * @return
   */
  private String reversedHornerScheme(int absoluteValue) {
    if(absoluteValue == 0)
      return "0";
    
    int newValue = absoluteValue;
    StringBuilder result = new StringBuilder();
    
    while(newValue >= 1) {
      result.append(Character.toString(ALPHABET[newValue % base.getBase()]));
      newValue = newValue / base.getBase();
    }
    
    return result.reverse().toString();
  }
}
