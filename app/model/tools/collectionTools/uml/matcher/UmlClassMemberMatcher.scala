package model.tools.collectionTools.uml.matcher

import model.core.matching._
import model.tools.collectionTools.uml.{UmlAttribute, UmlClassMember, UmlMethod, UmlVisibility}

sealed trait UmlClassMemberMatch[Mem <: UmlClassMember] extends Match[Mem]

sealed trait UmlClassMemberAnalysisResult {

  val visibilityComparison: Boolean
  val correctVisibility: UmlVisibility

  val typeComparison: Boolean
  val correctType: String

}

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
  userArg: Option[UmlAttribute],
  sampleArg: Option[UmlAttribute],
  maybeAnalysisResult: Option[UmlAttributeAnalysisResult]
) extends UmlClassMemberMatch[UmlAttribute]

object UmlAttributeMatcher extends Matcher[UmlAttribute, UmlAttributeMatch] {

  override protected val matchName: String = "Attribute"

  override protected val matchSingularName: String = "des Attributs"

  override protected def canMatch(a1: UmlAttribute, a2: UmlAttribute): Boolean = a1.memberName == a2.memberName

  override protected def instantiateOnlySampleMatch(sa: UmlAttribute): UmlAttributeMatch =
    UmlAttributeMatch(MatchType.ONLY_SAMPLE, None, Some(sa), None)

  override protected def instantiateOnlyUserMatch(ua: UmlAttribute): UmlAttributeMatch =
    UmlAttributeMatch(MatchType.ONLY_USER, Some(ua), None, None)

  override protected def instantiateCompleteMatch(ua: UmlAttribute, sa: UmlAttribute): UmlAttributeMatch = {

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

    UmlAttributeMatch(matchType, Some(ua), Some(sa), Some(ar))
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
  userArg: Option[UmlMethod],
  sampleArg: Option[UmlMethod],
  maybeAnalysisResult: Option[UmlMethodAnalysisResult]
) extends UmlClassMemberMatch[UmlMethod]

object UmlMethodMatcher extends Matcher[UmlMethod, UmlMethodMatch] {

  override protected val matchName: String = "Methoden"

  override protected val matchSingularName: String = "der Methode"

  override protected def canMatch(m1: UmlMethod, m2: UmlMethod): Boolean = m1.memberName == m2.memberName

  override protected def instantiateOnlySampleMatch(sa: UmlMethod): UmlMethodMatch =
    UmlMethodMatch(MatchType.ONLY_SAMPLE, None, Some(sa), None)

  override protected def instantiateOnlyUserMatch(ua: UmlMethod): UmlMethodMatch =
    UmlMethodMatch(MatchType.ONLY_USER, Some(ua), None, None)

  override protected def instantiateCompleteMatch(ua: UmlMethod, sa: UmlMethod): UmlMethodMatch = {

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

    UmlMethodMatch(matchType, Some(ua), Some(sa), Some(ar))
  }

}
