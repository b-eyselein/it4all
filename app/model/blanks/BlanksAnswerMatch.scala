package model.blanks

import model.core.Levenshtein.levenshteinDistance
import model.core.matching.{Match, MatchType}
import play.api.libs.json.JsValue


case class BlanksAnswerMatch(userArg: Option[BlanksAnswer], sampleArg: Option[BlanksAnswer]) extends Match[BlanksAnswer] {

  override def analyze(arg1: BlanksAnswer, arg2: BlanksAnswer): MatchType = {
    val maxPartialDist = Math.max(Math.min(arg1.solution.length, arg2.solution.length) / 10, 1)

    levenshteinDistance(arg1.solution, arg2.solution) match {
      case 0                        => MatchType.SUCCESSFUL_MATCH
      case x if x <= maxPartialDist => MatchType.PARTIAL_MATCH
      case _                        => MatchType.UNSUCCESSFUL_MATCH
    }
  }

  //  override protected def descArg(arg: BlanksAnswer): String = arg.solution

  override protected def descArgForJson(arg: BlanksAnswer): JsValue = ???

}
