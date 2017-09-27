package model.umlmatcher

import model.matching.Matcher
import model.UmlAssociation
import model.UmlAssociationEnd
import model.matching.Match
import model.matching.MatchType
import model.Association
import model.Composition
import model.Aggregation
import play.twirl.api.Html

object UmlAssocMatcherHelper {
  def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation) =
    endsEqual(assoc1.ends._1, assoc2.ends._2) && endsEqual(assoc1.ends._2, assoc2.ends._1)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation) =
    endsEqual(assoc1.ends._1, assoc2.ends._1) && endsEqual(assoc1.ends._2, assoc2.ends._2)

  def endsEqual(c1End: UmlAssociationEnd, c2End: UmlAssociationEnd) = c1End.endName == c2End.endName
}

object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch](
  "Assoziationen",
  List("Typ", "Start", "Ende", "Multiplizit\u00E4t"),
  (assoc1, assoc2) => UmlAssocMatcherHelper.endsParallelEqual(assoc1, assoc2) || UmlAssocMatcherHelper.endsCrossedEqual(assoc1, assoc2),
  new UmlAssociationMatch(_, _, _))

case class UmlAssociationMatch(a1: Option[UmlAssociation], a2: Option[UmlAssociation], s: Int)
  extends Match[UmlAssociation](a1, a2, s) {

  var assocTypeEqual = false
  var multiplicitiesEqual = false

  override def analyze(assoc1: UmlAssociation, assoc2: UmlAssociation) = {
    assocTypeEqual = assoc1.assocType == assoc2.assocType

    if (UmlAssocMatcherHelper.endsParallelEqual(assoc1, assoc2))
      multiplicitiesEqual = assoc1.ends._1.multiplicity == assoc2.ends._1.multiplicity &&
        assoc1.ends._1.multiplicity == assoc2.ends._2.multiplicity
    else
      multiplicitiesEqual = assoc1.ends._1.multiplicity == assoc2.ends._2.multiplicity &&
        assoc1.ends._2.multiplicity == assoc2.ends._1.multiplicity

    if (assocTypeEqual && multiplicitiesEqual)
      MatchType.SUCCESSFUL_MATCH
    else
      MatchType.UNSUCCESSFUL_MATCH
  }

  val correctMultiplicity = if (sampleArg.isDefined && userArg.isDefined)
    sampleArg.get.multsAsString(UmlAssocMatcherHelper.endsCrossedEqual(userArg.get, sampleArg.get))
  else ""

  val isCorrect = multiplicitiesEqual && assocTypeEqual

  override def describeArg(arg: UmlAssociation) = new Html(s"""
<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.assocType}</span></td>
<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.ends._1.endName}</span></td>
<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.ends._2.endName}</span></td>
<td>
  <span class="text-${if (isSuccessful) "success" else "danger"}">
  ${arg.ends._1.multiplicity.representant} : ${arg.ends._2.multiplicity.representant}</span>
</td>""")

}
