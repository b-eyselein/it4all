package model.umlmatcher;

import model.UmlClass
import model.matching.{ Match, MatchType, Matcher, MatchingResult, StringMatcher }
import play.twirl.api.Html

case class UmlClassMatch(m1: Option[UmlClass], m2: Option[UmlClass], s: Int, compareAttrsAndMethods: Boolean)
  extends Match[UmlClass](m1, m2, s) {

  var attributesResult: MatchingResult[String, Match[String]] = null
  var methodsResult: MatchingResult[String, Match[String]] = null

  override def analyze(c1: UmlClass, c2: UmlClass) = compareAttrsAndMethods match {
    case false ⇒ MatchType.SUCCESSFUL_MATCH
    case true ⇒
      attributesResult = UmlClassMatch.ATTRS_MATCHER.doMatch(c1.attributes, c2.attributes)
      methodsResult = UmlClassMatch.METHODS_MATCHER.doMatch(c1.methods, c2.methods)

      if (attributesResult.isSuccessful && methodsResult.isSuccessful) MatchType.SUCCESSFUL_MATCH
      else MatchType.UNSUCCESSFUL_MATCH
  }

  override def describeArg(arg: UmlClass) =
    new Html(s"""<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.name}</span></td>""")

}

object UmlClassMatch {
  val ATTRS_MATCHER = new StringMatcher("Attribute")
  val METHODS_MATCHER = new StringMatcher("Methoden")
}

class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlClass, UmlClassMatch](
  "Klassen",
  List("Klassenname"),
  (c1, c2) ⇒ c1.name == c2.name,
  new UmlClassMatch(_, _, _, compareAttrsAndMethods)) 
