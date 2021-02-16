package model.tools.uml.matcher

import model.matching._
import model.tools.uml.{UmlAttribute, UmlClassMember, UmlMethod, UmlVisibility}

sealed trait UmlClassMemberMatch[Mem <: UmlClassMember] extends Match[Mem]

sealed trait UmlClassMemberAnalysisResult {

  val visibilityComparison: Boolean
  val correctVisibility: UmlVisibility

  val typeComparison: Boolean
  val correctType: String

}

trait UmlClassMemberMatcher[CM <: UmlClassMember, M <: Match[CM]] extends Matcher[CM, M] {

  override protected def canMatch(a1: CM, a2: CM): Boolean = a1.memberName == a2.memberName

}

// Uml Attribute

final case class UmlAttributeAnalysisResult(
  visibilityComparison: Boolean,
  correctVisibility: UmlVisibility,
  typeComparison: Boolean,
  correctType: String,
  staticCorrect: Boolean,
  correctStatic: Boolean,
  derivedCorrect: Boolean,
  correctDerived: Boolean,
  abstractCorrect: Boolean,
  correctAbstract: Boolean
) extends UmlClassMemberAnalysisResult

final case class UmlAttributeMatch(
  matchType: MatchType,
  userArg: UmlAttribute,
  sampleArg: UmlAttribute,
  maybeAnalysisResult: UmlAttributeAnalysisResult
) extends UmlClassMemberMatch[UmlAttribute]

object UmlAttributeMatcher extends UmlClassMemberMatcher[UmlAttribute, UmlAttributeMatch] {

  override protected def instantiateMatch(ua: UmlAttribute, sa: UmlAttribute): UmlAttributeMatch = {

    // Compare visibility
    val visibilityComparison = ua.visibility == sa.visibility

    // Return type
    val returnTypeComparison = ua.memberType == sa.memberType

    // Modificator comparison
    val isStaticComparison   = ua.isStatic == sa.isStatic
    val isDerivedComparison  = ua.isDerived == sa.isDerived
    val isAbstractComparison = ua.isAbstract == sa.isAbstract

    val matchType: MatchType = if (returnTypeComparison) {
      if (visibilityComparison && isStaticComparison && isDerivedComparison && isAbstractComparison) {
        MatchType.SUCCESSFUL_MATCH
      } else {
        MatchType.PARTIAL_MATCH
      }
    } else {
      MatchType.UNSUCCESSFUL_MATCH
    }

    val ar = UmlAttributeAnalysisResult(
      visibilityComparison,
      sa.visibility,
      returnTypeComparison,
      sa.memberType,
      isStaticComparison,
      sa.isStatic,
      isDerivedComparison,
      sa.isDerived,
      isAbstractComparison,
      sa.isAbstract
    )

    UmlAttributeMatch(matchType, ua, sa, ar)
  }
}

final case class UmlMethodAnalysisResult(
  visibilityComparison: Boolean,
  correctVisibility: UmlVisibility,
  typeComparison: Boolean,
  correctType: String,
  parameterComparison: Boolean,
  correctParameters: String,
  staticCorrect: Boolean,
  correctStatic: Boolean,
  abstractCorrect: Boolean,
  correctAbstract: Boolean
) extends UmlClassMemberAnalysisResult

final case class UmlMethodMatch(
  matchType: MatchType,
  userArg: UmlMethod,
  sampleArg: UmlMethod,
  maybeAnalysisResult: UmlMethodAnalysisResult
) extends UmlClassMemberMatch[UmlMethod]

object UmlMethodMatcher extends UmlClassMemberMatcher[UmlMethod, UmlMethodMatch] {

  override protected def instantiateMatch(ua: UmlMethod, sa: UmlMethod): UmlMethodMatch = {

    // Compare visibility
    val visibilityComparison = ua.visibility == sa.visibility

    // Return type
    val returnTypeComparison = ua.memberType == sa.memberType

    val parameterComparison = ua.parameters == sa.parameters

    // Modificator comparison
    val isStaticComparison   = ua.isStatic == sa.isStatic
    val isAbstractComparison = ua.isAbstract == sa.isAbstract

    val matchType: MatchType = if (returnTypeComparison) {
      if (visibilityComparison && isStaticComparison && parameterComparison && isAbstractComparison) {
        MatchType.SUCCESSFUL_MATCH
      } else {
        MatchType.PARTIAL_MATCH
      }
    } else {
      MatchType.UNSUCCESSFUL_MATCH
    }

    val ar = UmlMethodAnalysisResult(
      visibilityComparison,
      sa.visibility,
      returnTypeComparison,
      sa.memberType,
      parameterComparison,
      sa.parameters,
      isStaticComparison,
      sa.isStatic,
      isAbstractComparison,
      sa.isAbstract
    )

    UmlMethodMatch(matchType, ua, sa, ar)
  }

}
