package model.tools.uml.matcher

import model.matching._
import model.tools.uml.UmlImplementation

final case class UmlImplementationMatch(
  matchType: MatchType,
  userArg: UmlImplementation,
  sampleArg: UmlImplementation
) extends Match[UmlImplementation]

object UmlImplementationMatcher extends Matcher[UmlImplementation, UmlImplementationMatch] {

  override protected def canMatch(i1: UmlImplementation, i2: UmlImplementation): Boolean =
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)

  override protected def instantiateMatch(ua: UmlImplementation, sa: UmlImplementation): UmlImplementationMatch = {

    val matchType = if (ua.subClass == sa.subClass && ua.superClass == sa.superClass) { MatchType.SUCCESSFUL_MATCH }
    else { MatchType.PARTIAL_MATCH }

    UmlImplementationMatch(matchType, ua, sa)
  }
}
