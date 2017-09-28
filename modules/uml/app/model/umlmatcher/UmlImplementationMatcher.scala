package model.umlmatcher;

import model.UmlImplementation
import model.matching.{Match, MatchType, Matcher}
import play.twirl.api.Html

object UmlImplMatcherHelper {
  def compareImpls(i1: UmlImplementation, i2: UmlImplementation) =
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) ||
      (i1.subClass == i2.superClass && i1.superClass == i2.subClass)
}

object UmlImplementationMatcher extends Matcher[UmlImplementation, UmlImplementationMatch](
  "Vererbungsbeziehungen",
  List("Subklasse", "Superklasse"),
  UmlImplMatcherHelper.compareImpls(_, _),
  new UmlImplementationMatch(_, _, _))

class UmlImplementationMatch(i1: Option[UmlImplementation], i2: Option[UmlImplementation], s: Int)
  extends Match[UmlImplementation](i1, i2, s) {

  override def analyze(i1: UmlImplementation, i2: UmlImplementation) =
    if (i1.subClass == i2.subClass && i1.superClass == i2.superClass)
      MatchType.SUCCESSFUL_MATCH
    else
      MatchType.UNSUCCESSFUL_MATCH

  override def describeArg(arg: UmlImplementation) = new Html(s"""
<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.subClass}</span></td>
<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.superClass}</span></td>""")

  override def explanation = matchType match {
    case MatchType.UNSUCCESSFUL_MATCH ⇒ List("Vererbungsrichtung falsch.")
    case MatchType.ONLY_SAMPLE        ⇒ List("Vererbungsbeziehung nicht erstellt.")
    case MatchType.ONLY_USER          ⇒ List("Vererbengsbeziehung ist falsch.")
    case _                            ⇒ super.explanation
  }

}
