package model.NAryTests;

import static org.junit.Assert.*;
import org.junit.Test;

import model.NAryNumbers.NAryNumber;

public class Test_Constructors 
{
	@Test
	public void testPositives()
	{
		NAryNumber hex_234 = new NAryNumber(234,16);
		NAryNumber oct_234 = new NAryNumber(234,8);
		NAryNumber bin_234 = new NAryNumber(234,2);
		NAryNumber bin_127 = new NAryNumber(127,2);
		NAryNumber oct_63 = new NAryNumber(63,8);
		
		// Testing for the right value
		assertEquals(hex_234.getValue(),234);
		assertEquals(oct_63.getValue(),63);
		
		// Testing for the right base
		assertEquals(hex_234.getBase(),16);
		assertEquals(oct_63.getBase(),8);
		
		// Testing for the right representation
		assertEquals(hex_234.toString(),"EA");
		assertEquals(bin_234.toString(),"11101010");
		assertEquals(oct_234.toString(),"352");
		assertEquals(bin_127.toString(),"1111111");
	}
	
	@Test
	public void testNegatives()
	{
		NAryNumber hex_234 = new NAryNumber(-234,16);
		NAryNumber bin_234 = new NAryNumber(-234,2);
		System.out.println(hex_234.toString());
	}
}
