package model.NAryNumbers.Questions;

import java.util.Random;

import model.NAryNumbers.*;

public class NAryAdditionQuestion {
  private static final Random GENERATOR = new Random();
  
  private NAryNumber number1;
  private NAryNumber number2;
  private NAryNumber sum;
  private String numberType;
  private String learnerSolution;

  public NAryAdditionQuestion() {
    // Create two random numbers with random values that are at max 256 in sum
    double proportion = GENERATOR.nextDouble();
    int nValue1 = GENERATOR.nextInt((int) (256 * proportion));
    int nValue2 = GENERATOR.nextInt((int) (256 * (1 - proportion)));
    int nBase = GENERATOR.nextInt(3);
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
    if(numberType.equals("Binärzahlen")) {
      sum = BinaryNumber.nAryToBinary(sum);
    }
  }
  
  public NAryAdditionQuestion(int value1, int value2, String numberType, String learnerSolution) {
	  this.learnerSolution = learnerSolution;
	  if (numberType.equals("Oktalzalen")) {
		  number1 = new NAryNumber(value1, 8);
		  number2 = new NAryNumber(value2, 8);
		  sum = NAryNumber.addNArys(number1, number2);
	  }
	  else if (numberType.equals("Hexdezimalzahlen")){
		  number1 = new NAryNumber(value1, 16);
		  number2 = new NAryNumber(value2, 16);
		  sum = NAryNumber.addNArys(number1, number2);
	  } else {
		  number1 = new BinaryNumber(value1);
		  number2 = new BinaryNumber(value2);
		  sum = BinaryNumber.addNArys(number1, number2);
	  }
  }

  public String getNumber1() {
    return number1.toString();
  }

  public String getNumber2() {
    return number2.toString();
  }

  public String getNumberType() {
    return numberType;
  }

  public String getSum() {
    return sum.toString();
  }
  
  public String getLearnerSolution() {
	  return learnerSolution;
  }
  
  public void setLearnerSolution(String solution) {
	  learnerSolution = solution;
  }
  
  public String getBase() {
	  return ""+number1.getBase();
  }
  
  public boolean checkSolution() {
	  if(sum.toString().equals(learnerSolution.toUpperCase())) return true;
	  return false;
  }
}
