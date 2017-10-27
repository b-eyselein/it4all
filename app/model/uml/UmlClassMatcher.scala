package model.uml

import model.core.matching.MatchType.MatchType
import model.core.matching._
import play.twirl.api.Html

case class UmlClassMatch(m1: Option[UmlClass], m2: Option[UmlClass], s: Int, compAM: Boolean) extends Match[UmlClass](m1, m2, s) {

  var attributesResult: MatchingResult[String, Match[String]] = _
  var methodsResult   : MatchingResult[String, Match[String]] = _

  override def analyze(c1: UmlClass, c2: UmlClass): MatchType = if (compAM) MatchType.SUCCESSFUL_MATCH
  else {
    attributesResult = new StringMatcher("Attribute").doMatch(c1.attributes, c2.attributes)
    methodsResult = new StringMatcher("Methoden").doMatch(c1.methods, c2.methods)

    if (attributesResult.isSuccessful && methodsResult.isSuccessful) MatchType.SUCCESSFUL_MATCH
    else MatchType.UNSUCCESSFUL_MATCH
  }

  override def describeArg(arg: UmlClass) =
    new Html(s"""<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.name}</span></td>""")

}

case class UmlClassMatcher(compareAttrsAndMethods: Boolean)
  extends Matcher[UmlClass, UmlClassMatch]("Klassen", List("Klassenname"), (c1, c2) => c1.name == c2.name, UmlClassMatch(_, _, _, compareAttrsAndMethods))
