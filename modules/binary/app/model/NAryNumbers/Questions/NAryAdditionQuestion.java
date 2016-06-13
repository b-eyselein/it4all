package model.NAryNumbers.Questions;

import java.util.Random;

import model.NAryNumbers.*;

public class NAryAdditionQuestion {
	private NAryNumber number1;
	private NAryNumber number2;
	private NAryNumber sum;
	private String numberType;
	
	public NAryAdditionQuestion() {
		  Random generator = new Random();
		  	// Create two random numbers with random values that are at max 256 in sum
		  	double proportion = generator.nextDouble();
		    int nValue1 = generator.nextInt((int)(256*proportion));
		    int nValue2 = generator.nextInt((int)(256*(1-proportion)));
		    int nBase = generator.nextInt(3);
		    if(nBase == 0) {
		    	numberType = "Binärzahlen";
		    	number1 = new BinaryNumber(nValue1);
			    number2 = new BinaryNumber(nValue2);
		    } else if(nBase == 1) {
		    	numberType = "Oktalzahlen";
		    	number1 = new NAryNumber(nValue1, 8);
			    number2 = new NAryNumber(nValue2, 8);
		    } else {
		    	numberType = "Hexadezimalzahlen";
		    	number1 = new NAryNumber(nValue1, 16);
			    number2 = new NAryNumber(nValue2, 16);
		    }
		    sum = NAryNumber.addNArys(number1, number2);
		    if (numberType.equals("Binärzahlen")) {
		    	sum = BinaryNumber.nAryToBinary(sum);
		    }
	  }
	
	public String getNumberType() {
		return numberType;
	}
	
	public String getNumber1() {
		return number1.toString();
	}
	
	public String getNumber2() {
		return number2.toString();
	}
	
	public String getSum() {
		return sum.toString();
	}
}
