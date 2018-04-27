package model.uml.matcher

import model.core.matching.{Match, MatchType, Matcher}
import model.uml.UmlConsts._
import model.uml.{UmlAttribute, UmlClassMember, UmlMethod}
import play.api.libs.json.{JsValue, Json}

sealed trait UmlClassMemberMatch[Mem <: UmlClassMember] extends Match[Mem]


case class UmlAttributeMatch(userArg: Option[UmlAttribute], sampleArg: Option[UmlAttribute]) extends UmlClassMemberMatch[UmlAttribute] {

  override protected def descArgForJson(arg: UmlAttribute): JsValue = Json.obj(nameName -> arg.memberName, typeName -> arg.memberType)

  override def analyze(arg1: UmlAttribute, arg2: UmlAttribute): MatchType = {

    // Compare visibility
    val visibilityComparison = arg1.visibility == arg2.visibility

    // Return type
    val returnTypeComparison = arg1.memberType == arg2.memberType

    // Modificator comparison
    val isStaticComparison = arg1.isStatic == arg2.isStatic
    val isDerivedComparison = arg1.isDerived == arg2.isDerived
    val isAbstractComparison = arg1.isAbstract == arg2.isAbstract

    MatchType.UNSUCCESSFUL_MATCH
  }

}

object UmlAttributeMatcher extends Matcher[UmlAttribute, UmlAttributeMatch] {

  override protected def canMatch: (UmlAttribute, UmlAttribute) => Boolean = _.memberName == _.memberName

  override protected def matchInstantiation: (Option[UmlAttribute], Option[UmlAttribute]) => UmlAttributeMatch = UmlAttributeMatch

}


case class UmlMethodMatch(userArg: Option[UmlMethod], sampleArg: Option[UmlMethod]) extends UmlClassMemberMatch[UmlMethod] {

  override protected def descArgForJson(arg: UmlMethod): JsValue = Json.obj(nameName -> arg.memberName, typeName -> arg.memberType)

  override def analyze(arg1: UmlMethod, arg2: UmlMethod): MatchType = {

    MatchType.UNSUCCESSFUL_MATCH
  }

}

object UmlMethodMatcher extends Matcher[UmlMethod, UmlMethodMatch] {

  override protected def canMatch: (UmlMethod, UmlMethod) => Boolean = _.memberName == _.memberName

  override protected def matchInstantiation: (Option[UmlMethod], Option[UmlMethod]) => UmlMethodMatch = UmlMethodMatch

}
