package model.tools.sql.matcher

import model._
import model.core.matching._
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column
import play.api.libs.json.{JsString, JsValue}

import scala.language.postfixOps


final case class GroupByMatch(userArg: Option[Expression], sampleArg: Option[Expression]) extends Match {

  override type T = Expression

  override type AR = GenericAnalysisResult

  override def analyze(ua: Expression, sa: Expression): GenericAnalysisResult = GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)

  override protected def descArgForJson(arg: Expression): JsValue = JsString(arg.toString)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) 1 halfPoint else 0 points

  override def maxPoints: Points = sampleArg match {
    case None    => 0 points
    case Some(_) => 1 halfPoint
  }

}


object GroupByMatcher extends Matcher[GroupByMatch] {

  override type T = Expression

  override protected val matchName: String = "Group Bys"

  override protected val matchSingularName: String = "des Group By-Statement"

  override protected def canMatch(e1: Expression, e2: Expression): Boolean = e1 match {
    case column1: Column => e2 match {
      case column2: Column => column1.getColumnName == column2.getColumnName
      case _               => false
    }
    case _               => false
  }


  override protected def matchInstantiation(ua: Option[Expression], sa: Option[Expression]): GroupByMatch =
    GroupByMatch(ua, sa)

}
