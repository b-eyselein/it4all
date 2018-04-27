package model.sql.matcher

import model.core.matching.{Match, Matcher}
import net.sf.jsqlparser.schema.Table
import play.api.libs.json.{JsString, JsValue}

case class TableMatch(userArg: Option[Table], sampleArg: Option[Table]) extends Match[Table] {

  override protected def descArgForJson(arg: Table): JsValue = JsString(arg.toString)

}

object TableMatcher extends Matcher[Table, TableMatch] {

  override protected def canMatch: (Table, Table) => Boolean = _.getName == _.getName


  override protected def matchInstantiation: (Option[Table], Option[Table]) => TableMatch = TableMatch

}
