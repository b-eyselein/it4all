package model.matching

import model.points._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final case class IntMatch(matchType: MatchType, userArg: Int, sampleArg: Int) extends Match[Int]

object IntMatcher extends Matcher[Int, Match[Int]] {

  override protected def instantiateMatch(ua: Int, sa: Int): Match[Int] = IntMatch(MatchType.SUCCESSFUL_MATCH, ua, sa)

}

class MatcherTest extends AnyFlatSpec with Matchers {

  behavior of "Matcher"

  it should ("match empty lists") in {

    val result: MatchingResult[Int, Match[Int]] = IntMatcher.doMatch(Seq.empty, Seq(1))

    result shouldBe MatchingResult[Int, IntMatch](
      allMatches = Seq.empty,
      notMatchedForUser = Seq.empty,
      notMatchedForSample = Seq(1),
      points = (-1).points,
      maxPoints = (-1).points
    )
  }

}
