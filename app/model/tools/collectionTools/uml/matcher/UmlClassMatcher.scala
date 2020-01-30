package model.tools.collectionTools.uml.matcher

import model.core.matching._
import model.tools.collectionTools.uml.UmlToolMain.{AttributeComparison, MethodComparison}
import model.tools.collectionTools.uml.{UmlClass, UmlClassType}

final case class UmlClassMatchAnalysisResult(
  classTypeCorrect: Boolean,
  correctClassType: UmlClassType,
  maybeAttributeMatchingResult: Option[AttributeComparison],
  maybeMethodMatchingResult: Option[MethodComparison]
)


final case class UmlClassMatch(
  matchType: MatchType,
  userArg: Option[UmlClass],
  sampleArg: Option[UmlClass],
  compAM: Boolean,
  analysisResult: Option[UmlClassMatchAnalysisResult]
) extends Match[UmlClass]


final case class UmlClassMatcher(
  compareAttrsAndMethods: Boolean
) extends Matcher[UmlClass, UmlClassMatch] {

  override protected val matchName: String = "Klassen"

  override protected val matchSingularName: String = "der Klasse"

  override protected def canMatch(c1: UmlClass, c2: UmlClass): Boolean = c1.name == c2.name

  override protected def instantiateOnlySampleMatch(sa: UmlClass): UmlClassMatch =
    UmlClassMatch(MatchType.ONLY_SAMPLE, None, Some(sa), compareAttrsAndMethods, None)

  override protected def instantiateOnlyUserMatch(ua: UmlClass): UmlClassMatch =
    UmlClassMatch(MatchType.ONLY_USER, Some(ua), None, compareAttrsAndMethods, None)

  override protected def instantiateCompleteMatch(ua: UmlClass, sa: UmlClass): UmlClassMatch = {
    val classTypeCorrect = ua.classType == sa.classType

    val attributesResult = UmlAttributeMatcher.doMatch(ua.attributes, sa.attributes)

    val methodsResult = UmlMethodMatcher.doMatch(ua.methods, sa.methods)

    val membersCorrect: Boolean = false // TODO: attributesResult.success == SuccessType.COMPLETE && methodsResult.success == SuccessType.COMPLETE

    val matchType: MatchType = (classTypeCorrect, membersCorrect) match {
      case (true, true)  => MatchType.SUCCESSFUL_MATCH
      case (false, true) => MatchType.PARTIAL_MATCH
      case _             => MatchType.UNSUCCESSFUL_MATCH
    }

    val ar = UmlClassMatchAnalysisResult(classTypeCorrect, ua.classType, Some(attributesResult), Some(methodsResult))

    UmlClassMatch(matchType, Some(ua), Some(sa), compareAttrsAndMethods, Some(ar))

  }
}
