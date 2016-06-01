package model.NAryNumbers;

public class BinaryNumber extends NAryNumber
{
	public BinaryNumber(int value)
	{
		super(value,2);
	}
	
	public BinaryNumber()
	{
		super(0,2);
	}
	
	/**
	 * Returns a string of at least 8 digits (2 Bytes).
	 * Always fills with leading zeros up to the next Byte.
	 * Examples:
	 * 101 is represented as 00000101
	 * 110001111 is represented as 000110001111
	 */
	public String toString()
	{
		String result = super.toString();
		int bytes = (int) Math.ceil((double) result.length() / 4);
		if (bytes < 2) bytes = 2;
		int leadingZeros = bytes * 4 - result.length();
		for (int i = 0; i < leadingZeros; i++)
		{
			result = 0 + result;
		}
		return result;
	}
	
	public static BinaryNumber StringToBin(String input) throws IllegalArgumentException
	{	
		if(input.toLowerCase().matches("0b[a-v0-9]*")) input = input.substring(2);
		BinaryNumber result = new BinaryNumber(NAryNumber.StringToNAry(input,2).getValue());
		return result;
	}
}
