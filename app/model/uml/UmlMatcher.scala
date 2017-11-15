package model.uml

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.matching.{Match, Matcher, MatchingResult, StringMatcher}
import model.uml.UmlEnums.UmlMultiplicity
import play.twirl.api.Html

object UmlAssocMatcherHelper {

  def canAssocsMatch(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)

  def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)

}

object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch](
  "Assoziationen", List("Typ", "Start", "Ende", "Multiplizität"), UmlAssocMatcherHelper.canAssocsMatch, UmlAssociationMatch)

case class UmlAssociationMatch(a1: Option[UmlAssociation], a2: Option[UmlAssociation], s: Int)
  extends Match[UmlAssociation](a1, a2, s) {

  var matched            : Boolean = _
  var endsCrossed        : Boolean = _
  var assocTypeEqual     : Boolean = _
  var multiplicitiesEqual: Boolean = _

  override def analyze(assoc1: UmlAssociation, assoc2: UmlAssociation): MatchType = {
    matched = true
    assocTypeEqual = assoc1.assocType == assoc2.assocType

    endsCrossed = UmlAssocMatcherHelper.endsCrossedEqual(assoc1, assoc2)

    multiplicitiesEqual =
      if (endsCrossed) assoc1.firstMult == assoc2.secondMult && assoc1.secondMult == assoc2.firstMult
      else assoc1.firstMult == assoc2.firstMult && assoc1.firstMult == assoc2.secondMult

    if (assocTypeEqual && multiplicitiesEqual) SUCCESSFUL_MATCH
    else UNSUCCESSFUL_MATCH
  }

  override def describeArg(arg: UmlAssociation): Html = {
    val turn = (arg eq sampleArg.orNull) && endsCrossed
    new Html(
      s"""<td><span class="text-${if (assocTypeEqual) "success" else "danger"}">${arg.assocType.germanName}</span></td>
         |<td><span class="text-${if (matched) "success" else "danger"}">${if (turn) arg.secondEnd else arg.firstEnd}</span></td>
         |<td><span class="text-${if (matched) "success" else "danger"}">${if (turn) arg.firstEnd else arg.secondEnd}</span></td>
         |<td><span class="text-${if (multiplicitiesEqual) "success" else "danger"}">${displayMults(arg, turn)}</span></td>""".stripMargin)
  }

  def displayMults(arg: UmlAssociation, turn: Boolean): String = if (turn) displayMult(arg.secondMult, arg.firstMult) else displayMult(arg.firstMult, arg.secondMult)

  def displayMult(end1: UmlMultiplicity, end2: UmlMultiplicity): String = end1.representant + " : " + end2.representant

  override def explanation: List[String] = matchType match {
    case UNSUCCESSFUL_MATCH =>
      val expls = scala.collection.mutable.ListBuffer.empty[String]
      if (!multiplicitiesEqual) expls += "Multiplizitäten sind falsch."
      if (!assocTypeEqual) expls += "Assoziationstyp ist falsch."
      expls.toList

    case _ => super.explanation
  }

}

case class UmlClassMatch(m1: Option[UmlCompleteClass], m2: Option[UmlCompleteClass], s: Int, compAM: Boolean) extends Match[UmlCompleteClass](m1, m2, s) {

  var attributesResult: MatchingResult[String, Match[String]] = _
  var methodsResult   : MatchingResult[String, Match[String]] = _

  override def analyze(c1: UmlCompleteClass, c2: UmlCompleteClass): MatchType = if (!compAM) SUCCESSFUL_MATCH
  else {
    // FIXME: get attributes!
    attributesResult = new StringMatcher("Attribute").doMatch(c1.allAttrs, c2.allAttrs)
    methodsResult = new StringMatcher("Methoden").doMatch(c1.allMethods, c2.allMethods)

    if (attributesResult.isSuccessful && methodsResult.isSuccessful) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH
  }

  override def describeArg(arg: UmlCompleteClass) =
    new Html(s"""<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.clazz.className}</span></td>""")

}

case class UmlClassMatcher(compareAttrsAndMethods: Boolean)
  extends Matcher[UmlCompleteClass, UmlClassMatch]("Klassen", List("Klassenname"), _.clazz.className == _.clazz.className, UmlClassMatch(_, _, _, compareAttrsAndMethods))

object UmlImplMatcherHelper {
  def compareImpls(i1: UmlImplementation, i2: UmlImplementation): Boolean =
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)
}

object UmlImplementationMatcher extends Matcher[UmlImplementation, UmlImplementationMatch](
  "Vererbungsbeziehungen", List("Subklasse", "Superklasse"), UmlImplMatcherHelper.compareImpls, UmlImplementationMatch)

case class UmlImplementationMatch(i1: Option[UmlImplementation], i2: Option[UmlImplementation], s: Int)
  extends Match[UmlImplementation](i1, i2, s) {

  override def analyze(i1: UmlImplementation, i2: UmlImplementation): MatchType =
    if (i1.subClass == i2.subClass && i1.superClass == i2.superClass) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH

  override def describeArg(arg: UmlImplementation) = new Html(
    s"""<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.subClass}</span></td>
       |<td><span class="text-${if (isSuccessful) "success" else "danger"}">${arg.superClass}</span></td>""".stripMargin)

  override def explanation: List[String] = matchType match {
    case UNSUCCESSFUL_MATCH => List("Vererbungsrichtung falsch.")
    case ONLY_SAMPLE        => List("Vererbungsbeziehung nicht erstellt.")
    case ONLY_USER          => List("Vererbengsbeziehung ist falsch.")
    case _                  => super.explanation
  }

}
