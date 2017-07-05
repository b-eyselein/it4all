package model;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.node.And;
import model.node.BoolNode;
import model.node.Not;
import model.node.Or;
import model.node.Variable;

public class BoolFormulaGenerator {
  
  private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
  
  private static final int MIN_VARS = 2;
  private static final int MAX_VARS = 3;
  
  private static final int MIN_DEPTH = 1;
  private static final int MAX_DEPTH = 2;
  
  private BoolFormulaGenerator() {
    
  }
  
  public static BoolNode generateRandom() {
    int depth = RANDOM.nextInt(MIN_DEPTH, MAX_DEPTH + 1);
    
    if(depth < 2)
      return generateRandomOperator(new Variable('a'), new Variable('b'));
    
    List<Variable> variables = IntStream.range('a', 'z').limit(RANDOM.nextInt(MIN_VARS, MAX_VARS + 1))
        .mapToObj(i -> new Variable((char) i)).collect(Collectors.toList());
    
    BoolNode leftChild = generateRandomOperator(takeRandomVariable(variables), takeRandomVariable(variables));
    BoolNode rightChild = generateRandomOperator(takeRandomVariable(variables), takeRandomVariable(variables));
    
    return generateRandomOperator(leftChild, rightChild);
  }
  
  /**
   * Generiert einen neuen Operator nach folgender Verteilung:
   *
   * <ul>
   * <li>50% AND</li>
   * <li>50% OR</li>
   * <li>zusaetzlich 33%, dass linker oder rechter Kindoperator negiert
   * wird</li>
   * </ul>
   */
  private static BoolNode generateRandomOperator(BoolNode leftChild, BoolNode rightChild) {
    boolean negLeft = RANDOM.nextInt(3) == 2;
    boolean negRight = RANDOM.nextInt(3) == 2;
    
    BoolNode left = negLeft ? new Not(leftChild) : leftChild;
    BoolNode right = negRight ? new Not(rightChild) : rightChild;
    
    return RANDOM.nextBoolean() ? new And(left, right) : new Or(left, right);
    
  }
  
  private static BoolNode takeRandomVariable(List<Variable> variables) {
    return variables.get(RANDOM.nextInt(variables.size()));
  }
  
}