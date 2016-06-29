package model.NAryTests;

import static org.junit.Assert.*;
import org.junit.Test;

import model.NAryNumbers.NAryNumber;

public class Test_StringToNaAry
{

	@Test
	public void testPositive()
	{
		String str_hex_234_1 = "ea";
		String str_hex_234_2 = "EA";
		String str_hex_234_3 = "0xEA";
		String str_hex_234_4 = "0XeA";
		
		NAryNumber hex_1 = NAryNumber.StringToNAry(str_hex_234_1,16);
		NAryNumber hex_2 = NAryNumber.StringToNAry(str_hex_234_2,16);
		NAryNumber hex_3 = NAryNumber.StringToNAry(str_hex_234_3,16);
		NAryNumber hex_4 = NAryNumber.StringToNAry(str_hex_234_4,16);
		NAryNumber hex_5 = NAryNumber.StringToNAry(str_hex_234_3);
		NAryNumber hex_6 = NAryNumber.StringToNAry(str_hex_234_4);
		
		assertEquals(hex_1.toString(),str_hex_234_2);
		assertEquals(hex_5.toString(),str_hex_234_2);
		assertEquals(hex_6.toString(),str_hex_234_2);
		assertEquals(hex_6.getValue(),234);
		assertTrue(hex_3.equals(hex_4));
		assertTrue(hex_2.equals(hex_6));
	}
	
	@Test
	public void testNegative()
	{
		String str_hex_234_1 = "-ea";
		String str_hex_234_2 = "-EA";
		String str_hex_234_3 = "-0xEA";
		String str_hex_234_4 = "-0XeA";
		
		NAryNumber hex_1 = NAryNumber.StringToNAry(str_hex_234_1,16);
		NAryNumber hex_2 = NAryNumber.StringToNAry(str_hex_234_2,16);
		NAryNumber hex_3 = NAryNumber.StringToNAry(str_hex_234_3,16);
		NAryNumber hex_4 = NAryNumber.StringToNAry(str_hex_234_4,16);
		NAryNumber hex_5 = NAryNumber.StringToNAry(str_hex_234_3);
		NAryNumber hex_6 = NAryNumber.StringToNAry(str_hex_234_4);
		
		assertEquals(hex_1.toString(),str_hex_234_2);
		assertEquals(hex_5.toString(),str_hex_234_2);
		assertEquals(hex_6.toString(),str_hex_234_2);
		assertEquals(hex_6.getValue(),-234);
		assertTrue(hex_3.equals(hex_4));
		assertTrue(hex_2.equals(hex_6));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testWrongCharacter()
	{
		String str_hex_234_1 = "eaZ";
		NAryNumber hex_1 = NAryNumber.StringToNAry(str_hex_234_1,16);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testMissingPrefix()
	{
		String str_hex_234_1 = "ea";
		NAryNumber hex_1 = NAryNumber.StringToNAry(str_hex_234_1);
	}
}
