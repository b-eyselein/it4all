package model.matching

import org.junit.Test

class ScalaMatcherTest {
  val TEST_NAME = "TestString"
  val EXPECTED_MATCHES = 4

  val firstList = List("One", "Two", "Three")
  val secondList = List("1", "Two", "Three")

  def checkSize(gotten: Int, expected: Int, name: String) = assert(gotten == expected, s"Expected that there are $expected $name, but there were $gotten!")

  @Test
  def testScalaMatcher {
    val stringMatcher = new ScalaStringMatcher(TEST_NAME)
    val result = stringMatcher.doMatch(firstList, secondList)

    assert(result.matchName == TEST_NAME, s"Expected that match name ${result.matchName} is $TEST_NAME")
    assert(result.allMatches.size == EXPECTED_MATCHES, s"Expected that there are $EXPECTED_MATCHES matches but there were ${result.allMatches.size}")

    val matchesMap: Map[MatchType, Int] = result.allMatches.groupBy(_.matchType).map(group => (group._1, group._2.size))

    assert(!matchesMap.isDefinedAt(MatchType.FAILURE))
    assert(!matchesMap.isDefinedAt(MatchType.UNSUCCESSFUL_MATCH))

    checkSize(matchesMap(MatchType.ONLY_SAMPLE), 1, "matches only in user list")
    checkSize(matchesMap(MatchType.ONLY_USER), 1, "matches only in sample list")
    checkSize(matchesMap(MatchType.SUCCESSFUL_MATCH), 2, "successful matches")
  }

}