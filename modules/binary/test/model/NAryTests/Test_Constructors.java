package model.NAryTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import model.NAryNumbers.NAryNumber;

public class Test_Constructors {
  
  @Test
  public void testNegatives() {
    NAryNumber hex_234 = new NAryNumber(-234, 16);
    assertEquals(hex_234.toString(), "-EA");

    NAryNumber bin_234 = new NAryNumber(-234, 2);
    assertEquals(bin_234.toString(), "-11101010");
  }
  
  @Test
  public void testPositives() {
    NAryNumber hex_234 = new NAryNumber(234, 16);
    NAryNumber oct_63 = new NAryNumber(63, 8);
    
    // Testing for the right value
    assertEquals(hex_234.getValue(), 234);
    assertEquals(oct_63.getValue(), 63);
    
    // Testing for the right base
    assertEquals(hex_234.getBase(), 16);
    assertEquals(oct_63.getBase(), 8);
    
    // Testing for the right representation
    NAryNumber bin_127 = new NAryNumber(127, 2);
    NAryNumber bin_234 = new NAryNumber(234, 2);
    NAryNumber oct_234 = new NAryNumber(234, 8);
    assertEquals(hex_234.toString(), "EA");
    assertEquals(oct_234.toString(), "352");
    assertEquals(bin_234.toString(), "11101010");
    assertEquals(bin_127.toString(), "1111111");
  }
  
}
