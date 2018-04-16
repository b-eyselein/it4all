package model.uml.matcher

import model.Enums.MatchType
import model.Enums.MatchType.{SUCCESSFUL_MATCH, UNSUCCESSFUL_MATCH}
import model.core.CoreConsts.{sampleArgName, successName, userArgName}
import model.core.matching.{Match, Matcher, MatchingResult}
import model.uml.UmlClass
import model.uml.UmlConsts._
import play.api.libs.json.{JsValue, Json}

import scala.language.postfixOps

case class UmlClassMatch(userArg: Option[UmlClass], sampleArg: Option[UmlClass], compAM: Boolean) extends Match[UmlClass] {

  val memberResults: Option[(UmlClassMemberMatchingResult, UmlClassMemberMatchingResult)] = if (compAM) {

    val attributeResult = UmlClassMemberMatcher.doMatch(userArg.map(_.attributes).getOrElse(Seq.empty), sampleArg.map(_.attributes).getOrElse(Seq.empty))
    val methodResult = UmlClassMemberMatcher.doMatch(userArg.map(_.methods).getOrElse(Seq.empty), sampleArg.map(_.methods).getOrElse(Seq.empty))

    Some((attributeResult, methodResult))
  } else None

  val attributesResult: Option[UmlClassMemberMatchingResult] = memberResults.map(_._1)
  val methodsResult   : Option[UmlClassMemberMatchingResult] = memberResults.map(_._2)

  override def analyze(c1: UmlClass, c2: UmlClass): MatchType = if (!compAM) SUCCESSFUL_MATCH else {
    val attrResult = UmlClassMemberMatcher.doMatch(c1.attributes, c2.attributes)
    val methResult = UmlClassMemberMatcher.doMatch(c1.methods, c2.methods)

    if (attrResult.isSuccessful && methResult.isSuccessful) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH
  }

  // FIXME: check if correct!
  override protected def descArgForJson(arg: UmlClass): JsValue = Json.obj(classNameName -> arg.className, classTypeName -> arg.classType.name)

  override def toJson: JsValue = Json.obj(
    successName -> matchType.name,
    userArgName -> (userArg map descArgForJson),
    sampleArgName -> (sampleArg map descArgForJson),
    attributesResultName -> attributesResult.map(_.toJson),
    methodsResultName -> methodsResult.map(_.toJson)
  )

}

case class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlClass, UmlClassMatch, UmlClassMatchingResult] {

  override protected def canMatch: (UmlClass, UmlClass) => Boolean = _.className == _.className


  override protected def matchInstantiation: (Option[UmlClass], Option[UmlClass]) => UmlClassMatch = UmlClassMatch(_, _, compareAttrsAndMethods)


  override protected def resultInstantiation: Seq[UmlClassMatch] => UmlClassMatchingResult = UmlClassMatchingResult

}


case class UmlClassMatchingResult(allMatches: Seq[UmlClassMatch]) extends MatchingResult[UmlClass, UmlClassMatch]
