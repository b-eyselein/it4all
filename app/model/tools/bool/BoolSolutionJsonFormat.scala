package model.tools.bool

import play.api.libs.json._
import play.api.libs.functional.syntax._

import model.tools.bool.BoolConsts._

final case class BoolSolution(formula: String, assignments: Seq[BoolTableRow])

final case class BoolAssignment(variable: Variable, value: Boolean)

object BoolSolutionJsonFormat {

  private val boolAssignmentReads: Reads[BoolAssignment] = (
    (__ \ variableName).read[String] and
      (__ \ valueName).read[Boolean]
    ) { (varStr, value) => BoolAssignment.apply(Variable(varStr(0)), value) }

  private val boolTableRowReads: Reads[BoolTableRow] = {
    implicit val bar: Reads[BoolAssignment] = boolAssignmentReads

    (JsPath \ assignmentsName).read[Seq[BoolAssignment]].map(BoolTableRow.fromAssignments)
  }

  val boolSolutionReads: Reads[BoolSolution] = {
    implicit val btrr: Reads[BoolTableRow] = boolTableRowReads

    (
      (__ \ formulaName).read[String] and
        (__ \ tableRowsName).read[Seq[BoolTableRow]]
      ) (BoolSolution.apply(_, _))
  }


}
