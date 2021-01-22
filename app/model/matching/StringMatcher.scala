package model.matching

final case class StringMatch(
  matchType: MatchType,
  userArg: Option[String],
  sampleArg: Option[String]
) extends Match[String]

object StringMatcher extends Matcher[String, StringMatch] {

  type StringMatchingResult = MatchingResult[String, StringMatch]

  override protected def instantiateOnlyUserMatch(ua: String): StringMatch =
    StringMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateOnlySampleMatch(sa: String): StringMatch =
    StringMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateCompleteMatch(ua: String, sa: String): StringMatch =
    StringMatch(MatchType.SUCCESSFUL_MATCH, Some(ua), Some(sa))

}
