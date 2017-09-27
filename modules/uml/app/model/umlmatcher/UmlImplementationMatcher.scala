package model.umlmatcher;

import model.matching.Matcher;
import model.UmlImplementation;
import model.matching.Match
import model.matching.MatchType
import scala.xml.Elem
import play.twirl.api.Html

object UmlImplementationMatcher extends Matcher[UmlImplementation, UmlImplementationMatch](
  "Vererbungsbeziehungen",
  List("Subklasse", "Superklasse"),
  _ == _,
  new UmlImplementationMatch(_, _, _))

class UmlImplementationMatch(i1: Option[UmlImplementation], i2: Option[UmlImplementation], s: Int)
  extends Match[UmlImplementation](i1, i2, s) {

  // TODO Auto-generated method stub
  override def analyze(i1: UmlImplementation, i2: UmlImplementation) =
    if (i1.subClass == i2.subClass && i1.superClass == i2.superClass)
      MatchType.SUCCESSFUL_MATCH
    else
      MatchType.UNSUCCESSFUL_MATCH

  override def describeArg(arg: UmlImplementation) = new Html(s"""
<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.subClass}</span></td>
<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.superClass}</span></td>""")

}
