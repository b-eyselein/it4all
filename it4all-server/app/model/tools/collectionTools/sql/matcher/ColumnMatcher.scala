package model.tools.collectionTools.sql.matcher

import model.core.matching._
import model.points._
import model.tools.collectionTools.sql.ColumnWrapper

final case class ColumnMatch(
  userArg: Option[ColumnWrapper],
  sampleArg: Option[ColumnWrapper],
  analysisResult: GenericAnalysisResult
) extends Match[ColumnWrapper, GenericAnalysisResult] {

  override val maybeAnalysisResult: Option[GenericAnalysisResult] = Some(analysisResult)

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

  override protected def instantiateOnlySampleMatch(sa: ColumnWrapper): ColumnMatch =
    ColumnMatch(None, Some(sa), GenericAnalysisResult(MatchType.ONLY_SAMPLE))

  override protected def instantiateOnlyUserMatch(ua: ColumnWrapper): ColumnMatch =
    ColumnMatch(Some(ua), None, GenericAnalysisResult(MatchType.ONLY_USER))

  override protected def instantiateCompleteMatch(ua: ColumnWrapper, sa: ColumnWrapper): ColumnMatch = {

    println(ua)
    println(sa)

    val mt: MatchType = ua doMatch sa

    println(mt)

    ColumnMatch(Some(ua), Some(sa), GenericAnalysisResult(mt))
  }
}
