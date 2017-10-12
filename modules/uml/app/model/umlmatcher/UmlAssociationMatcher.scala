package model.umlmatcher

import model.matching.{Match, MatchType, Matcher}
import model.{UmlAssociation, UmlAssociationEnd}
import play.twirl.api.Html

object UmlAssocMatcherHelper {
  def canAssocsMatch(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)

  def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    endsEqual(assoc1.ends._1, assoc2.ends._2) && endsEqual(assoc1.ends._2, assoc2.ends._1)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    endsEqual(assoc1.ends._1, assoc2.ends._1) && endsEqual(assoc1.ends._2, assoc2.ends._2)

  def endsEqual(c1End: UmlAssociationEnd, c2End: UmlAssociationEnd): Boolean = c1End.endName == c2End.endName
}

object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch](
  "Assoziationen", List("Typ", "Start", "Ende", "Multiplizität"), UmlAssocMatcherHelper.canAssocsMatch, UmlAssociationMatch)

case class UmlAssociationMatch(a1: Option[UmlAssociation], a2: Option[UmlAssociation], s: Int)
  extends Match[UmlAssociation](a1, a2, s) {

  var matched: Boolean = _
  var endsCrossed: Boolean = _
  var assocTypeEqual: Boolean = _
  var multiplicitiesEqual: Boolean = _

  override def analyze(assoc1: UmlAssociation, assoc2: UmlAssociation): MatchType = {
    matched = true
    assocTypeEqual = assoc1.assocType == assoc2.assocType

    endsCrossed = UmlAssocMatcherHelper.endsCrossedEqual(assoc1, assoc2)

    multiplicitiesEqual =
      if (endsCrossed)
        assoc1.ends._1.multiplicity == assoc2.ends._2.multiplicity &&
          assoc1.ends._2.multiplicity == assoc2.ends._1.multiplicity
      else
        assoc1.ends._1.multiplicity == assoc2.ends._1.multiplicity &&
          assoc1.ends._1.multiplicity == assoc2.ends._2.multiplicity

    if (assocTypeEqual && multiplicitiesEqual) MatchType.SUCCESSFUL_MATCH
    else MatchType.UNSUCCESSFUL_MATCH
  }

  def displayMult(end1: UmlAssociationEnd, end2: UmlAssociationEnd): String =
    end1.multiplicity.representant + " : " + end2.multiplicity.representant

  def displayMults(arg: UmlAssociation, turn: Boolean): String =
    if (turn) displayMult(arg.ends._2, arg.ends._1)
    else displayMult(arg.ends._1, arg.ends._2)

  override def describeArg(arg: UmlAssociation): Html = {
    val turn = (arg eq sampleArg.orNull) && endsCrossed
    new Html(
      s"""<td><span class="text-${if (assocTypeEqual) "success" else "danger"}">${arg.assocType.germanName}</span></td>
         |<td><span class="text-${if (matched) "success" else "danger"}">${if (turn) arg.ends._2.endName else arg.ends._1.endName}</span></td>
         |<td><span class="text-${if (matched) "success" else "danger"}">${if (turn) arg.ends._1.endName else arg.ends._2.endName}</span></td>
         |<td><span class="text-${if (multiplicitiesEqual) "success" else "danger"}">${displayMults(arg, turn)}</span></td>""".stripMargin)
  }

  override def explanation: List[String] = matchType match {
    case MatchType.UNSUCCESSFUL_MATCH => {
      val expls = scala.collection.mutable.ListBuffer.empty[String]
      if (!multiplicitiesEqual) expls += "Multiplizitäten sind falsch."
      if (!assocTypeEqual) expls += "Assoziationstyp ist falsch."
      expls.toList
    }
    case _ => super.explanation
  }

}
