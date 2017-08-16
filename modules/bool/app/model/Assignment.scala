package model;

import scala.collection.mutable.MapBuilder
import scala.language.implicitConversions
import model.ScalaNode._

class Assignment(var assignments: Map[Variable, Boolean]) {
  def apply(variable: Variable) = assignments(variable)

  def asChar(variable: Variable): Char = if (get(variable)) '1' else '0'

  def get(variable: Variable) = assignments(variable)

  def isCorrect = get(BooleanQuestion.LEARNER_VARIABLE) == get(BooleanQuestion.SOLUTION_VARIABLE)

  def getColor() = if (isCorrect) "success" else "danger";

  def getLearnerValueAsChar = asChar(BooleanQuestion.LEARNER_VARIABLE)

  def variables = assignments.keys
    .filter(variable => variable != BooleanQuestion.SOLUTION_VARIABLE && variable != BooleanQuestion.LEARNER_VARIABLE)

  def isSet(variable: Variable) = assignments.isDefinedAt(variable)

  def +(toAssign: (Variable, Boolean)) = { assignments = assignments + (toAssign._1 -> toAssign._2) }
}

object Assignment {
  implicit def intToBool(value: Int) = value != 0

  def apply(assigns: (Variable, Boolean)*) = new Assignment(assigns.toMap)

  def generateAllAssignments(variables: List[Variable]): List[Assignment] = {
    variables match {
      case Nil => List.empty
      case head :: Nil => List(Assignment(head -> false), Assignment(head -> true))
      case head :: tail => {
        val falseAssignments, trueAssignments = generateAllAssignments(tail)

        falseAssignments.foreach { _ + (head -> false) }
        trueAssignments.foreach { _ + (head -> true) }

        falseAssignments ++ trueAssignments
      }
    }
  }

  def getDisjunktiveNormalForm(assignments: List[Assignment]): ScalaNode = assignments
    .filter(as => as.get(BooleanQuestion.SOLUTION_VARIABLE))
    .map {
      as =>
        as.variables.map(v => { if (as(v)) v else ScalaNode.not(v) })
          .reduceLeft((v, restVs) => v and restVs)
    }
    .reduceLeft((node, restNodes) => node or restNodes)

  def getKonjunktiveNormalForm(assignments: List[Assignment]) = assignments
    .filter(as => !as.get(BooleanQuestion.SOLUTION_VARIABLE))
    .map(as => {
      as.variables.map(v => { if (as(v)) ScalaNode.not(v) else v })
        .reduceLeft((v, restVs) => v or restVs)
    }).reduceLeft((node, restNodes) => node and restNodes)

}
