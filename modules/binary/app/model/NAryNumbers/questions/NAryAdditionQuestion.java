package model.NAryNumbers.questions;

import java.util.Random;

import model.NAryNumbers.NAryNumber;
import model.NAryNumbers.NumberBase;

public class NAryAdditionQuestion {
  private static final Random GENERATOR = new Random();
  
  /**
   * Generates a new addition question.
   * 
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
  
  private NAryNumber firstSummand;
  private NAryNumber secondSummand;
  
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
   * 
   * @param theFirstSummand
   * @param theSecondSummand
   * @param theQuestionType
   */
  public NAryAdditionQuestion(int theFirstSummand, int theSecondSummand, NumberBase theQuestionType) {
    questionType = theQuestionType;
    firstSummand = new NAryNumber(theFirstSummand, questionType);
    secondSummand = new NAryNumber(theSecondSummand, questionType);
    sum = NAryNumber.addNArys(firstSummand, secondSummand);
    learnerSolution = new NAryNumber(questionType);
  }
  
  /**
   * Constructor to use HTML form information (strings and integers).
   * 
   * @param firstSummandInNAry
   *          is the string representation of the first summand.
   * @param secondSummandInNAry
   *          is the string representation of the second summand.
   * @param base
   *          is the integer representation of the base (question type).
   * @param theLearnerSolution
   *          is the string input of the learner.
   */
  public NAryAdditionQuestion(String firstSummandInNAry, String secondSummandInNAry, int base,
      String theLearnerSolution) {
    questionType = NumberBase.getByBase(base);
    firstSummand = new NAryNumber(firstSummandInNAry, questionType);
    secondSummand = new NAryNumber(secondSummandInNAry, questionType);
    sum = NAryNumber.addNArys(firstSummand, secondSummand);
    learnerSolution = new NAryNumber(theLearnerSolution, questionType);
  }
  
  /**
   * Checks the correctness of the learners solution.
   * 
   * @return
   */
  public boolean checkSolution() {
    return sum.equals(learnerSolution);
  }
  
  /**
   * Getter for the base.
   * 
   * @return integer representation of the NumberBase.
   */
  public int getBase() {
    return questionType.getBase();
  }
  
  /**
   * Getter for the first summand.
   * 
   * @return instance of NAryNumber.
   */
  public NAryNumber getFirstSummand() {
    return firstSummand;
  }
  
  /**
   * Getter for the second summand.
   * 
   * @return instance of NAryNumber.
   */
  public NAryNumber getSecondSummand() {
    return secondSummand;
  }
  
  /**
   * Getter for the addition result.
   * 
   * @return instance of NAryNumber.
   */
  public NAryNumber getSum() {
    return sum;
  }
  
  /**
   * Getter for the learner solution.
   * 
   * @return instance of NAryNumber.
   */
  public NAryNumber getLearnerSolution() {
    return learnerSolution;
  }
  
  /**
   * Getter for the question type (NumberBase).
   * 
   * @return the question's NumberBase.
   */
  public NumberBase getQuestionType() {
    return questionType;
  }
}
