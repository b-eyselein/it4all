package model.NAryTests;

import static org.junit.Assert.*;
import org.junit.Test;

import model.NAryNumbers.BinaryNumber;

public class Test_Binary
{

	@Test
	public void test()
	{
		String bin_1 = "1101";
		String bin_2 = "101101";
		String bin_3 = "1000111001101";
		String bin_4 = "0b10101";
		
		BinaryNumber bin_a = BinaryNumber.StringToBin(bin_1);
		BinaryNumber bin_b = BinaryNumber.StringToBin(bin_2);
		BinaryNumber bin_c = BinaryNumber.StringToBin(bin_3);
		BinaryNumber bin_d = BinaryNumber.StringToBin(bin_4);
		
		// Tests the correct representation
		assertEquals(bin_a.toString(),"00001101");
		assertEquals(bin_b.toString(),"00101101");
		assertEquals(bin_c.toString(),"0001000111001101");
		assertEquals(bin_d.toString(),"00010101");
	}

}
