package model.essentials

import model.essentials.EssentialsConsts._
import model.essentials.ScalaNode.{constant, not}

import scala.language.postfixOps

case class BoolAssignment(assignments: Map[Variable, Boolean]) {

  def asChar(variable: Variable): Char = if (this (variable)) '1' else '0'

  def asChar(char: Char): Char = if (this (Variable(char))) '1' else '0'

  def apply(variable: Variable) = assignments(variable)

  def isCorrect: Boolean = apply(LerVariable) == apply(SolVariable)

  def getColor: String = if (isCorrect) "success" else "danger"

  def variables: Iterable[Variable] = assignments.keys filter (variable => variable != SolVariable && variable != LerVariable)

  def isSet(variable: Variable): Boolean = assignments.isDefinedAt(variable)

  def +(toAssign: (Variable, Boolean)) = new BoolAssignment(assignments = assignments + (toAssign._1 -> toAssign._2))

  def identifier: String = assignments.toSeq.filter(as => as._1 != SolVariable && as._1 != LerVariable).sortBy(_._1.variable) map (as => as._1 + (if (as._2) "1" else "0")) mkString

}

object BoolAssignment {

  def apply(assignments: Map[Variable, Boolean]) = new BoolAssignment(assignments)

  def apply(assigns: (Variable, Boolean)*) = new BoolAssignment(assigns.toMap)

  def generateAllAssignments(variables: Seq[Variable]): Seq[BoolAssignment] = variables.sorted.toList match {
    case Nil          => List.empty
    case head :: Nil  => List(BoolAssignment(head -> false), BoolAssignment(head -> true))
    case head :: tail =>
      val falseAssignments, trueAssignments = generateAllAssignments(tail)
      (falseAssignments map (_ + (head -> false))) ++ (trueAssignments map (_ + (head -> true)))

  }

  def getNF(assignments: Seq[BoolAssignment], takePos: Boolean, innerF: (ScalaNode, ScalaNode) => ScalaNode, outerF: (ScalaNode, ScalaNode) => ScalaNode): ScalaNode =
    assignments
      .filter(takePos ^ _.apply(SolVariable))
      .map(as => as.variables map (v => if (takePos ^ as(v)) v else not(v)) reduceLeft innerF) match {
      case Nil => constant(takePos)
      case l   => l reduceLeft outerF
    }

  def getDisjunktiveNormalForm(assignments: Seq[BoolAssignment]): ScalaNode = getNF(assignments, takePos = false, AndScalaNode, OrScalaNode)

  def getKonjunktiveNormalForm(assignments: Seq[BoolAssignment]): ScalaNode = getNF(assignments, takePos = true, OrScalaNode, AndScalaNode)
}
