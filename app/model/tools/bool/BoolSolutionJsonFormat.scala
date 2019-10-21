package model.tools.bool

import play.api.libs.json._

final case class BoolSolution(formula: String, rows: Seq[BoolTableRow])


private final case class JsonBoolSolution(formula: String, rows: Seq[JsonBoolTableRow])

private final case class JsonBoolTableRow(assignments: Seq[JsonBoolAssignment])

private final case class JsonBoolAssignment(variable: Variable, value: Boolean)


object BoolSolutionJsonFormat {

  private def seqToMap(assignments: Seq[JsonBoolAssignment]): Map[Variable, Boolean] = assignments
    .map { case JsonBoolAssignment(variable, value) => (variable, value) }
    .toMap

  private def convertSolution(jbs: JsonBoolSolution): BoolSolution =
    BoolSolution(jbs.formula, jbs.rows.map((jbstr: JsonBoolTableRow) => BoolTableRow(seqToMap(jbstr.assignments))))

  private val jsonBoolSolutionReads: Reads[JsonBoolSolution] = {

    implicit val vr  : Reads[Variable]           = {
      case JsString(value) => JsSuccess(Variable(value(0)))
      case _               => ???
    }
    implicit val jvar: Reads[JsonBoolAssignment] = Json.reads[JsonBoolAssignment]

    implicit val jbtrr: Reads[JsonBoolTableRow] = Json.reads[JsonBoolTableRow]

    Json.reads[JsonBoolSolution]
  }

  val boolSolutionReads: Reads[BoolSolution] = jsonBoolSolutionReads.map(convertSolution)

}
