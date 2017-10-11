package model.umlmatcher

import model.UmlClass
import model.matching._
import play.twirl.api.Html

case class UmlClassMatch(m1: Option[UmlClass], m2: Option[UmlClass], s: Int, compareAttrsAndMethods: Boolean)
  extends Match[UmlClass](m1, m2, s) {

  import UmlClassMatch._

  var attributesResult: MatchingResult[String, Match[String]] = _
  var methodsResult: MatchingResult[String, Match[String]] = _

  override def analyze(c1: UmlClass, c2: UmlClass): MatchType = if (compareAttrsAndMethods) MatchType.SUCCESSFUL_MATCH
  else {
    attributesResult = ATTRS_MATCHER.doMatch(c1.attributes, c2.attributes)
    methodsResult = METHODS_MATCHER.doMatch(c1.methods, c2.methods)

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
  (c1, c2) => c1.name == c2.name,
  UmlClassMatch(_, _, _, compareAttrsAndMethods))
