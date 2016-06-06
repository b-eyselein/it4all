package model.NAryNumbers;

public class NAryNumber
{
	protected int base;
	protected int value;
	
	char[] alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUV".toCharArray();
	
	public NAryNumber(int value, int base) throws IllegalArgumentException
	{
		if (base > 32)
		{
			throw new IllegalArgumentException("NAryNumber supports only numbers up to base 32");
		}
		else this.base = base;
		this.value = value;
	}
	
	public NAryNumber(int base) throws IllegalArgumentException
	{
		this(0,base);
	}

	public String toString() {
		int x = Math.abs(value);
		String result = new String();
		
		// Case value == 0
		if (x == 0) return result += 0;
		
		while(x >= 1)
		{
			int currentDigit = x % base;
			result += alphabet[currentDigit];
			x = x/base;
		}
		
		// Case value < 0
		if (value < 0) result += '-';
		
		result = new StringBuffer(result).reverse().toString();
		return result;
	}
	
	public NAryNumber add(NAryNumber i)
	{
		value += i.getValue();
		return this;
	}
	
	public NAryNumber sub(NAryNumber i)
	{
		value -= i.getValue();
		return this;
	}
	
	public NAryNumber mul(NAryNumber i)
	{
		value *= i.getValue();
		return this;
	}
	
	public NAryNumber div(NAryNumber i)
	{
		value /= i.getValue();
		return this;
	}
	
	public boolean equals(NAryNumber i)
	{
		if (base == i.getBase() && value == i.getValue()) return true;
		return false;
	}
	
	public boolean equ(NAryNumber i)
	{
		if (value == i.getValue()) return true;
		return false;
	}
	
	public boolean neq(NAryNumber i)
	{
		if (value != i.getValue()) return true;
		return false;
	}
	
	public boolean lss(NAryNumber i)
	{
		if (value < i.getValue()) return true;
		return false;
	}
	
	public boolean leq(NAryNumber i)
	{
		if (value <= i.getValue()) return true;
		return false;
	}
	
	public boolean gtr(NAryNumber i)
	{
		if (value > i.getValue()) return true;
		return false;
	}
	
	public boolean geq(NAryNumber i)
	{
		if (value >= i.getValue()) return true;
		return false;
	}

	public String toDec()
	{	
		return ""+value;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public int getBase()
	{
		return base;
	}
	
	/**
	 * Converts a string with characters in range 0..9a...v or A...V into an instance of NAryNumber.
	 * @param input String that represents the value of the number in the given base.
	 * @param base Base of the n-ary number.
	 * @return Instance of NAryNumber with given base and value.
	 * @throws IllegalArgumentException in case input contains wrong characters.
	 */
	public static NAryNumber StringToNAry(String input, int base) throws IllegalArgumentException
	{
		NAryNumber result = new NAryNumber(base);
		
		// Catch negative sign
		boolean neg = false;
		if(input.toCharArray()[0] == '-')
		{
			neg = true;
			input = input.substring(1);
		}
		
		// Cut off prefixes
		if(input.toLowerCase().matches("0x[a-v0-9]*") && base == 16) input = input.substring(2);
		if(input.toLowerCase().matches("0o[a-v0-9]*") && base == 8) input = input.substring(2);
		if(input.toLowerCase().matches("0b[a-v0-9]*") && base == 2) input = input.substring(2);
	
		if(!input.matches("[a-vA-V0-9]*"))
		{
			throw new IllegalArgumentException("The input string does contains unallowed characters. \n"
					+ "Only characters in range 0..9, a...v or A...V are allowed.");
		}
				
		char[] coeffs = input.toCharArray();
		int value = 0;
		int length = input.length()-1;
		int power = 0;
		for (int i = length; i >= 0; i--)
		{
			if (Character.getNumericValue(coeffs[i])>= base)
			{
				throw new IllegalArgumentException("The digit "  + coeffs[i] + " has a numeric value of " +
						Character.getNumericValue(coeffs[i]) + ", which is greater than the base " + base + 
						". \nOnly digits of value less than the given base are allowed.");
			}
			value += Math.pow(base,power)*Character.getNumericValue(coeffs[i]);
			power++;
		}
		if (neg) value*= -1;
		result.setValue(value);
		return result;
	}
	
	public static NAryNumber StringToNAry(String input) throws IllegalArgumentException
	{
		if(input.toLowerCase().matches("-?0x[a-v0-9]*")) return StringToNAry(input,16);
		if(input.toLowerCase().matches("-?0o[a-v0-9]*")) return StringToNAry(input,8);
		if(input.toLowerCase().matches("-?0b[a-v0-9]*")) return StringToNAry(input,2);
		throw new IllegalArgumentException("The input " + input + "does not specify the base.");
	}
}
