package model.umlmatcher;

import model.matching.ScalaMatcher;
import model.UmlImplementation;
import model.matching.ScalaMatch
import model.matching.MatchType
import scala.xml.Elem

object UmlImplementationMatcher
  extends ScalaMatcher[UmlImplementation, UmlImplementationMatch](
    "Vererbungsbeziehungen",
    _ == _, new UmlImplementationMatch(_, _));

class UmlImplementationMatch(i1: Option[UmlImplementation], i2: Option[UmlImplementation])
  extends ScalaMatch[UmlImplementation](i1, i2) {

  // TODO Auto-generated method stub
  override def analyze(i1: UmlImplementation, i2: UmlImplementation) =
    if (i1.subClass == i2.subClass && i1.superClass == i2.superClass)
      MatchType.SUCCESSFUL_MATCH
    else
      MatchType.UNSUCCESSFUL_MATCH

  override def describeArg(arg: UmlImplementation) = arg.subClass + " --> " + arg.superClass

}
