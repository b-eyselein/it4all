package model;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import model.node.And;
import model.node.Node;
import model.node.Not;
import model.node.Or;
import model.node.Variable;
import model.tree.BoolFormula;

public class BoolFormulaGenerator {
  
  private static ThreadLocalRandom random = ThreadLocalRandom.current();
  
  private static final int MIN_VARS = 2;
  private static final int MAX_VARS = 3;
  
  private static final int MIN_DEPTH = 1;
  private static final int MAX_DEPTH = 2;
  
  private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
  
  private BoolFormulaGenerator() {
    
  }
  
  public static BoolFormula generateRandom() {
    int depth = random.nextInt(MIN_DEPTH, MAX_DEPTH + 1);
    
    if(depth == 2) {
      // Initialise variables
      int numOfVariables = random.nextInt(MIN_VARS, MAX_VARS + 1);
      List<Variable> variables = ALPHABET.chars().limit(numOfVariables).mapToObj(i -> new Variable((char) i))
          .collect(Collectors.toList());
      
      // Generate leftChild
      Node leftChild = generateRandomOperator(takeRandomVariable(variables), takeRandomVariable(variables));

      // Generate rightChild
      Node rightChild = generateRandomOperator(takeRandomVariable(variables), takeRandomVariable(variables));
      
      Node rootNode = generateRandomOperator(leftChild, rightChild);
      return new BoolFormula(rootNode);
    } else { // depth == 1
      List<Variable> variables = Arrays.asList(new Variable('a'), new Variable('b'));
      Node rootNode = generateRandomOperator(variables.get(0), variables.get(1));
      return new BoolFormula(rootNode);
    }
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
  private static Node generateRandomOperator(Node leftChild, Node rightChild) {
    boolean negLeft = false;
    boolean negRight = false;
    
    if(random.nextInt(3) == 2)
      negLeft = true;
    if(random.nextInt(3) == 2)
      negRight = true;
    
    if(random.nextBoolean())
      // 50% chance and
      return new And(negLeft ? new Not(leftChild) : leftChild, negRight ? new Not(rightChild) : rightChild);
    else
      // 50% change or
      return new Or(negLeft ? new Not(leftChild) : leftChild, negRight ? new Not(rightChild) : rightChild);
  
  }

  private static Node takeRandomVariable(List<Variable> variables) {
    return variables.get(random.nextInt(variables.size()));
  }
  
}