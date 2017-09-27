package model.umlmatcher;

import model.matching.ScalaMatcher
import model.UmlClass;
import model.matching.MatchingResult
import model.matching.ScalaMatch
import model.matching.StringEqualsMatcher
import model.matching.MatchType
import model.matching.Match
import model.matching.ScalaStringMatcher
import model.UmlClass
import model.matching.ScalaMatchingResult

case class UmlClassMatch(m1: Option[UmlClass], m2: Option[UmlClass], compareAttrsAndMehtods: Boolean)
  extends ScalaMatch[UmlClass](m1, m2) {

  var attributesResult: ScalaMatchingResult[String, ScalaMatch[String]] = null
  var methodsResult: ScalaMatchingResult[String, ScalaMatch[String]] = null

  override def analyze(c1: UmlClass, c2: UmlClass) = {
    attributesResult = UmlClassMatch.ATTRS_MATCHER.doMatch(c1.attributes, c2.attributes)
    methodsResult = UmlClassMatch.METHODS_MATCHER.doMatch(c1.methods, c2.methods)

    if (!compareAttrsAndMehtods || (attributesResult.isSuccessful() && methodsResult.isSuccessful()))
      MatchType.SUCCESSFUL_MATCH
    else
      MatchType.UNSUCCESSFUL_MATCH
  }

  override def describeArg(arg: UmlClass) = arg.name

}

object UmlClassMatch {
  val ATTRS_MATCHER = new ScalaStringMatcher("Attribute")
  val METHODS_MATCHER = new ScalaStringMatcher("Methoden")
}

class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends ScalaMatcher[UmlClass, UmlClassMatch](
  "Klassen",
  (c1, c2) => c1.name == c2.name,
  new UmlClassMatch(_, _, compareAttrsAndMethods)) 
