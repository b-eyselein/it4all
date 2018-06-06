package model.bool

import model.bool.BoolTableRow.generateAllAssignments
import model.bool.BoolConsts._

import scala.language.postfixOps


object BooleanQuestion {

  private def randomBetweenInclBounds(startIncl: Int, endIncl: Int): Int = startIncl + generator.nextInt(endIncl - startIncl + 1)

  private def takeRandom[A](seq: Seq[A]): A = seq(generator.nextInt(seq.size))

  private val MinVars = 2
  private val MaxVars = 3

  private val MinDepth = 1
  private val MaxDepth = 2

  def generateRandom: ScalaNode = if (randomBetweenInclBounds(MinDepth, MaxDepth) < 2)
    generateRandomOperator(Variable('a'), Variable('b'))
  else {
    val variables = ('a' to 'z') map Variable take randomBetweenInclBounds(MinVars, MaxVars)

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
    val left = if (generator.nextInt(3) == 2) leftChild.negate else leftChild
    val right = if (generator.nextInt(3) == 2) rightChild.negate else rightChild

    if (generator.nextBoolean) left and right else left or right
  }

  def generateNewCreationQuestion: CreationQuestion = {
    val variables = ('a' to 'z') take randomBetweenInclBounds(2, 3) map Variable toSet

    val assignments = generateAllAssignments(variables toSeq) map (as => as + (SolVariable -> generator.nextBoolean))

    CreationQuestion(assignments)
  }

  def generateNewFilloutQuestion: FilloutQuestion = FilloutQuestion(generateRandom)


}

sealed trait BooleanQuestion {

  val variables: Set[Variable]

  val exPart: BoolExPart

}

case class CreationQuestion(solutions: Seq[BoolTableRow]) extends BooleanQuestion {

  override val variables: Set[Variable] = solutions.headOption map (_.variables.toList.reverse.toSet) getOrElse Set.empty

  def joinedVariables: String = variables mkString ","

  override val exPart: BoolExPart = FormulaCreation

}

case class FilloutQuestion(formula: ScalaNode) extends BooleanQuestion {

  val assignments: Seq[BoolTableRow] = generateAllAssignments(formula.usedVariables toSeq)

  override val variables: Set[Variable] = formula.usedVariables

  override val exPart: BoolExPart = TableFillout

}
