package model.essentials

import model.essentials.ScalaNode._

import scala.util.Random

abstract sealed class BooleanQuestion(val variables: List[Variable]) {
  val joinedVariables: String = variables.mkString(",")

  def numberOfLines: Int = scala.math.pow(2, variables.size).toInt
}

object BooleanQuestion {
  def randomBetweenInclBounds(startIncl: Int, endIncl: Int): Int = startIncl + RANDOM.nextInt(endIncl - startIncl + 1)

  val SOLUTION_VARIABLE: Variable = Variable('z')
  val LEARNER_VARIABLE : Variable = Variable('y')

  val RANDOM = new Random
}

case class CreationQuestion(vars: List[Variable], solutions: List[BoolAssignment]) extends BooleanQuestion(vars)

object CreationQuestion {
  def generateNew: CreationQuestion = {
    val variables = ('a' to 'z').take(BooleanQuestion.randomBetweenInclBounds(2, 3)).map(toVariable).toList

    val assignments: List[BoolAssignment] = BoolAssignment
      .generateAllAssignments(variables)
      .map(as => as + (BooleanQuestion.SOLUTION_VARIABLE -> BooleanQuestion.RANDOM.nextBoolean))

    new CreationQuestion(variables, assignments)
  }
}

case class FilloutQuestion(formula: ScalaNode, assignments: List[BoolAssignment]) extends BooleanQuestion(formula.usedVariables.toList) {
  def getFormulaAsHtml: String = {
    var formulaAsHtml = formulaAsString
    for ((key, value) <- FilloutQuestion.HTML_REPLACERS) formulaAsHtml = formulaAsHtml.replaceAll(key, value)
    formulaAsHtml
  }

  def formulaAsString: String = formula.getAsString(false)

  val isCorrect: Boolean = assignments.forall(as => {
    as.isSet(BooleanQuestion.LEARNER_VARIABLE) && as(BooleanQuestion.LEARNER_VARIABLE) == as(BooleanQuestion.SOLUTION_VARIABLE)
  })
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
    new FilloutQuestion(formula, BoolAssignment.generateAllAssignments(formula.usedVariables.toList))
  }
}
