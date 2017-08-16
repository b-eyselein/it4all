package model;

import model.ScalaNode._
import scala.language.postfixOps
import scala.util.Random

object BoolFormulaGenerator {

  val RANDOM = new Random()

  val allVars = 'a' to 'z' map toVariable toList

  val MIN_VARS = 2
  val MAX_VARS = 3

  val MIN_DEPTH = 1
  val MAX_DEPTH = 2

  def generateRandom(): ScalaNode = {
    val depth = randomBetween(MIN_DEPTH, MAX_DEPTH)

    if (depth < 2)
      return generateRandomOperator('a', 'b');

    val count = randomBetween(MIN_VARS, MAX_VARS)
    val variables = allVars take count

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
    if (RANDOM.nextInt(3) == 2) leftChild.negate();

    if (RANDOM.nextInt(3) == 2) rightChild.negate();

    if (RANDOM.nextBoolean()) leftChild and rightChild else leftChild or rightChild;
  }

  def takeRandom[A](variables: List[A]): A = variables(RANDOM.nextInt(variables.size))

  def randomBetween(start: Int, end: Int) = start + RANDOM.nextInt((end - start + 1))

}