package model.NAryTests;

import static org.junit.Assert.*;
import org.junit.Test;

import model.nary.NAryNumber;
import model.nary.NumberBase;

public class Test_StringToNaAry {
  
  @Test(expected = IllegalArgumentException.class)
  public void testMissingPrefix() {
    String str_hex_234_1 = "ea";
    NAryNumber.stringToNAry(str_hex_234_1);
  }
  
  @Test
  public void testNegative() {
    String str_hex_234_1 = "-ea";
    String str_hex_234_2 = "-EA";
    String str_hex_234_3 = "-0xEA";
    String str_hex_234_4 = "-0XeA";
    
    NAryNumber hex_1 = new NAryNumber(str_hex_234_1, NumberBase.HEXADECIMAL);
    NAryNumber hex_2 = new NAryNumber(str_hex_234_2, NumberBase.HEXADECIMAL);
    NAryNumber hex_3 = new NAryNumber(str_hex_234_3, NumberBase.HEXADECIMAL);
    NAryNumber hex_4 = new NAryNumber(str_hex_234_4, NumberBase.HEXADECIMAL);
    NAryNumber hex_5 = NAryNumber.stringToNAry(str_hex_234_3);
    NAryNumber hex_6 = NAryNumber.stringToNAry(str_hex_234_4);
    
    assertEquals(hex_1.toString(), str_hex_234_2);
    assertEquals(hex_5.toString(), str_hex_234_2);
    assertEquals(hex_6.toString(), str_hex_234_2);
    assertEquals(hex_6.getValue(), -234);
    assertTrue(hex_3.equals(hex_4));
    assertTrue(hex_2.equals(hex_6));
  }
  
  @Test
  public void testPositive() {
    String str_hex_234_1 = "ea";
    String str_hex_234_2 = "EA";
    String str_hex_234_3 = "0xEA";
    String str_hex_234_4 = "0XeA";
    
    NAryNumber hex_1 = new NAryNumber(str_hex_234_1, NumberBase.HEXADECIMAL);
    NAryNumber hex_2 = new NAryNumber(str_hex_234_2, NumberBase.HEXADECIMAL);
    NAryNumber hex_3 = new NAryNumber(str_hex_234_3, NumberBase.HEXADECIMAL);
    NAryNumber hex_4 = new NAryNumber(str_hex_234_4, NumberBase.HEXADECIMAL);
    NAryNumber hex_5 = NAryNumber.stringToNAry(str_hex_234_3);
    NAryNumber hex_6 = NAryNumber.stringToNAry(str_hex_234_4);
    
    assertEquals(hex_1.toString(), str_hex_234_2);
    assertEquals(hex_5.toString(), str_hex_234_2);
    assertEquals(hex_6.toString(), str_hex_234_2);
    assertEquals(hex_6.getValue(), 234);
    assertTrue(hex_3.equals(hex_4));
    assertTrue(hex_2.equals(hex_6));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testWrongCharacter() {
    String str_hex_234_1 = "eaZ";
    new NAryNumber(str_hex_234_1, NumberBase.HEXADECIMAL);
  }
}
