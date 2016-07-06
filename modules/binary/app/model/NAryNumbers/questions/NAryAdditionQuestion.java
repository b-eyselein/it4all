package model.NAryNumbers.questions;

import java.util.Random;

import model.NAryNumbers.BinaryNumber;
import model.NAryNumbers.NAryNumber;

public class NAryAdditionQuestion {
  private static final Random GENERATOR = new Random();
  
  public static NAryAdditionQuestion generateNew() {
    // Exclude 0
    int sum = GENERATOR.nextInt(255) + 1;
    
    int firstSummand = GENERATOR.nextInt(sum);
    int secondSummand = sum - firstSummand;
    
    NumberBase questionType = NumberBase.values()[GENERATOR.nextInt(3)];
    
    return new NAryAdditionQuestion(firstSummand, secondSummand, questionType);
  }
  
  private NAryNumber summand_1;
  private NAryNumber summand_2;
  
  private NAryNumber sum;
  private NumberBase questionType;
  
  private NAryNumber learnerSolution;
    
  public NAryAdditionQuestion(int firstSummand, int secondSummand, NumberBase theQuestionType) {
    questionType = theQuestionType;
    if(questionType == NumberBase.BINARY) {
      summand_1 = new BinaryNumber(firstSummand);
      summand_2 = new BinaryNumber(secondSummand);
    } else {
      summand_1 = new NAryNumber(firstSummand, questionType);
      summand_2 = new NAryNumber(secondSummand, questionType);
    }
    sum = NAryNumber.addNArys(summand_1, summand_2);
    learnerSolution = new NAryNumber(questionType);
  }
  
  public NAryAdditionQuestion(String firstSummandInNAry, String secondSummandInNAry, int base,
      String theLearnerSolution) {
    questionType = NumberBase.getByBase(base);
    if(base == 2) {
      summand_1 = BinaryNumber.stringToBin(firstSummandInNAry);
      summand_2 = BinaryNumber.stringToBin(secondSummandInNAry);
    } else {
      summand_1 = NAryNumber.stringToNAry(firstSummandInNAry, questionType);
      summand_2 = NAryNumber.stringToNAry(secondSummandInNAry, questionType);
    }
    sum = NAryNumber.addNArys(summand_1, summand_2);
    if(questionType == NumberBase.BINARY) {
    	learnerSolution = BinaryNumber.stringToBin(theLearnerSolution.replaceAll("\\s", ""));
    } else {
    	learnerSolution = NAryNumber.stringToNAry(theLearnerSolution, questionType);
    }
  }
  
  public boolean checkSolution() {
    return sum.equals(learnerSolution);
  }
  
  public int getBase() {
    return questionType.getBase();
  }
  
  public NAryNumber getFirstSummand() {
    return summand_1;
  }
  
  public NAryNumber getLearnerSolution() {
    return learnerSolution;
  }
  
  public NumberBase getQuestionType() {
    return questionType;
  }
  
  public NAryNumber getSecondSummand() {
    return summand_2;
  }
  
  public NAryNumber getSum() {
    return sum;
  }
  
}
