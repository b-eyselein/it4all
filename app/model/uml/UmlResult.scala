package model.uml

import model.Enums
import model.Enums.SuccessType.NONE
import model.core.EvaluationResult._
import model.core.matching.{Match, MatchingResult}
import model.core.{CompleteResult, EvaluationResult}
import model.uml.UmlResult._
import play.twirl.api.Html

import scala.language.postfixOps

object UmlResult {

  def describeImplementation(impl: UmlImplementation): String = s"${impl.subClass}  &rarr;  ${impl.superClass}"

  def describeAssociation(assoc: UmlAssociation): String =
    s"${assoc.assocType.germanName}: ${assoc.firstEnd} &harr; ${assoc.secondEnd} (${assoc.displayMult()})"

}

// FIXME: convert to CompleteResult?
abstract sealed class UmlResult(val exercise: UmlCompleteEx, val learnerSolution: UmlSolution, compareClasses: Boolean, compareClassesExtended: Boolean, assocs: Boolean, impls: Boolean)
  extends CompleteResult[EvaluationResult] {

  override type SolType = UmlSolution

  override val success: Enums.SuccessType = NONE

  override def results: Seq[EvaluationResult] = Seq.empty ++ classResult ++ associationResult ++ implementationResult

  val musterSolution: UmlSolution = exercise.solution

  protected val classResult: Option[MatchingResult[UmlCompleteClass, UmlClassMatch]] =
    if (compareClasses) Some(UmlClassMatcher(compareClassesExtended).doMatch(learnerSolution.classes, musterSolution.classes))
    else None

  protected val associationResult: Option[MatchingResult[UmlAssociation, UmlAssociationMatch]] =
    if (assocs) Some(UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations))
    else None

  protected val implementationResult: Option[MatchingResult[UmlImplementation, UmlImplementationMatch]] =
    if (impls) Some(UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations))
    else None

  def notEmptyMatchingResults: Seq[MatchingResult[_, _ <: Match[_]]] =
    Seq(classResult, associationResult, implementationResult) flatten

  val nextPart: Option[UmlExPart]

  protected def displayAssocsAndImpls: String =
    s"""<h4>Ihre Vererbungsbeziehungen:</h4>
       |<ul>
       |  ${learnerSolution.implementations map (describeImplementation(_) asListElem) mkString}
       |</ul>
       |<h4>Ihre Assoziationen:</h4>
       |<ul>
       |  ${learnerSolution.associations map (describeAssociation(_) asListElem) mkString}
       |</ul>""".stripMargin

  protected def displayClasses: String = "<h4>Ihre Klassen:</h4>" + (learnerSolution.classes map { clazz =>
    s"""<p>${clazz.clazz.className}</p>
       |<ul>
       |  ${displayMembers(clazz.allMembers)}
       |</ul>""".stripMargin
  } mkString)

  private def displayMembers(members: Seq[UmlClassMember]): String = members map (_.render asListElem) match {
    case Nil => "<li>--</li>"
    case ms  => ms mkString
  }

}

case class ClassSelectionResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, compareClasses = true, compareClassesExtended = false, assocs = false, impls = false) {

  override def renderLearnerSolution: Html = new Html(
    s"""<h4>Ihre Klassen:</h4>
       |<ul>
       |  ${classResult map (_.allMatches flatMap (_.userArg map (_.clazz.className asListElem)) mkString) getOrElse ""}
       |</ul>""".stripMargin)

  override val nextPart: Option[UmlExPart] = Some(DiagramDrawingHelp)

}

case class DiagramDrawingHelpResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, compareClasses = false, compareClassesExtended = true, assocs = true, impls = true) {

  // FIXME: implement renderLearnerSolution!
  override def renderLearnerSolution: Html = new Html(displayAssocsAndImpls)

  override val nextPart: Option[UmlExPart] = Some(MemberAllocation)

}

case class DiagramDrawingResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, compareClasses = true, compareClassesExtended = true, assocs = true, impls = true) {

  override def renderLearnerSolution: Html = new Html(displayClasses + displayAssocsAndImpls)

  override val nextPart: Option[UmlExPart] = None

}

case class AllocationResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, compareClasses = true, compareClassesExtended = true, assocs = false, impls = false) {

  override def renderLearnerSolution: Html = new Html(displayClasses)

  override val nextPart: Option[UmlExPart] = None

}