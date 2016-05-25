package model.NAryNumbers;

public class OctalNumber extends NAryNumber
{
	public OctalNumber(int value)
	{
		super(value,8);
	}
	
	public OctalNumber()
	{
		super(0,8);
	}
	
	public static OctalNumber StringToOct(String input) throws IllegalArgumentException
	{	
		if(input.toLowerCase().matches("0o[a-v0-9]*")) input = input.substring(2);
		OctalNumber result = new OctalNumber(NAryNumber.StringToNAry(input,8).getValue());
		return result;
	}
}
