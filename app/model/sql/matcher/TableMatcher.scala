package model.sql.matcher

import model.core.matching._
import net.sf.jsqlparser.schema.Table
import play.api.libs.json.{JsString, JsValue}

case class TableMatch(userArg: Option[Table], sampleArg: Option[Table]) extends Match[Table] {

  override type AR = GenericAnalysisResult

  override protected def descArgForJson(arg: Table): JsValue = JsString(arg.toString)

  override protected def analyze(arg1: Table, arg2: Table): GenericAnalysisResult = GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)
}

object TableMatcher extends Matcher[Table, GenericAnalysisResult, TableMatch] {

  override protected def canMatch: (Table, Table) => Boolean = _.getName == _.getName

  override protected def matchInstantiation: (Option[Table], Option[Table]) => TableMatch = TableMatch

}
