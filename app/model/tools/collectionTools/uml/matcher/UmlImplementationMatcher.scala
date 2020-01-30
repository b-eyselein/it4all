package model.tools.collectionTools.uml.matcher

import model.core.matching._
import model.tools.collectionTools.uml.UmlImplementation

final case class UmlImplementationMatch(
  matchType: MatchType,
  userArg: Option[UmlImplementation],
  sampleArg: Option[UmlImplementation]
) extends Match[UmlImplementation]


object UmlImplementationMatcher extends Matcher[UmlImplementation, UmlImplementationMatch] {

  override protected val matchName: String = "Vererbungen"

  override protected val matchSingularName: String = "der Vererbung"

  override protected def canMatch(i1: UmlImplementation, i2: UmlImplementation): Boolean =
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)

  override protected def instantiateOnlySampleMatch(sa: UmlImplementation): UmlImplementationMatch =
    UmlImplementationMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: UmlImplementation): UmlImplementationMatch =
    UmlImplementationMatch(MatchType.ONLY_USER, Some(ua), None)


  override protected def instantiateCompleteMatch(ua: UmlImplementation, sa: UmlImplementation): UmlImplementationMatch = {
    val matchType = if (ua.subClass == sa.subClass && ua.superClass == sa.superClass) {
      MatchType.SUCCESSFUL_MATCH
    } else {
      MatchType.PARTIAL_MATCH
    }

    UmlImplementationMatch(matchType, Some(ua), Some(sa))
  }
}
