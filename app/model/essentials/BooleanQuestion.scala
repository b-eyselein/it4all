package model.essentials

import model.essentials.BoolAssignment.generateAllAssignments
import model.essentials.EssentialsConsts._

import scala.language.postfixOps
import scala.util.Random

object BooleanQuestion {

  private def randomBetweenInclBounds(startIncl: Int, endIncl: Int): Int = startIncl + RANDOM.nextInt(endIncl - startIncl + 1)

  private def takeRandom[A](seq: Seq[A]): A = seq(RANDOM.nextInt(seq.size))

  private val RANDOM = new Random

  private val MIN_VARS = 2
  private val MAX_VARS = 3

  private val MIN_DEPTH = 1
  private val MAX_DEPTH = 2

  def generateRandom: ScalaNode = if (randomBetweenInclBounds(MIN_DEPTH, MAX_DEPTH) < 2)
    generateRandomOperator(Variable('a'), Variable('b'))
  else {
    val variables = ('a' to 'z') map Variable take randomBetweenInclBounds(MIN_VARS, MAX_VARS)

    val leftChild = generateRandomOperator(takeRandom(variables), takeRandom(variables))
    val rightChild = generateRandomOperator(takeRandom(variables), takeRandom(variables))

    generateRandomOperator(leftChild, rightChild)
  }

  /**
    * Generiert einen neuen Operator nach folgender Verteilung:
    *
    * <ul>
    * <li>50% AND</li>
    * <li>50% OR</li>
    * <li>zusaetzlich 33%, dass linker oder rechter Kindoperator negiert wird</li>
    * </ul>
    */
  private def generateRandomOperator(leftChild: ScalaNode, rightChild: ScalaNode): ScalaNode = {
    val left = if (RANDOM.nextInt(3) == 2) leftChild.negate else leftChild
    val right = if (RANDOM.nextInt(3) == 2) rightChild.negate else rightChild

    if (RANDOM.nextBoolean) left and right else left or right
  }

  def generateNewCreationQuestion: CreationQuestion = {
    val variables = ('a' to 'z') take randomBetweenInclBounds(2, 3) map Variable toSet

    val assignments: Seq[BoolAssignment] = generateAllAssignments(variables toSeq) map (as => as + (SolVariable -> RANDOM.nextBoolean))

    CreationQuestion(variables, assignments)
  }

  def generateNewFilloutQuestion: FilloutQuestion = FilloutQuestion(generateRandom)


}

sealed trait BooleanQuestion {

  val variables: Set[Variable]

}

case class CreationQuestion(variables: Set[Variable], solutions: Seq[BoolAssignment]) extends BooleanQuestion {

  def joinedVariables: String = variables mkString ","

}

case class FilloutQuestion(formula: ScalaNode) extends BooleanQuestion {

  val assignments: Seq[BoolAssignment] = generateAllAssignments(formula.usedVariables toSeq)

  override val variables: Set[Variable] = formula.usedVariables

}
