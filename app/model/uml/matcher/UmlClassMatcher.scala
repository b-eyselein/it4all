package model.uml.matcher

import model.core.CoreConsts.{sampleArgName, successName, userArgName}
import model.core.matching.{Match, MatchType, Matcher, MatchingResult}
import model.uml.{UmlAttribute, UmlClass, UmlMethod}
import model.uml.UmlConsts._
import play.api.libs.json.{JsValue, Json}

import scala.language.postfixOps

case class UmlClassMatch(userArg: Option[UmlClass], sampleArg: Option[UmlClass], compAM: Boolean) extends Match[UmlClass] {

  val memberResults: Option[(MatchingResult[UmlAttribute, UmlAttributeMatch], MatchingResult[UmlMethod, UmlMethodMatch])] = if (compAM) {

    val attributeResult = UmlAttributeMatcher.doMatch(userArg.map(_.attributes).getOrElse(Seq.empty), sampleArg.map(_.attributes).getOrElse(Seq.empty))
    val methodResult = UmlMethodMatcher.doMatch(userArg.map(_.methods).getOrElse(Seq.empty), sampleArg.map(_.methods).getOrElse(Seq.empty))

    Some((attributeResult, methodResult))
  } else None

  val attributesResult: Option[MatchingResult[UmlAttribute, UmlAttributeMatch]] = memberResults.map(_._1)
  val methodsResult   : Option[MatchingResult[UmlMethod, UmlMethodMatch]]       = memberResults.map(_._2)

  override def analyze(c1: UmlClass, c2: UmlClass): MatchType = if (!compAM) MatchType.SUCCESSFUL_MATCH else {
    val attrResult = UmlAttributeMatcher.doMatch(c1.attributes, c2.attributes)
    val methResult = UmlMethodMatcher.doMatch(c1.methods, c2.methods)

    if (attrResult.isSuccessful && methResult.isSuccessful) MatchType.SUCCESSFUL_MATCH else MatchType.UNSUCCESSFUL_MATCH
  }

  // FIXME: check if correct!
  override protected def descArgForJson(arg: UmlClass): JsValue = Json.obj(classNameName -> arg.className, classTypeName -> arg.classType.entryName)

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    userArgName -> (userArg map descArgForJson),
    sampleArgName -> (sampleArg map descArgForJson),
    attributesResultName -> attributesResult.map(_.toJson),
    methodsResultName -> methodsResult.map(_.toJson)
  )

}

case class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlClass, UmlClassMatch] {

  override protected def canMatch: (UmlClass, UmlClass) => Boolean = _.className == _.className


  override protected def matchInstantiation: (Option[UmlClass], Option[UmlClass]) => UmlClassMatch = UmlClassMatch(_, _, compareAttrsAndMethods)

}
