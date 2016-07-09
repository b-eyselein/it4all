package model.NAryNumbers.questions;

import java.util.Random;

import model.NAryNumbers.BinaryNumber;
import model.NAryNumbers.NAryNumber;
import model.NAryNumbers.NumberBase;

/**
 * This class models an addition question.
 */
public class NAryAdditionQuestion {
  private static final Random GENERATOR = new Random();
  
  /**
   * Generates a new addition question.
   * @return instance of NAryAdditionQuestion.
   */
  public static NAryAdditionQuestion generateNew() {
    // Exclude 0
    int sum = GENERATOR.nextInt(255) + 1;
    
    int firstSummand = GENERATOR.nextInt(sum);
    int secondSummand = sum - firstSummand;
    
    NumberBase questionType = NumberBase.values()[GENERATOR.nextInt(3)];
    
    return new NAryAdditionQuestion(firstSummand, secondSummand, questionType);
  }
  
  /**
   * First summand.
   */
  private NAryNumber summand_1;
  
  /**
   * Second summand.
   */
  private NAryNumber summand_2;
  
  /**
   * Sum (result of the addition).
   */
  private NAryNumber sum;
  
  /**
   * Base of the numbers to add.
   */
  private NumberBase questionType;
  
  /**
   * Optional learner solution submitted by the user.
   */
  private NAryNumber learnerSolution;
  
  /**
   * Standard constructor.
   * @param firstSummand
   * @param secondSummand
   * @param theQuestionType
   */
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
  
  /**
   * Constructor to use HTML form information (strings and integers).
   * @param firstSummandInNAry is the string representation of the first summand.
   * @param secondSummandInNAry is the string representation of the second summand.
   * @param base is the integer representation of the base (question type).
   * @param theLearnerSolution is the string input of the learner.
   */
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
  
  /**
   * Checks the correctness of the learners solution.
   * @return
   */
  public boolean checkSolution() {
    return sum.equals(learnerSolution);
  }
  
  /**
   * Getter for the base.
   * @return integer representation of the NumberBase.
   */
  public int getBase() {
    return questionType.getBase();
  }
  
  /**
   * Getter for the first summand.
   * @return instance of NAryNumber.
   */
  public NAryNumber getFirstSummand() {
    return summand_1;
  }
  
  /**
   * Getter for the second summand.
   * @return instance of NAryNumber.
   */
  public NAryNumber getSecondSummand() {
	    return summand_2;
  }
  
  /**
   * Getter for the addition result.
   * @return instance of NAryNumber.
   */
  public NAryNumber getSum() {
	    return sum;
  }
  
  /**
   * Getter for the learner solution.
   * @return instance of NAryNumber.
   */
  public NAryNumber getLearnerSolution() {
    return learnerSolution;
  }
  
  /**
   * Getter for the question type (NumberBase).
   * @return the question's NumberBase.
   */
  public NumberBase getQuestionType() {
    return questionType;
  }
}
