package model.NAryNumbers;

public class HexadecimalNumber extends NAryNumber
{
	public HexadecimalNumber(int value)
	{
		super(value,16);
	}
	
	public HexadecimalNumber()
	{
		super(0,16);
	}
	
	public static HexadecimalNumber StringToHex(String input) throws IllegalArgumentException
	{	
		if(input.toLowerCase().matches("0x[a-v0-9]*")) input = input.substring(2);
		HexadecimalNumber result = new HexadecimalNumber(NAryNumber.StringToNAry(input,16).getValue());
		return result;
	}
}
