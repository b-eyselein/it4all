package model.NAryNumbers;

/**
 * This class models an n-ary number up to base 32.
 */
public class NAryNumber {
  private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV".toCharArray();
  
  private static final String UNSIGNED_HEX_REGEX = "0x[a-v0-9]*";
  private static final String SIGNED_HEX_REGEX = "-?" + UNSIGNED_HEX_REGEX;
  
  private static final String UNSIGNED_OCT_REGEX = "0o[a-v0-9]*";
  private static final String SIGNED_OCT_REGEX = "-?" + UNSIGNED_OCT_REGEX;
  
  protected static final String UNSIGNED_BIN_REGEX = "0b[a-v0-9]*";
  protected static final String SIGNED_BIN_REGEX = "-?" + UNSIGNED_BIN_REGEX;
  
  /**
   * Adds two given n-ary numbers.
   * @param number1 is the first summand.
   * @param number2 is the second summand.
   * @return a n-ary number that represents the sum of the two summands.
   */
  public static NAryNumber addNArys(NAryNumber number1, NAryNumber number2) {
    NumberBase base = number2.getBase();
    int value = number1.getValue()+number2.getValue();
    return new NAryNumber(value,base);
  }
  
  /**
   * Converts a string with characters in range 0..9a...v or A...V with optional
   * prefix "0b", "0o" or "0x" into an instance of NAryNumber.
   *
   * @param input
   *          String that represents the value of the number in the given base.
   * @param base
   *          Base of the n-ary number.
   * @return Instance of NAryNumber with given base and value.
   * @throws IllegalArgumentException
   *           in case input contains wrong characters.
   */
  public static NAryNumber stringToNAry(String input, NumberBase base) throws IllegalArgumentException {
    // Catch negative sign
    boolean neg = false;
    
    input = input.replaceAll("\\s", "");
    
    if(input.toCharArray()[0] == '-') {
      neg = true;
      input = input.substring(1);
    }
    
    // Cut off prefixes
    if(input.toLowerCase().matches(UNSIGNED_HEX_REGEX) && base.getBase() == 16)
      input = input.substring(2);
    
    if(input.toLowerCase().matches(UNSIGNED_OCT_REGEX) && base.getBase() == 8)
      input = input.substring(2);
    
    if(input.toLowerCase().matches(UNSIGNED_BIN_REGEX) && base.getBase() == 2)
      input = input.substring(2);
    
    if(!input.matches("[a-vA-V0-9]*"))
      throw new IllegalArgumentException("The input string does contains unallowed characters. \n"
          + "Only characters in range 0..9, a...v or A...V are allowed.");
    
    char[] coeffs = input.toCharArray();
    int value = 0;
    int length = input.length() - 1;
    int power = 0;
    for(int i = length; i >= 0; i--) {
      if(Character.getNumericValue(coeffs[i]) >= base.getBase()) {
        throw new IllegalArgumentException("The digit " + coeffs[i] + " has a numeric value of "
            + Character.getNumericValue(coeffs[i]) + ", which is greater than the base " + base
            + ". \nOnly digits of value less than the given base are allowed.");
      }
      value += Math.pow(base.getBase(), power) * Character.getNumericValue(coeffs[i]);
      power++;
    }
    if(neg)
      value *= -1;
    
    return new NAryNumber(value, base);
  }
  
  /**
   * Converts a given string that specifies the base into a n-ary number.
   * @param input
   *          String that represents the value of the number and specifies the base (0b for binary, 0o for octal, 0x for hexadecimal)
   * @return Instance of NAryNumber with specified base and given value.
   * @throws IllegalArgumentException
   *           in case the base is not specified in the input string.
   */
  public static NAryNumber StringToNAry(String input) throws IllegalArgumentException {
    if(input.toLowerCase().matches(SIGNED_HEX_REGEX))
      return stringToNAry(input, NumberBase.HEXADECIMAL);
    
    if(input.toLowerCase().matches(SIGNED_OCT_REGEX))
      return stringToNAry(input, NumberBase.OCTAL);
    
    if(input.toLowerCase().matches(SIGNED_BIN_REGEX))
      return stringToNAry(input, NumberBase.BINARY);
    
    throw new IllegalArgumentException("The input " + input
        + "does not specify the base. It needs to start with 0b for binary, 0o for octal or 0x for hexadecimal numbers!");
  }
  
  /**
   * The base of the n-ary number.
   */
  protected NumberBase base;
  
  /**
   * The value of the n-ary number.
   */
  protected int value;
  
  /**
   * The standard constructor.
   * @param value
   * @param base
   * @throws IllegalArgumentException in case the base is greater than 32.
   */
  public NAryNumber(int value, NumberBase base) throws IllegalArgumentException {
    if(base.getBase() > 32)
      throw new IllegalArgumentException("NAryNumber supports only numbers up to base 32");
    
    this.base = base;
    this.value = value;
  }
  
  /**
   * The default constructor generating a NAryNumber of value 0 with the given base.
   * @param base
   * @throws IllegalArgumentException in case the base is greater than 32.
   */
  public NAryNumber(NumberBase base) throws IllegalArgumentException {
    this(0, base);
  }

  /**
   * Getter for the base.
   * @return base of this instance.
   */
  public NumberBase getBase() {
    return base;
  }
  
  /**
   * Getter for the value.
   * @return value of this instance.
   */ 
  public int getValue() {
    return value;
  }
  
  /**
   * Setter for the value.
   */ 
  public void setValue(int value) {
	    this.value = value;
  }
  
  /**
   * "Equal to" sign.
   * @param i is another instance of an NAryNumber.
   */
  public boolean equ(NAryNumber i) {
	    return value == i.getValue();
  }  
  
  /**
   * "Greater than or equal to" sign.
   * @param i is another instance of an NAryNumber.
   */
  public boolean geq(NAryNumber i) {
    if(value >= i.getValue())
      return true;
    return false;
  }
  
  /**
   * "Greater than" sign.
   * @param i is another instance of an NAryNumber.
   */
  public boolean gtr(NAryNumber i) {
    if(value > i.getValue())
      return true;
    return false;
  }
  
  /**
   * "Less than or equal to" sign.
   * @param i is another instance of an NAryNumber.
   */
  public boolean leq(NAryNumber i) {
    if(value <= i.getValue())
      return true;
    return false;
  }
  
  /**
   * "Less than" sign.
   * @param i is another instance of an NAryNumber.
   */
  public boolean lss(NAryNumber i) {
    if(value < i.getValue())
      return true;
    return false;
  }
  
  /**
   * "Not equal to" sign.
   * @param i is another instance of an NAryNumber.
   */
  public boolean neq(NAryNumber i) {
    if(value != i.getValue())
      return true;
    return false;
  }
  
  /**
   * @return value of the number to a string in decimal representation.
   */
  public String toDec() {
    return "" + value;
  }
  
  /**
   * @return value of the number into a string represented according to its base.
   */
  @Override
  public String toString() {
    int absoluteValue = Math.abs(value);
    
    if(absoluteValue == 0)
      return "0";
    
    // Reversed Horner's method
    String result = "";
    
    while(absoluteValue >= 1) {
      result += ALPHABET[absoluteValue % base.getBase()];
      absoluteValue = absoluteValue / base.getBase();
    }
    
    if(value < 0)
      result += '-';
    
    result = new StringBuffer(result).reverse().toString();
    return result;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + value;
    return result;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof NAryNumber))
      return false;
    NAryNumber other = (NAryNumber) obj;
    return value == other.value;
  }
}
