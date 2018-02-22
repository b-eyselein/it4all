package model.sql

import model.core.matching.{Match, Matcher, MatchingResult}
import net.sf.jsqlparser.schema.Table


case class TableMatch(userArg: Option[Table], sampleArg: Option[Table]) extends Match[Table] {

  override def descArg(arg: Table): String = arg.getName

}

object TableMatcher extends Matcher[Table, TableMatch, TableMatchingResult] {

  override def canMatch: (Table, Table) => Boolean = _.getName == _.getName

  override def matchInstantiation: (Option[Table], Option[Table]) => TableMatch = TableMatch

  override def resultInstantiation: Seq[TableMatch] => TableMatchingResult = TableMatchingResult

}

case class TableMatchingResult(allMatches: Seq[TableMatch]) extends MatchingResult[Table, TableMatch] {

  override val matchName: String = "Tabellen"

}