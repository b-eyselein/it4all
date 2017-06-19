package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NaryNumberTest {
  
  @Test
  public void testCompare() {
    NAryNumber hex_234 = new NAryNumber(234, NumberBase.HEXADECIMAL);
    NAryNumber oct_234 = new NAryNumber(234, NumberBase.OCTAL);
    NAryNumber bin_234 = new NAryNumber(234, NumberBase.BINARY);

    NAryNumber hex_233 = new NAryNumber(233, NumberBase.HEXADECIMAL);
    NAryNumber bin_127 = new NAryNumber(127, NumberBase.BINARY);
    NAryNumber oct_63 = new NAryNumber(63, NumberBase.OCTAL);

    // Testing equality of objects
    assertTrue(hex_234.equals(hex_234));

    // Testing comparation operators
    assertTrue(hex_234.equ(oct_234));
    assertTrue(hex_234.equ(bin_234));
    assertFalse(hex_234.equ(hex_233));
    assertFalse(hex_234.equ(oct_63));

    assertTrue(hex_234.neq(bin_127));
    assertFalse(hex_234.neq(bin_234));

    assertFalse(hex_234.gtr(hex_234));
    assertFalse(bin_127.gtr(oct_234));
    
    assertTrue(hex_234.geq(oct_63));
    assertFalse(oct_63.geq(bin_234));

    assertTrue(oct_63.lss(bin_234));
    assertFalse(oct_63.lss(bin_234));
    
    assertTrue(bin_127.leq(bin_234));
    assertFalse(bin_127.leq(bin_127));
    assertFalse(oct_63.leq(bin_127));
  }

  @Test
  public void testConstrucorNegatives() {
    NAryNumber hex_234 = new NAryNumber(-234, NumberBase.HEXADECIMAL);
    assertEquals(hex_234.toString(), "-EA");

    NAryNumber bin_234 = new NAryNumber(-234, NumberBase.BINARY);
    assertEquals(bin_234.toString(), "-1110 1010");
  }

  @Test
  public void testConstructorPositives() {
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

  @Test(expected = IllegalArgumentException.class)
  public void testFromStringMissingPrefix() {
    String str_hex_234_1 = "ea";
    NAryNumber.fromString(str_hex_234_1);
  }

  @Test
  public void testFromStringNegative() {
    String str_hex_234_1 = "-ea";
    String str_hex_234_2 = "-EA";
    String str_hex_234_3 = "-0xEA";
    String str_hex_234_4 = "-0XeA";

    NAryNumber hex_1 = new NAryNumber(str_hex_234_1, NumberBase.HEXADECIMAL);
    NAryNumber hex_2 = new NAryNumber(str_hex_234_2, NumberBase.HEXADECIMAL);
    NAryNumber hex_3 = new NAryNumber(str_hex_234_3, NumberBase.HEXADECIMAL);
    NAryNumber hex_4 = new NAryNumber(str_hex_234_4, NumberBase.HEXADECIMAL);
    NAryNumber hex_5 = NAryNumber.fromString(str_hex_234_3);
    NAryNumber hex_6 = NAryNumber.fromString(str_hex_234_4);

    assertEquals(hex_1.toString(), str_hex_234_2);
    assertEquals(hex_5.toString(), str_hex_234_2);
    assertEquals(hex_6.toString(), str_hex_234_2);
    assertEquals(hex_6.getValue(), -234);
    assertTrue(hex_3.equals(hex_4));
    assertTrue(hex_2.equals(hex_6));
  }

  @Test
  public void testFromStringPositive() {
    String str_hex_234_1 = "ea";
    String str_hex_234_2 = "EA";
    String str_hex_234_3 = "0xEA";
    String str_hex_234_4 = "0XeA";

    NAryNumber hex_1 = new NAryNumber(str_hex_234_1, NumberBase.HEXADECIMAL);
    NAryNumber hex_2 = new NAryNumber(str_hex_234_2, NumberBase.HEXADECIMAL);
    NAryNumber hex_3 = new NAryNumber(str_hex_234_3, NumberBase.HEXADECIMAL);
    NAryNumber hex_4 = new NAryNumber(str_hex_234_4, NumberBase.HEXADECIMAL);
    NAryNumber hex_5 = NAryNumber.fromString(str_hex_234_3);
    NAryNumber hex_6 = NAryNumber.fromString(str_hex_234_4);

    assertEquals(hex_1.toString(), str_hex_234_2);
    assertEquals(hex_5.toString(), str_hex_234_2);
    assertEquals(hex_6.toString(), str_hex_234_2);
    assertEquals(hex_6.getValue(), 234);
    assertTrue(hex_3.equals(hex_4));
    assertTrue(hex_2.equals(hex_6));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromStringWrongCharacter() {
    String str_hex_234_1 = "eaZ";
    new NAryNumber(str_hex_234_1, NumberBase.HEXADECIMAL);
  }

  @Test
  public void testToString() {
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
