package model.tools.collectionTools.sql.matcher

import model.core.matching._
import model.points._
import model.tools.collectionTools.sql.ColumnWrapper
import play.api.libs.json.{JsString, JsValue}

final case class ColumnMatch(
  userArg: Option[ColumnWrapper],
  sampleArg: Option[ColumnWrapper],
  analysisResult: Option[GenericAnalysisResult]
) extends Match[ColumnWrapper, GenericAnalysisResult] {

  /*
  override protected def analyze(userArg: ColumnWrapper, sampleArg: ColumnWrapper): GenericAnalysisResult =
    GenericAnalysisResult(userArg doMatch sampleArg)

   */

  override protected def descArgForJson(arg: ColumnWrapper): JsValue = JsString(arg.toString)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singleHalfPoint
  }

}

object ColumnMatcher extends Matcher[ColumnWrapper, GenericAnalysisResult, ColumnMatch] {

  override protected val matchName: String = "Spalten"

  override protected val matchSingularName: String = "der Spalte"

  override protected def canMatch(cw1: ColumnWrapper, cw2: ColumnWrapper): Boolean = cw1 canMatch cw2

  override protected def instantiatePartMatch(ua: Option[ColumnWrapper], sa: Option[ColumnWrapper]): ColumnMatch =
    ColumnMatch(ua, sa, None)

  override protected def instantiateCompleteMatch(ua: ColumnWrapper, sa: ColumnWrapper): ColumnMatch = {
    ColumnMatch(Some(ua), Some(sa), Some(GenericAnalysisResult(ua doMatch sa)))
  }
}
