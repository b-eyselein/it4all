package model.uml.matcher

import model.core.matching._
import model.uml.UmlConsts._
import model.uml.{UmlAttribute, UmlClassMember, UmlMethod, UmlVisibility}
import play.api.libs.json.{JsValue, Json}


sealed trait UmlClassMemberMatch[Mem <: UmlClassMember, AR <: UmlClassMemberAnalysisResult] extends Match[Mem, AR]

sealed trait UmlClassMemberAnalysisResult extends AnalysisResult {

  val visibilityComparison: Boolean
  val correctVisibility   : UmlVisibility

  val typeComparison: Boolean
  val correctType   : String

}


case class UmlAttributeAnalysisResult(matchType: MatchType,
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

case class UmlAttributeMatch(userArg: Option[UmlAttribute], sampleArg: Option[UmlAttribute]) extends UmlClassMemberMatch[UmlAttribute, UmlAttributeAnalysisResult] {

  //  override type MatchAnalysisResult = UmlAttributeAnalysisResult

  override def analyze(arg1: UmlAttribute, arg2: UmlAttribute): UmlAttributeAnalysisResult = {

    // Compare visibility
    val visibilityComparison = arg1.visibility == arg2.visibility

    // Return type
    val returnTypeComparison = arg1.memberType == arg2.memberType

    // Modificator comparison
    val isStaticComparison = arg1.isStatic == arg2.isStatic
    val isDerivedComparison = arg1.isDerived == arg2.isDerived
    val isAbstractComparison = arg1.isAbstract == arg2.isAbstract

    val matchType = if (returnTypeComparison) {
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

object UmlAttributeMatcher extends Matcher[UmlAttribute, UmlAttributeAnalysisResult, UmlAttributeMatch] {

  override protected def canMatch: (UmlAttribute, UmlAttribute) => Boolean = _.memberName == _.memberName

  override protected def matchInstantiation: (Option[UmlAttribute], Option[UmlAttribute]) => UmlAttributeMatch = UmlAttributeMatch

}


case class UmlMethodAnalysisResult(matchType: MatchType,
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

case class UmlMethodMatch(userArg: Option[UmlMethod], sampleArg: Option[UmlMethod]) extends UmlClassMemberMatch[UmlMethod, UmlMethodAnalysisResult] {

  //  override type MatchAnalysisResult = UmlMethodAnalysisResult

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

    val matchType = if (returnTypeComparison) {
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

object UmlMethodMatcher extends Matcher[UmlMethod, UmlMethodAnalysisResult, UmlMethodMatch] {

  override protected def canMatch: (UmlMethod, UmlMethod) => Boolean = _.memberName == _.memberName

  override protected def matchInstantiation: (Option[UmlMethod], Option[UmlMethod]) => UmlMethodMatch = UmlMethodMatch

}
