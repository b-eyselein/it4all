package model.tools.sql.matcher

import model.core.matching._
import model.points._
import model.tools.sql.ColumnWrapper
import play.api.libs.json.{JsString, JsValue}

import scala.language.postfixOps


final case class ColumnMatch(userArg: Option[ColumnWrapper], sampleArg: Option[ColumnWrapper]) extends Match {

  override type T = ColumnWrapper

  override type AR = GenericAnalysisResult

  override protected def analyze(userArg: ColumnWrapper, sampleArg: ColumnWrapper): GenericAnalysisResult =
    GenericAnalysisResult(userArg doMatch sampleArg)

  override protected def descArgForJson(arg: ColumnWrapper): JsValue = JsString(arg.toString)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) 1 halfPoint else 0 points

  override def maxPoints: Points = sampleArg match {
    case None    => 0 points
    case Some(_) => 1 halfPoint
  }

}

object ColumnMatcher extends Matcher[ColumnMatch] {

  override type T = ColumnWrapper

  override protected val matchName: String = "Spalten"

  override protected val matchSingularName: String = "der Spalte"

  override protected def canMatch(cw1: ColumnWrapper, cw2: ColumnWrapper): Boolean = cw1 canMatch cw2

  override protected def matchInstantiation(ua: Option[ColumnWrapper], sa: Option[ColumnWrapper]): ColumnMatch =
    ColumnMatch(ua, sa)

}
