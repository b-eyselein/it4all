package model.uml

import model.Enums
import model.Enums.SuccessType.NONE
import model.core.EvaluationResult._
import model.core.matching.{Match, MatchingResult}
import model.core.{CompleteResult, EvaluationResult}
import model.uml.UmlEnums.UmlExPart
import model.uml.UmlResult._
import play.twirl.api.Html

import scala.language.postfixOps

object UmlResult {

  def describeImplementation(impl: UmlImplementation): String = s"${impl.subClass}  &rarr;  ${impl.superClass}"

  def describeAssociation(assoc: UmlAssociation): String =
    s"${assoc.assocType.germanName}: ${assoc.firstEnd} &harr; ${assoc.secondEnd} (${assoc.displayMult()})"

}

// FIXME: convert to CompleteResult?
abstract sealed class UmlResult(val exercise: UmlCompleteEx, val learnerSolution: UmlSolution, compareClasses: Boolean, compareClassesExtended: Boolean, assocs: Boolean, impls: Boolean) extends CompleteResult[EvaluationResult] {

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

  val nextPart: UmlExPart

}

case class ClassSelectionResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, compareClasses = true, compareClassesExtended = false, assocs = false, impls = false) {

  override def renderLearnerSolution: Html = new Html(
    "<h4>Ihre gew&auml;hlten Klassen:</h4>\n<ul>" + (classResult match {
      case Some(cr) => cr.allMatches.flatMap(matching => matching.userArg map (_.clazz.className)) map (clazz => "<li>" + clazz + "</li>") mkString
      case _        => ""
    }) + "</ul>")

  override val nextPart = UmlExPart.DIAG_DRAWING_HELP

}

case class DiagramDrawingHelpResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, compareClasses = false, compareClassesExtended = true, assocs = true, impls = true) {

  // FIXME: implement renderLearnerSolution!
  override def renderLearnerSolution: Html = new Html(
    "<h4>Ihre Vererbungsbeziehungen:</h4><ul>" + (learnerSolution.implementations map (impl => "<li>" + describeImplementation(impl) + "</li>") mkString) + "</ul>"
      + "<h4>Ihre Assoziationen:</h4><ul>" + (learnerSolution.associations map (assoc => "<li>" + describeAssociation(assoc) + "</li>") mkString) + "</ul>")

  override val nextPart = UmlExPart.ALLOCATION

}

case class DiagramDrawingResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, compareClasses = true, compareClassesExtended = true, assocs = true, impls = true) {

  override def renderLearnerSolution: Html = ???

  override val nextPart = UmlExPart.FINISHED

}

case class AllocationResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, compareClasses = true, compareClassesExtended = true, assocs = false, impls = false) {

  override def renderLearnerSolution: Html = new Html(learnerSolution.classes map { clazz =>
    "<p>" + clazz.clazz.className + "</p><ul>" + (clazz.allMembers map (_.render asListElem) match {
      case Nil => "--" asListElem
      case ms  => ms mkString
    }) + "</ul>"
  } mkString)

  override val nextPart: UmlExPart = UmlExPart.FINISHED

}