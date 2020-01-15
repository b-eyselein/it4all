package model.tools.collectionTools.sql.matcher

import model.core.matching.{GenericAnalysisResult, Match, MatchType, Matcher}
import net.sf.jsqlparser.expression.operators.relational.ExpressionList


final case class ExpressionListMatch(
  userArg: Option[ExpressionList],
  sampleArg: Option[ExpressionList],
  analysisResult: GenericAnalysisResult
) extends Match[ExpressionList, GenericAnalysisResult] {

  override val maybeAnalysisResult: Option[GenericAnalysisResult] = Some(analysisResult)

}

object ExpressionListMatcher extends Matcher[ExpressionList, GenericAnalysisResult, ExpressionListMatch] {

  override protected val matchName: String = "Bedingungen"

  override protected val matchSingularName: String = "der Bedingung"

  override protected def canMatch(el1: ExpressionList, el2: ExpressionList): Boolean = el1.toString == el2.toString

  override protected def instantiateOnlySampleMatch(sa: ExpressionList): ExpressionListMatch =
    ExpressionListMatch(None, Some(sa), GenericAnalysisResult(MatchType.ONLY_SAMPLE))

  override protected def instantiateOnlyUserMatch(ua: ExpressionList): ExpressionListMatch =
    ExpressionListMatch(Some(ua), None, GenericAnalysisResult(MatchType.ONLY_USER))

  override protected def instantiateCompleteMatch(ua: ExpressionList, sa: ExpressionList): ExpressionListMatch =
    ExpressionListMatch(Some(ua), Some(sa), GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH))
}
