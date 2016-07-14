package model.NAryNumbers;

import java.util.LinkedList;

import com.google.common.base.Strings;

/**
 * This class models a binary number extending the n-ary number. The key
 * difference is the representation in byte-wise notation.
 */
public class BinaryNumber extends NAryNumber {
  
  /**
   * Converts a given n-ary number into a binary number.
   * 
   * @param nAry
   *          instance of NAryNumber.
   * @return instance of BinaryNumber with the value of the given number.
   */
  public static BinaryNumber nAryToBinary(NAryNumber nAry) {
    return new BinaryNumber(nAry.getValue());
  }
  
  /**
   * Converts a given n-ary number into a binary number.
   * 
   * @param nAry
   *          instance of NAryNumber.
   * @return instance of BinaryNumber with the value of the given number.
   */
  public BinaryNumber(String input) {
    super(input, NumberBase.BINARY);
  }
  
  /**
   * The default constructor generating a binary number of value 0.
   */
  public BinaryNumber() {
    super(0, NumberBase.BINARY);
  }
  
  /**
   * The standard constructor generating a binary number of the given value.
   * 
   * @param value
   *          of the binary number.
   */
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
    
    return result;
  }
}
