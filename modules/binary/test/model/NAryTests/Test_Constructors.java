package model.NAryTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import model.nary.NAryNumber;
import model.nary.NumberBase;

public class Test_Constructors {
  
  @Test
  public void testNegatives() {
    NAryNumber hex_234 = new NAryNumber(-234, NumberBase.HEXADECIMAL);
    assertEquals(hex_234.toString(), "-EA");
    
    NAryNumber bin_234 = new NAryNumber(-234, NumberBase.BINARY);
    assertEquals(bin_234.toString(), "-1110 1010");
  }
  
  @Test
  public void testPositives() {
    NAryNumber hex_234 = new NAryNumber(234, NumberBase.HEXADECIMAL);
    NAryNumber oct_63 = new NAryNumber(63, NumberBase.OCTAL);
    
    // Testing for the right value
    assertEquals(hex_234.getValue(), 234);
    assertEquals(oct_63.getValue(), 63);
    
    // Testing for the right base
    assertEquals(hex_234.getBase(), NumberBase.HEXADECIMAL);
    assertEquals(oct_63.getBase(), NumberBase.OCTAL);
    
    // Testing for the right representation
    NAryNumber bin_127 = new NAryNumber(127, NumberBase.BINARY);
    NAryNumber bin_234 = new NAryNumber(234, NumberBase.BINARY);
    NAryNumber oct_234 = new NAryNumber(234, NumberBase.OCTAL);
    assertEquals(hex_234.toString(), "EA");
    assertEquals(oct_234.toString(), "352");
    // Padding!
    assertEquals(bin_234.toString(), "1110 1010");
    assertEquals(bin_127.toString(), "0111 1111");
  }
  
}
