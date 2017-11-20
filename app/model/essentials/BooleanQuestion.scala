package model.essentials

import model.essentials.BoolAssignment.generateAllAssignments
import model.essentials.EssentialsConsts._
import model.essentials.ScalaNode._

import scala.language.postfixOps
import scala.util.Random

abstract sealed class BooleanQuestion(val variables: Seq[Variable]) {
  val joinedVariables: String = variables.mkString(",")

  def numberOfLines: Int = scala.math.pow(2, variables.size).toInt
}

object BooleanQuestion {
  def randomBetweenInclBounds(startIncl: Int, endIncl: Int): Int = startIncl + RANDOM.nextInt(endIncl - startIncl + 1)

  val RANDOM = new Random
}

case class CreationQuestion(vars: Seq[Variable], solutions: Seq[BoolAssignment]) extends BooleanQuestion(vars)

object CreationQuestion {
  def generateNew: CreationQuestion = {
    val variables = ('a' to 'z').take(BooleanQuestion.randomBetweenInclBounds(2, 3)) map toVariable

    val assignments: Seq[BoolAssignment] = generateAllAssignments(variables) map (as => as + (SolVariable -> BooleanQuestion.RANDOM.nextBoolean))

    CreationQuestion(variables, assignments)
  }
}

case class FilloutQuestion(formula: ScalaNode, assignments: Seq[BoolAssignment]) extends BooleanQuestion(formula.usedVariables toSeq) {
  def getFormulaAsHtml: String = {
    var formulaAsHtml = formulaAsString
    for ((key, value) <- FilloutQuestion.HTML_REPLACERS) formulaAsHtml = formulaAsHtml.replaceAll(key, value)
    formulaAsHtml
  }

  def formulaAsString: String = formula.getAsString(false)

  val isCorrect: Boolean = assignments.forall(as => as.isSet(LerVariable) && as(LerVariable) == as(SolVariable))

}

object FilloutQuestion {
  val HTML_REPLACERS = Map(
    "IMPL" -> "&rArr",
    "NOR" -> "&#x22bd",
    "NAND" -> "&#x22bc",
    "EQUIV" -> "&hArr",
    "NOT" -> "&not",
    "AND" -> "&and",
    "XOR" -> "&oplus",
    "OR" -> "&or")

  def generateNew: FilloutQuestion = {
    val formula = BoolFormulaGenerator.generateRandom
    FilloutQuestion(formula, generateAllAssignments(formula.usedVariables.toSeq))
  }
}
