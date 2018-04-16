package model.uml.matcher

import model.core.matching.{Match, Matcher, MatchingResult}
import model.uml.UmlClassMember
import model.uml.UmlConsts._
import play.api.libs.json.{JsValue, Json}

case class UmlClassMemberMatch(userArg: Option[UmlClassMember], sampleArg: Option[UmlClassMember]) extends Match[UmlClassMember] {

  override protected def descArgForJson(arg: UmlClassMember): JsValue = Json.obj(nameName -> arg.memberName, typeName -> arg.memberType)

}

object UmlClassMemberMatcher extends Matcher[UmlClassMember, UmlClassMemberMatch, UmlClassMemberMatchingResult] {

  override protected def canMatch: (UmlClassMember, UmlClassMember) => Boolean = _ == _


  override protected def matchInstantiation: (Option[UmlClassMember], Option[UmlClassMember]) => UmlClassMemberMatch = UmlClassMemberMatch


  override protected def resultInstantiation: Seq[UmlClassMemberMatch] => UmlClassMemberMatchingResult = UmlClassMemberMatchingResult

}

case class UmlClassMemberMatchingResult(allMatches: Seq[UmlClassMemberMatch]) extends MatchingResult[UmlClassMember, UmlClassMemberMatch]
