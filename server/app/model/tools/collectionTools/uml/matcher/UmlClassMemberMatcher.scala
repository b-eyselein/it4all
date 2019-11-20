package model.tools.collectionTools.uml.matcher

import model.core.matching._
import model.tools.collectionTools.uml.UmlConsts._
import model.tools.collectionTools.uml.{UmlAttribute, UmlClassMember, UmlMethod, UmlVisibility}
import play.api.libs.json.{JsValue, Json}


sealed trait UmlClassMemberMatch[Mem <: UmlClassMember] extends Match {

  override type T = Mem

  override type AR <: UmlClassMemberAnalysisResult

}

sealed trait UmlClassMemberAnalysisResult extends AnalysisResult {

  val visibilityComparison: Boolean
  val correctVisibility   : UmlVisibility

  val typeComparison: Boolean
  val correctType   : String

}


final case class UmlAttributeAnalysisResult(matchType: MatchType,
                                            visibilityComparison: Boolean, correctVisibility: UmlVisibility,
                                            typeComparison: Boolean, correctType: String,
                                            staticCorrect: Boolean, correctStatic: Boolean,
                                            derivedCorrect: Boolean, correctDerived: Boolean,
                                            abstractCorrect: Boolean, correctAbstract: Boolean) extends UmlClassMemberAnalysisResult {

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    "visibilityCorrect" -> visibilityComparison, "correctVisibility" -> correctVisibility.representant,
    "typeCorrect" -> typeComparison, "correctType" -> correctType,
    "staticCorrect" -> staticCorrect, "correctStatic" -> correctStatic,
    "derivedCorrect" -> derivedCorrect, "correctDerived" -> correctDerived,
    "abstractCorrect" -> abstractCorrect, "correctAbstract" -> correctAbstract
  )

}

final case class UmlAttributeMatch(userArg: Option[UmlAttribute], sampleArg: Option[UmlAttribute]) extends UmlClassMemberMatch[UmlAttribute] {

  override type AR = UmlAttributeAnalysisResult

  override def analyze(arg1: UmlAttribute, arg2: UmlAttribute): UmlAttributeAnalysisResult = {

    // Compare visibility
    val visibilityComparison = arg1.visibility == arg2.visibility

    // Return type
    val returnTypeComparison = arg1.memberType == arg2.memberType

    // Modificator comparison
    val isStaticComparison = arg1.isStatic == arg2.isStatic
    val isDerivedComparison = arg1.isDerived == arg2.isDerived
    val isAbstractComparison = arg1.isAbstract == arg2.isAbstract

    val matchType: MatchType = if (returnTypeComparison) {
      if (visibilityComparison && isStaticComparison && isDerivedComparison && isAbstractComparison) {
        MatchType.SUCCESSFUL_MATCH
      } else {
        MatchType.PARTIAL_MATCH
      }
    } else {
      MatchType.UNSUCCESSFUL_MATCH
    }

    UmlAttributeAnalysisResult(matchType,
      visibilityComparison, arg2.visibility,
      returnTypeComparison, arg2.memberType,
      isStaticComparison, arg2.isStatic,
      isDerivedComparison, arg2.isDerived,
      isAbstractComparison, arg2.isAbstract)
  }

  val visibilityComparison: Boolean = analysisResult.exists(_.visibilityComparison)
  val returnTypeComparison: Boolean = analysisResult.exists(_.typeComparison)
  val isStaticComparison  : Boolean = analysisResult.exists(_.staticCorrect)
  val isDerivedComparison : Boolean = analysisResult.exists(_.derivedCorrect)
  val isAbstractComparison: Boolean = analysisResult.exists(_.abstractCorrect)

  override protected def descArgForJson(arg: UmlAttribute): JsValue = Json.obj(nameName -> arg.memberName, typeName -> arg.memberType)

}

object UmlAttributeMatcher extends Matcher[UmlAttributeMatch] {

  override type T = UmlAttribute

  override protected val matchName: String = "Attribute"

  override protected val matchSingularName: String = "des Attributs"

  override protected def canMatch(a1: UmlAttribute, a2: UmlAttribute): Boolean = a1.memberName == a2.memberName

  override protected def matchInstantiation(ua: Option[UmlAttribute], sa: Option[UmlAttribute]): UmlAttributeMatch =
    UmlAttributeMatch(ua, sa)

}


final case class UmlMethodAnalysisResult(matchType: MatchType,
                                         visibilityComparison: Boolean, correctVisibility: UmlVisibility,
                                         typeComparison: Boolean, correctType: String,
                                         parameterComparison: Boolean, correctParameters: String,
                                         staticCorrect: Boolean, correctStatic: Boolean,
                                         abstractCorrect: Boolean, correctAbstract: Boolean)
  extends UmlClassMemberAnalysisResult {

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    "visibilityCorrect" -> visibilityComparison, "correctVisibility" -> correctVisibility.representant,
    "typeCorrect" -> typeComparison, "correctType" -> correctType,
    "staticCorrect" -> staticCorrect, "correctStatic" -> correctStatic,
    "parametersCorrect" -> parameterComparison, "correctParameters" -> correctParameters,
    "abstractCorrect" -> abstractCorrect, "correctAbstract" -> correctAbstract
  )

}

final case class UmlMethodMatch(userArg: Option[UmlMethod], sampleArg: Option[UmlMethod]) extends UmlClassMemberMatch[UmlMethod] {

  override type AR = UmlMethodAnalysisResult

  override protected def descArgForJson(arg: UmlMethod): JsValue = Json.obj(nameName -> arg.memberName, typeName -> arg.memberType)

  override def analyze(arg1: UmlMethod, arg2: UmlMethod): UmlMethodAnalysisResult = {

    // Compare visibility
    val visibilityComparison = arg1.visibility == arg2.visibility

    // Return type
    val returnTypeComparison = arg1.memberType == arg2.memberType

    val parameterComparison = arg1.parameters == arg2.parameters

    // Modificator comparison
    val isStaticComparison = arg1.isStatic == arg2.isStatic
    val isAbstractComparison = arg1.isAbstract == arg2.isAbstract

    val matchType: MatchType = if (returnTypeComparison) {
      if (visibilityComparison && isStaticComparison && parameterComparison && isAbstractComparison) {
        MatchType.SUCCESSFUL_MATCH
      } else {
        MatchType.PARTIAL_MATCH
      }
    } else {
      MatchType.UNSUCCESSFUL_MATCH
    }

    UmlMethodAnalysisResult(matchType,
      visibilityComparison, arg2.visibility,
      returnTypeComparison, arg2.memberType,
      parameterComparison, arg2.parameters,
      isStaticComparison, arg2.isStatic,
      isAbstractComparison, arg2.isAbstract)
  }

}

object UmlMethodMatcher extends Matcher[UmlMethodMatch] {

  override type T = UmlMethod

  override protected val matchName: String = "Methoden"

  override protected val matchSingularName: String = "der Methode"

  override protected def canMatch(m1: UmlMethod, m2: UmlMethod): Boolean = m1.memberName == m2.memberName

  override protected def matchInstantiation(ua: Option[UmlMethod], sa: Option[UmlMethod]): UmlMethodMatch =
    UmlMethodMatch(ua, sa)

}
