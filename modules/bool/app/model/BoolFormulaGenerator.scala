package model;

import model.ScalaNode._
import scala.util.Random

object BoolFormulaGenerator {

  val RANDOM = new Random()

  val allVars = ('a' to 'z').map(toVariable(_)).toList

  val MIN_VARS = 2
  val MAX_VARS = 3

  val MIN_DEPTH = 1
  val MAX_DEPTH = 2

  def generateRandom(): ScalaNode = {
    val depth = randomBetween(MIN_DEPTH, MAX_DEPTH)

    if (depth < 2)
      return generateRandomOperator('a', 'b');

    val variables = allVars take randomBetween(MIN_VARS, MAX_VARS)

    val leftChild = generateRandomOperator(takeRandom(variables), takeRandom(variables));
    val rightChild = generateRandomOperator(takeRandom(variables), takeRandom(variables));

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
  def generateRandomOperator(leftChild: ScalaNode, rightChild: ScalaNode): ScalaNode = {
    val left = if (RANDOM.nextInt(3) == 2) leftChild.negate() else leftChild
    val right = if (RANDOM.nextInt(3) == 2) rightChild.negate() else rightChild

    if (RANDOM.nextBoolean()) left and right else left or right
  }

  def takeRandom[A](variables: List[A]): A = variables(RANDOM.nextInt(variables.size))

  def randomBetween(start: Int, end: Int) = start + RANDOM.nextInt((end - start + 1))

}