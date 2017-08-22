package model;

import scala.collection.mutable.MapBuilder
import scala.language.implicitConversions
import model.ScalaNode._

class Assignment(assignments: Map[Variable, Boolean]) {

  val SOL_VAR = BooleanQuestion.SOLUTION_VARIABLE
  val LEA_VAR = BooleanQuestion.LEARNER_VARIABLE

  def asChar(variable: Variable) = if (apply(variable)) '1' else '0'

  def apply(variable: Variable) = assignments(variable)

  def isCorrect = apply(LEA_VAR) == apply(SOL_VAR)

  def getColor = if (isCorrect) "success" else "danger";

  def getLearnerValueAsChar = asChar(LEA_VAR)

  def variables = assignments.keys.filter(variable => variable != SOL_VAR && variable != LEA_VAR)

  def isSet(variable: Variable) = assignments.isDefinedAt(variable)

  override def toString = assignments
    .filter(as => as._1 != LEA_VAR && as._1 != SOL_VAR)
    .map(as => as._1 + ":" + (if (as._2) "1" else "0"))
    .mkString(",")

  def +(toAssign: (Variable, Boolean)) = new Assignment(assignments = assignments + (toAssign._1 -> toAssign._2))
}

object Assignment {
  implicit def intToBool(value: Int) = value != 0

  def apply(assigns: (Variable, Boolean)*) = new Assignment(assigns.toMap)

  def generateAllAssignments(variables: List[Variable]): List[Assignment] = {
    variables.sorted.reverse match {
      case Nil => List.empty
      case head :: Nil => List(Assignment(head -> false), Assignment(head -> true))
      case head :: tail => {
        val falseAssignments, trueAssignments = generateAllAssignments(tail)
        falseAssignments.map(_ + (head -> false)) ++ trueAssignments.map(_ + (head -> true))
      }
    }
  }

  def getNF(assignments: List[Assignment], takePos: Boolean, innerF: (ScalaNode, ScalaNode) => ScalaNode, outerF: (ScalaNode, ScalaNode) => ScalaNode) = assignments
    .filter(takePos ^ _(BooleanQuestion.SOLUTION_VARIABLE))
    .map(as => as.variables.map(v => if (takePos ^ as(v)) v else ScalaNode.not(v)).reduceLeft(innerF)) match {
      case Nil => takePos
      case l => l.reduceLeft(outerF)
    }
  
  def getDisjunktiveNormalForm(assignments: List[Assignment]) = getNF(assignments, false, AndScalaNode(_, _), OrScalaNode(_, _))

  def getKonjunktiveNormalForm(assignments: List[Assignment]) = getNF(assignments, true, OrScalaNode(_, _), AndScalaNode(_, _))
}
