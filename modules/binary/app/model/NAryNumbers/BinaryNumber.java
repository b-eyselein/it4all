package model.NAryNumbers;

import java.util.LinkedList;

import com.google.common.base.Strings;

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
   * Returns a string of at least 8 digits (2 bytes) in groups of 4 digits.
   * Always fills up to the next byte with leading zeros. Examples:
   * <ul>
   * <li>101 is represented as 0000 0101</li>
   * <li>1111 is represented as 0000 1111</li>
   * <li>1 0101 0001 as 0001 0101 0001</li>
   * </ul>
   */
  @Override
  public String toString() {
    String result = super.toString();
    int digits = 4 * Math.max(2, (int) Math.ceil((double) result.length() / 4));
    result = Strings.padStart(result, digits, '0');

    LinkedList<String> teilString = new LinkedList<>();
    for(int i = 0; i < digits; i = i + 4)
      teilString.add(result.substring(i, i + 4));
    result = String.join(" ", teilString);

    return result;
  }
}
