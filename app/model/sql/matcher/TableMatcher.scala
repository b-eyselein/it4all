package model.sql.matcher

import model._
import model.core.matching._
import net.sf.jsqlparser.schema.Table
import play.api.libs.json.{JsString, JsValue}

import scala.language.postfixOps

final case class TableMatch(userArg: Option[Table], sampleArg: Option[Table]) extends Match {

  override type T = Table

  override type AR = GenericAnalysisResult

  override protected def descArgForJson(arg: Table): JsValue = JsString(arg.toString)

  override protected def analyze(arg1: Table, arg2: Table): GenericAnalysisResult = GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) 1 halfPoint else 0 points

  override def maxPoints: Points = sampleArg match {
    case None    => 0 points
    case Some(_) => 1 halfPoint
  }

}

object TableMatcher extends Matcher[TableMatch] {

  override type T = Table

  override protected val matchName: String = "Tabellen"

  override protected val matchSingularName: String = "der Tabelle"

  override protected def canMatch(t1: Table, t2: Table): Boolean = t1.getName == t2.getName

  override protected def matchInstantiation(ua: Option[Table], sa: Option[Table]): TableMatch = TableMatch(ua, sa)

}
