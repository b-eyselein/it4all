package model.tools.bool

import play.api.libs.json._
import play.api.libs.functional.syntax._

import model.tools.bool.BoolConsts._

final case class BoolSolution(formula: String, assignments: Seq[BoolTableRow])

final case class BoolAssignment(variable: Variable, value: Boolean)

//noinspection ConvertibleToMethodValue
object BoolSolutionJsonFormat {


  private implicit val boolAssignmentReads: Reads[BoolAssignment] = (
    (__ \ variableName).read[String] and
      (__ \ valueName).read[Boolean]
    ) { (varStr, value) => BoolAssignment.apply(Variable(varStr(0)), value) }

  private implicit val boolTableRowReads: Reads[BoolTableRow] = (JsPath \ assignmentsName).read[Seq[BoolAssignment]] map BoolTableRow.fromAssignments

  val boolSolutionReads: Reads[BoolSolution] = (
    (__ \ formulaName).read[String] and
      (__ \ tableRowsName).read[Seq[BoolTableRow]]
    ) (BoolSolution.apply(_, _))


}
