package model.blanks

import model.core.matching.{GenericAnalysisResult, Matcher, MatchingResult}
import model.core.result.CompleteResult
import play.api.libs.json.JsValue

case class BlanksCompleteResult(learnerSolution: Seq[BlanksAnswer], result: MatchingResult[BlanksAnswer, GenericAnalysisResult, BlanksAnswerMatch])
  extends CompleteResult[MatchingResult[BlanksAnswer, GenericAnalysisResult, BlanksAnswerMatch]] {

  override type SolType = Seq[BlanksAnswer]

  override def results: Seq[MatchingResult[BlanksAnswer, GenericAnalysisResult, BlanksAnswerMatch]] = Seq(result)

  override def toJson(saved: Boolean): JsValue = ???

}

object BlanksCorrector extends Matcher[BlanksAnswer, GenericAnalysisResult, BlanksAnswerMatch] {

  override protected def canMatch: (BlanksAnswer, BlanksAnswer) => Boolean = _.id == _.id


  override protected def matchInstantiation: (Option[BlanksAnswer], Option[BlanksAnswer]) => BlanksAnswerMatch = BlanksAnswerMatch

}