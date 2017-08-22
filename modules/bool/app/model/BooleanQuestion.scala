package model

import model.ScalaNode._
import scala.util.Random

abstract sealed class BooleanQuestion(val variables: List[Variable]) {
  def joinedVariables = variables.mkString(",")

  def numberOfLines = scala.math.pow(2, variables.size).toInt
}

object BooleanQuestion {
  def randomBetweenInclBounds(startIncl: Int, endIncl: Int) = startIncl + RANDOM.nextInt((endIncl - startIncl + 1))

  val SOLUTION_VARIABLE: Variable = 'z'
  val LEARNER_VARIABLE: Variable = 'y'

  val RANDOM = new Random()
}

case class CreationQuestion(vars: List[Variable], val solutions: List[Assignment]) extends BooleanQuestion(vars)

object CreationQuestion {
  def generateNew(): CreationQuestion = {
    val variables = ('a' to 'z').take(BooleanQuestion.randomBetweenInclBounds(2, 3)).map(toVariable(_)).toList

    val assignments: List[Assignment] = Assignment
      .generateAllAssignments(variables)
      .map(as => as + (BooleanQuestion.SOLUTION_VARIABLE -> BooleanQuestion.RANDOM.nextBoolean()))

    return new CreationQuestion(variables, assignments)
  }
}

case class FilloutQuestion(val formula: ScalaNode, assignments: List[Assignment]) extends BooleanQuestion(formula.usedVariables.toList) {
  def getFormulaAsHtml: String = {
    var formulaAsHtml = formulaAsString
    for ((key, value) <- FilloutQuestion.HTML_REPLACERS) formulaAsHtml = formulaAsHtml.replaceAll(key, value)
    formulaAsHtml
  }

  def formulaAsString = formula.getAsString(false)

  def isCorrect() = assignments.forall(as => {
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

  def generateNew() = {
    val formula = BoolFormulaGenerator.generateRandom()
    new FilloutQuestion(formula, Assignment.generateAllAssignments(formula.usedVariables.toList))
  }
}
