package model.NAryNumbers;

public class NAryNumber {
  private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV".toCharArray();
  
  private static final String HEX_REGEX = "-?0x[a-v0-9]*";
  private static final String OCT_REGEX = "-?0o[a-v0-9]*";
  private static final String BIN_REGEX = "-?0b[a-v0-9]*";
  
  // adds two given NAryNumbers
  public static NAryNumber addNArys(NAryNumber number1, NAryNumber number2) {
    NAryNumber result = new NAryNumber(number1.getValue(), number2.getBase());
    result.add(number2);
    return result;
  }
  
  public static NAryNumber StringToNAry(String input) throws IllegalArgumentException {
    if(input.toLowerCase().matches(HEX_REGEX))
      return StringToNAry(input, 16);
    
    if(input.toLowerCase().matches(OCT_REGEX))
      return StringToNAry(input, 8);
    
    if(input.toLowerCase().matches(BIN_REGEX))
      return StringToNAry(input, 2);
    
    throw new IllegalArgumentException("The input " + input
        + "does not specify the base. It needs to start with 0b for binary, 0o for octal or 0x for hexadecimal numbers!");
  }
  
  /**
   * Converts a string with characters in range 0..9a...v or A...V into an
   * instance of NAryNumber.
   *
   * @param input
   *          String that represents the value of the number in the given base.
   * @param base
   *          Base of the n-ary number.
   * @return Instance of NAryNumber with given base and value.
   * @throws IllegalArgumentException
   *           in case input contains wrong characters.
   */
  public static NAryNumber StringToNAry(String input, int base) throws IllegalArgumentException {
    NAryNumber result = new NAryNumber(base);
    
    // Catch negative sign
    boolean neg = false;
    if(input.toCharArray()[0] == '-') {
      neg = true;
      input = input.substring(1);
    }
    
    // Cut off prefixes
    if(input.toLowerCase().matches("0x[a-v0-9]*") && base == 16)
      input = input.substring(2);
    
    if(input.toLowerCase().matches("0o[a-v0-9]*") && base == 8)
      input = input.substring(2);
    
    if(input.toLowerCase().matches("0b[a-v0-9]*") && base == 2)
      input = input.substring(2);
    
    if(!input.matches("[a-vA-V0-9]*"))
      throw new IllegalArgumentException("The input string does contains unallowed characters. \n"
          + "Only characters in range 0..9, a...v or A...V are allowed.");
    
    char[] coeffs = input.toCharArray();
    int value = 0;
    int length = input.length() - 1;
    int power = 0;
    for(int i = length; i >= 0; i--) {
      if(Character.getNumericValue(coeffs[i]) >= base) {
        throw new IllegalArgumentException("The digit " + coeffs[i] + " has a numeric value of "
            + Character.getNumericValue(coeffs[i]) + ", which is greater than the base " + base
            + ". \nOnly digits of value less than the given base are allowed.");
      }
      value += Math.pow(base, power) * Character.getNumericValue(coeffs[i]);
      power++;
    }
    if(neg)
      value *= -1;
    result.setValue(value);
    return result;
  }
  
  protected int base;
  
  protected int value;
  
  public NAryNumber(int base) throws IllegalArgumentException {
    this(0, base);
  }
  
  public NAryNumber(int value, int base) throws IllegalArgumentException {
    if(base > 32)
      throw new IllegalArgumentException("NAryNumber supports only numbers up to base 32");
    
    this.base = base;
    this.value = value;
  }
  
  public NAryNumber add(NAryNumber i) {
    value += i.getValue();
    return this;
  }
  
  public NAryNumber div(NAryNumber i) {
    value /= i.getValue();
    return this;
  }
  
  public boolean equ(NAryNumber i) {
    if(value == i.getValue())
      return true;
    return false;
  }
  
  public boolean equals(NAryNumber i) {
    if(base == i.getBase() && value == i.getValue())
      return true;
    return false;
  }
  
  public boolean geq(NAryNumber i) {
    if(value >= i.getValue())
      return true;
    return false;
  }
  
  public int getBase() {
    return base;
  }
  
  public int getValue() {
    return value;
  }
  
  public boolean gtr(NAryNumber i) {
    if(value > i.getValue())
      return true;
    return false;
  }
  
  public boolean leq(NAryNumber i) {
    if(value <= i.getValue())
      return true;
    return false;
  }
  
  public boolean lss(NAryNumber i) {
    if(value < i.getValue())
      return true;
    return false;
  }
  
  public NAryNumber mul(NAryNumber i) {
    value *= i.getValue();
    return this;
  }
  
  public boolean neq(NAryNumber i) {
    if(value != i.getValue())
      return true;
    return false;
  }
  
  public void setValue(int value) {
    this.value = value;
  }
  
  public NAryNumber sub(NAryNumber i) {
    value -= i.getValue();
    return this;
  }
  
  public String toDec() {
    return "" + value;
  }
  
  @Override
  public String toString() {
    int absoluteValue = Math.abs(value);
    
    if(absoluteValue == 0)
      return "0";
    
    // Umgekehrtes Hornerschema
    String result = "";

    while(absoluteValue >= 1) {
      result += ALPHABET[absoluteValue % base];
      absoluteValue = absoluteValue / base;
    }
    
    if(value < 0)
      result += '-';
    
    return new StringBuffer(result).reverse().toString();
  }
}
