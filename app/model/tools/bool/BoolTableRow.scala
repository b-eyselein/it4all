package model.tools.bool

import model.tools.bool.BoolConsts._
import model.tools.bool.BoolNode.{constant, not}

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
    def go(variables: List[Variable], tableRows: List[BoolTableRow]): Seq[BoolTableRow] = variables match {
      case Nil          => tableRows
      case head :: tail => tableRows match {
        case Nil => go(tail, List(BoolTableRow(head -> false), BoolTableRow(head -> true)))
        case _   => go(tail, tableRows.map(_ + (head -> false)) ++ tableRows.map(_ + (head -> true)))
      }
    }

    go(variables.sorted.toList, List[BoolTableRow]())
  }

  private def getNF(assignments: Seq[BoolTableRow], takeTrues: Boolean, innerF: (BoolNode, BoolNode) => BoolNode, outerF: (BoolNode, BoolNode) => BoolNode): BoolNode =
    assignments
      .toList
      .filter(takeTrues == _.apply(SolVariable)) match {
      case Nil  => constant(!takeTrues)
      case rows =>
        val innerTerms = rows.map(as => as.variables map (v => if (as(v)) v else not(v)) reduceLeft innerF)
        innerTerms reduceLeft outerF
    }

  def disjunktiveNormalForm(assignments: Seq[BoolTableRow]): BoolNode = getNF(assignments, takeTrues = true, AndBoolNode, OrBoolNode)

  def konjunktiveNormalForm(assignments: Seq[BoolTableRow]): BoolNode = getNF(assignments, takeTrues = false, OrBoolNode, AndBoolNode)

}
