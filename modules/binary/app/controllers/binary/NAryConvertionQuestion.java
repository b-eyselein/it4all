package controllers.binary;

import java.util.Random;

import model.NAryNumbers.NAryNumber;

public class NAryConvertionQuestion {
	  private NAryNumber number;
	  private String fromNumberType;
	  private String toNumberType;
	  private boolean toDecimalNumber;
	  
	  public NAryConvertionQuestion() {
		  Random generator = new Random();
		  	// Create random number with random value
		    int nValue = generator.nextInt(256);
		    int nBase = generator.nextInt(3);
		    String numberType;
		    if(nBase == 0) {
		    	numberType = "Binärzahl";
		    	nBase = 2;
		    } else if(nBase == 1) {
		    	numberType = "Oktalzahl";
		    	nBase = 8;
		    } else {
		    	numberType = "Hexadezimalzahl";
		    	nBase = 16;
		    }
		    number = new NAryNumber(nValue, nBase);

		    // Randomly convert from decimal to nary or vice versa
		    int nToDecimalNumber = generator.nextInt(2);
		    if (nToDecimalNumber == 0) {
		    	toDecimalNumber = false;
		    	toNumberType = numberType;
		    	fromNumberType = "Dezimalzahl";
		    } else {
		    	toDecimalNumber = true;
		    	toNumberType = "Dezimalzahl";
		    	fromNumberType = numberType;
		    }
	  }
	  
	  public String getToNumberType() {
		  return toNumberType;
	  }
	  
	  String getFromNumberType() {
		  return fromNumberType;
	  }
	  
	  String getFromValue() {
		  if(toDecimalNumber) return number.toString();
		  else return number.toDec();
	  }
	  
	  String getToValue() {
		  if(toDecimalNumber) return number.toDec();
		  else return number.toString();
	  }
}
