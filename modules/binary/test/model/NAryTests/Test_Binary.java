package model.NAryTests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import model.NAryNumbers.NAryNumber;
import model.NAryNumbers.NumberBase;

public class Test_Binary {
  
  @Test
  public void test() {
    String bin_1 = "1101";
    String bin_2 = "101101";
    String bin_3 = "1000111001101";
    String bin_4 = "0b10101";
    
    NAryNumber bin_a = new NAryNumber(bin_1, NumberBase.BINARY);
    NAryNumber bin_b = new NAryNumber(bin_2, NumberBase.BINARY);
    NAryNumber bin_c = new NAryNumber(bin_3, NumberBase.BINARY);
    NAryNumber bin_d = new NAryNumber(bin_4, NumberBase.BINARY);
    
    // Tests the correct representation
    assertEquals(bin_a.toString(), "0000 1101");
    assertEquals(bin_b.toString(), "0010 1101");
    assertEquals(bin_c.toString(), "0001 0001 1100 1101");
    assertEquals(bin_d.toString(), "0001 0101");
  }
  
}
