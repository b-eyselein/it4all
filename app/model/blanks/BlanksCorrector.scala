package model.blanks

import model.Enums.MatchType
import model.core.CompleteResult
import model.core.Levenshtein.levenshteinDistance
import model.core.matching.{Match, Matcher, MatchingResult}
import play.twirl.api.Html

case class BlanksCompleteResult(learnerSolution: Seq[BlanksAnswer], result: BlanksAnswerMatchingResult) extends CompleteResult[BlanksAnswerMatchingResult] {

  override type SolType = Seq[BlanksAnswer]

  override val solutionSaved = false

  override def results: Seq[BlanksAnswerMatchingResult] = Seq(result)

  override def renderLearnerSolution: Html = ???

}

case class BlanksAnswerMatch(userArg: Option[BlanksAnswer], sampleArg: Option[BlanksAnswer]) extends Match[BlanksAnswer] {

  override def analyze(arg1: BlanksAnswer, arg2: BlanksAnswer): MatchType = {
    val maxPartialDist = Math.max(Math.min(arg1.solution.length, arg2.solution.length) / 10, 1)

    levenshteinDistance(arg1.solution, arg2.solution) match {
      case 0                        => MatchType.SUCCESSFUL_MATCH
      case x if x <= maxPartialDist => MatchType.PARTIAL_MATCH
      case _                        => MatchType.UNSUCCESSFUL_MATCH
    }
  }

  override protected def descArg(arg: BlanksAnswer): String = arg.solution

}

object BlanksCorrector extends Matcher[BlanksAnswer, BlanksAnswerMatch, BlanksAnswerMatchingResult] {

  override def canMatch: (BlanksAnswer, BlanksAnswer) => Boolean = _.id == _.id

  override def matchInstantiation: (Option[BlanksAnswer], Option[BlanksAnswer]) => BlanksAnswerMatch = BlanksAnswerMatch

  override def resultInstantiation: Seq[BlanksAnswerMatch] => BlanksAnswerMatchingResult = BlanksAnswerMatchingResult

}

case class BlanksAnswerMatchingResult(allMatches: Seq[BlanksAnswerMatch]) extends MatchingResult[BlanksAnswer, BlanksAnswerMatch] {

  override val matchName: String = "gegebene Antworten"

}