package model.bool

import model.bool.BoolConsts._
import model.bool.ScalaNode.{constant, not}

import scala.language.postfixOps

final case class BoolTableRow(assignments: Map[Variable, Boolean]) {

  def asChar(variable: Variable): Char = if (this (variable)) '1' else '0'

  def apply(variable: Variable): Boolean = assignments(variable)

  def variables: Iterable[Variable] = assignments.keys filter (variable => variable != SolVariable && variable != LerVariable)

  def isSet(variable: Variable): Boolean = assignments.isDefinedAt(variable)

  def +(toAssign: (Variable, Boolean)): BoolTableRow = new BoolTableRow(assignments = assignments + (toAssign._1 -> toAssign._2))

  def identifier: String = assignments.toSeq
    .filter(as => as._1 != SolVariable && as._1 != LerVariable)
    .sortBy(_._1.variable)
    .map(as => s"${as._1}${if (as._2) "1" else "0"}")
    .mkString

}

object BoolTableRow {

  def apply(assignments: Map[Variable, Boolean]): BoolTableRow = new BoolTableRow(assignments)

  def apply(assigns: (Variable, Boolean)*): BoolTableRow = new BoolTableRow(assigns.toMap)

  def fromAssignments(assignments: Seq[BoolAssignment]): BoolTableRow = new BoolTableRow(assignments.map(a => (a.variable, a.value)).toMap)

  def generateAllAssignments(variables: Seq[Variable]): Seq[BoolTableRow] = {

    @annotation.tailrec
    def go(variables: List[Variable], tableRows: Seq[BoolTableRow]): Seq[BoolTableRow] = variables match {
      case Nil          => tableRows
      case head :: tail => tableRows match {
        case Seq() => go(tail, Seq(BoolTableRow(head -> false), BoolTableRow(head -> true)))
        case _     => go(tail, tableRows.map(_ + (head -> false)) ++ tableRows.map(_ + (head -> true)))
      }
    }

    go(variables.sorted.reverse.toList, Seq[BoolTableRow]())
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
