package model.NAryTests;

import static org.junit.Assert.*;
import org.junit.Test;

import model.nary.NAryNumber;
import model.nary.NumberBase;

public class Test_Compare {
  
  @Test
  public void test() {
    NAryNumber hex_234 = new NAryNumber(234, NumberBase.HEXADECIMAL);
    NAryNumber hex_234_2 = new NAryNumber(234, NumberBase.HEXADECIMAL);
    NAryNumber oct_234 = new NAryNumber(234, NumberBase.OCTAL);
    NAryNumber bin_234 = new NAryNumber(234, NumberBase.BINARY);
    NAryNumber bin_127 = new NAryNumber(127, NumberBase.BINARY);
    NAryNumber oct_63 = new NAryNumber(63, NumberBase.OCTAL);
    
    // Testing equality of objects
    assertTrue(hex_234.equals(hex_234_2));
    
    // Testing comparation operators
    assertTrue(hex_234.equ(oct_234));
    assertTrue(hex_234.equ(bin_234));
    assertTrue(hex_234.neq(bin_127));
    assertTrue(hex_234.geq(oct_63));
    assertFalse(oct_63.geq(bin_234));
    assertTrue(bin_127.leq(bin_234));
    assertFalse(hex_234.gtr(hex_234_2));
    assertFalse(bin_127.gtr(oct_234));
    assertTrue(oct_63.lss(bin_234));
  }
  
}
