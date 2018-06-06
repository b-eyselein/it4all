package model.bool

import model.bool.BoolConsts._
import model.bool.ScalaNode.{constant, not}

import scala.language.postfixOps

case class BoolTableRow(assignments: Map[Variable, Boolean]) {

  def asChar(variable: Variable): Char = if (this (variable)) '1' else '0'

  def apply(variable: Variable) = assignments(variable)

  def variables: Iterable[Variable] = assignments.keys filter (variable => variable != SolVariable && variable != LerVariable)

  def isSet(variable: Variable): Boolean = assignments.isDefinedAt(variable)

  def +(toAssign: (Variable, Boolean)) = new BoolTableRow(assignments = assignments + (toAssign._1 -> toAssign._2))

  def identifier: String = assignments.toSeq.filter(as => as._1 != SolVariable && as._1 != LerVariable).sortBy(_._1.variable) map (as => as._1 + (if (as._2) "1" else "0")) mkString

}

object BoolTableRow {

  def apply(assignments: Map[Variable, Boolean]) = new BoolTableRow(assignments)

  def apply(assigns: (Variable, Boolean)*) = new BoolTableRow(assigns.toMap)

  def fromAssignments(assignments: Seq[BoolAssignment]): BoolTableRow = new BoolTableRow(assignments.map(a => (a.variable, a.value)).toMap)

  def generateAllAssignments(variables: Seq[Variable]): Seq[BoolTableRow] = variables.sorted.toList match {
    // TODO: eventually tail recursive?
    case Nil          => List.empty
    case head :: Nil  => List(BoolTableRow(head -> false), BoolTableRow(head -> true))
    case head :: tail =>
      val falseAssignments, trueAssignments = generateAllAssignments(tail)
      (falseAssignments map (_ + (head -> false))) ++ (trueAssignments map (_ + (head -> true)))

  }

  def getNF(assignments: Seq[BoolTableRow], takePos: Boolean, innerF: (ScalaNode, ScalaNode) => ScalaNode, outerF: (ScalaNode, ScalaNode) => ScalaNode): ScalaNode =
    assignments
      .filter(takePos ^ _.apply(SolVariable))
      .map(as => as.variables map (v => if (takePos ^ as(v)) v else not(v)) reduceLeft innerF) match {
      case Nil => constant(takePos)
      case l   => l reduceLeft outerF
    }

  def disjunktiveNormalForm(assignments: Seq[BoolTableRow]): ScalaNode = getNF(assignments, takePos = false, AndScalaNode, OrScalaNode)

  def konjunktiveNormalForm(assignments: Seq[BoolTableRow]): ScalaNode = getNF(assignments, takePos = true, OrScalaNode, AndScalaNode)
}
