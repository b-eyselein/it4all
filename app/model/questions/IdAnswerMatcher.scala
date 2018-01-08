package model.questions

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.matching.{Match, Matcher, MatchingResult}
import model.questions.QuestionEnums.Correctness._

case class IdAnswerMatch(userArg: Option[IdAnswer], sampleArg: Option[IdAnswer]) extends Match[IdAnswer] {

  def id: Int = userArg map (_.id) getOrElse (sampleArg map (_.id) getOrElse (-1))

  override def analyze(arg1: IdAnswer, arg2: IdAnswer): MatchType = arg2 match {
    case idA: Answer => idA.correctness match {
      case WRONG                => ONLY_USER
      case (OPTIONAL | CORRECT) => SUCCESSFUL_MATCH
    }
    case _           => FAILURE
  }

}

object IdAnswerMatcher extends Matcher[IdAnswer, IdAnswerMatch, IdAnswerMatchingResult] {

  override def canMatch: (IdAnswer, IdAnswer) => Boolean = _.id == _.id

  override def matchInstantiation: (Option[IdAnswer], Option[IdAnswer]) => IdAnswerMatch = IdAnswerMatch

  override def resultInstantiation: Seq[IdAnswerMatch] => IdAnswerMatchingResult = IdAnswerMatchingResult

}

case class IdAnswerMatchingResult(allMatches: Seq[IdAnswerMatch]) extends MatchingResult[IdAnswer, IdAnswerMatch] {

  override val matchName: String = "TODO!"

}