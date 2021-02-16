package model.tools.uml.matcher

import model.matching._
import model.tools.uml.UmlTool.{AttributeComparison, MethodComparison}
import model.tools.uml.{UmlClass, UmlClassType}

final case class UmlClassMatchAnalysisResult(
  classTypeCorrect: Boolean,
  correctClassType: UmlClassType,
  maybeAttributeMatchingResult: Option[AttributeComparison],
  maybeMethodMatchingResult: Option[MethodComparison]
)

final case class UmlClassMatch(
  matchType: MatchType,
  userArg: UmlClass,
  sampleArg: UmlClass,
  compAM: Boolean,
  analysisResult: UmlClassMatchAnalysisResult
) extends Match[UmlClass]

final case class UmlClassMatcher(
  compareAttrsAndMethods: Boolean
) extends Matcher[UmlClass, UmlClassMatch] {

  override protected def canMatch(c1: UmlClass, c2: UmlClass): Boolean = c1.name == c2.name

  override protected def instantiateMatch(ua: UmlClass, sa: UmlClass): UmlClassMatch = {
    val classTypeCorrect = ua.classType == sa.classType

    val attributesResult = UmlAttributeMatcher.doMatch(ua.attributes, sa.attributes)

    val methodsResult = UmlMethodMatcher.doMatch(ua.methods, sa.methods)

    val membersCorrect: Boolean =
      false // TODO: attributesResult.success == SuccessType.COMPLETE && methodsResult.success == SuccessType.COMPLETE

    val matchType: MatchType = if (classTypeCorrect && membersCorrect) {
      MatchType.SUCCESSFUL_MATCH
    } else {
      MatchType.PARTIAL_MATCH
    }

    val ar = UmlClassMatchAnalysisResult(classTypeCorrect, ua.classType, Some(attributesResult), Some(methodsResult))

    UmlClassMatch(matchType, ua, sa, compareAttrsAndMethods, ar)
  }
}
