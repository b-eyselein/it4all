package model;

import static model.ScalaNode.and;
import static model.ScalaNode.or;
import static model.ScalaNode.variable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoolFormulaGenerator {
  
  private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
  
  private static final int MIN_VARS = 2;
  private static final int MAX_VARS = 3;
  
  private static final int MIN_DEPTH = 1;
  private static final int MAX_DEPTH = 2;
  
  private BoolFormulaGenerator() {
    
  }
  
  public static ScalaNode generateRandom() {
    int depth = RANDOM.nextInt(MIN_DEPTH, MAX_DEPTH + 1);
    
    if(depth < 2)
      return generateRandomOperator(variable('a'), new Variable('b'));
    
    List<Variable> variables = IntStream.range('a', 'z').limit(RANDOM.nextInt(MIN_VARS, MAX_VARS + 1))
        .mapToObj(i -> variable((char) i)).collect(Collectors.toList());
    
    ScalaNode leftChild = generateRandomOperator(takeRandomVariable(variables), takeRandomVariable(variables));
    ScalaNode rightChild = generateRandomOperator(takeRandomVariable(variables), takeRandomVariable(variables));
    
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
  private static ScalaNode generateRandomOperator(ScalaNode leftChild, ScalaNode rightChild) {
    if(RANDOM.nextInt(3) == 2)
      leftChild.negate();
    
    if(RANDOM.nextInt(3) == 2)
      rightChild.negate();
    
    return RANDOM.nextBoolean() ? and(leftChild, rightChild) : or(leftChild, rightChild);
    
  }
  
  private static ScalaNode takeRandomVariable(List<Variable> variables) {
    return variables.get(RANDOM.nextInt(variables.size()));
  }
  
}