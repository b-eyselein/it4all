package model.NAryNumbers;

import model.NAryNumbers.questions.NumberBase;

public class BinaryNumber extends NAryNumber {

  public static BinaryNumber nAryToBinary(NAryNumber nAry) {
    return new BinaryNumber(nAry.getValue());
  }

  public static BinaryNumber stringToBin(String input) throws IllegalArgumentException {
    if(input.toLowerCase().matches(UNSIGNED_BIN_REGEX))
      input = input.substring(2);
    return new BinaryNumber(NAryNumber.stringToNAry(input, NumberBase.BINARY).getValue());
  }

  public BinaryNumber() {
    super(0, NumberBase.BINARY);
  }

  public BinaryNumber(int value) {
    super(value, NumberBase.BINARY);
  }

  /**
   * Returns a string of at least 8 digits (2 Bytes). Always fills with leading
   * zeros up to the next Byte. Examples: 101 is represented as 00000101
   * 110001111 is represented as 000110001111
   */
  @Override
  public String toString() {
    String result = super.toString();
    int bytes = (int) Math.ceil((double) result.length() / 4);
    if(bytes < 2)
      bytes = 2;
    int leadingZeros = bytes * 4 - result.length();
    for(int i = 0; i < leadingZeros; i++) {
      result = 0 + result;
    }
    return result;
  }
}
