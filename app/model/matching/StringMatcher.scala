package model.matching

final case class StringMatch(matchType: MatchType, userArg: String, sampleArg: String) extends Match[String]

object StringMatcher extends Matcher[String, StringMatch] {

  type StringMatchingResult = MatchingResult[String, StringMatch]

  override protected def instantiateMatch(ua: String, sa: String): StringMatch =
    StringMatch(MatchType.SUCCESSFUL_MATCH, ua, sa)

}
