package model;

import scala.util.Random;

abstract class BooleanQuestion(val variables: List[Variable]) {

  val GENERATOR = new Random()

  def getJoinedVariables() = variables mkString (", ")

  def getNumberOfLines() = scala.math.pow(2, variables.size).toInt

}

object BooleanQuestion {
  val SOLUTION_VARIABLE: Variable = 'z';
  val LEARNER_VARIABLE: Variable = 'y';
}
