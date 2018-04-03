package model.uml

import model.Enums
import model.Enums.SuccessType.NONE
import model.core.EvaluationResult._
import model.core.matching.{Match, MatchingResult}
import model.core.{CompleteResult, EvaluationResult}
import model.uml.UmlCompleteResult._
import play.twirl.api.Html

import scala.language.postfixOps

object UmlCompleteResult {

  def describeImplementation(impl: UmlClassDiagImplementation): String = s"${impl.subClass}  &rarr;  ${impl.superClass}"

  def describeAssociation(assoc: UmlClassDiagAssociation): String =
    s"${assoc.assocType.germanName}: ${assoc.firstEnd} &harr; ${assoc.secondEnd} (${assoc.displayMult(turn = false)})"

}

abstract sealed class UmlCompleteResult(val exercise: UmlCompleteEx, val learnerSolution: UmlClassDiagram, val solutionSaved: Boolean,
                                        compareClasses: Boolean, compareClassesExtended: Boolean, assocs: Boolean, impls: Boolean)
  extends CompleteResult[EvaluationResult] {

  override type SolType = UmlClassDiagram

  override val success: Enums.SuccessType = NONE

  override def results: Seq[EvaluationResult] = Seq.empty ++ classResult ++ associationResult ++ implementationResult

  // FIXME: part!
  val musterSolution: UmlClassDiagram = exercise.ex.solution

  protected val classResult: Option[MatchingResult[UmlClassDiagClass, UmlClassMatch]] =
    if (compareClasses) Some(UmlClassMatcher(compareClassesExtended).doMatch(learnerSolution.classes, musterSolution.classes))
    else None

  protected val associationResult: Option[MatchingResult[UmlClassDiagAssociation, UCD_AssociationMatch]] =
    if (assocs) Some(UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations))
    else None

  protected val implementationResult: Option[MatchingResult[UmlClassDiagImplementation, UmlImplementationMatch]] =
    if (impls) Some(UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations))
    else None

  def notEmptyMatchingResults: Seq[MatchingResult[_, _ <: Match[_]]] = Seq(classResult, associationResult, implementationResult) flatten

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
    s"""<p>${clazz.className}</p>
       |<ul>
       |  ${displayMembers(clazz.allMembers)}
       |</ul>""".stripMargin
  } mkString)

  private def displayMembers(members: Seq[UmlClassDiagClassMember]): String = members map (m => "<li>" + m.name + ": " + m.memberType + "</li>") match {
    case Nil => "<li>--</li>"
    case ms  => ms mkString
  }

}

class ClassSelectionResult(completeExercise: UmlCompleteEx, learnerSolution: UmlClassDiagram, solSaved: Boolean)
  extends UmlCompleteResult(completeExercise, learnerSolution, solSaved: Boolean, compareClasses = true, compareClassesExtended = false, assocs = false, impls = false) {

  override def renderLearnerSolution: Html = new Html(
    s"""<h4>Ihre Klassen:</h4>
       |<ul>
       |  ${classResult map (_.allMatches flatMap (_.userArg map (_.className asListElem)) mkString) getOrElse ""}
       |</ul>""".stripMargin)

  override val nextPart: Option[UmlExPart] = Some(DiagramDrawingHelp)

}

class DiagramDrawingHelpResult(completeExercise: UmlCompleteEx, learnserSolution: UmlClassDiagram, solSaved: Boolean)
  extends UmlCompleteResult(completeExercise, learnserSolution, solSaved: Boolean, compareClasses = false, compareClassesExtended = true, assocs = true, impls = true) {

  // FIXME: implement renderLearnerSolution!
  override def renderLearnerSolution: Html = new Html(displayAssocsAndImpls)

  override val nextPart: Option[UmlExPart] = Some(MemberAllocation)

}

class DiagramDrawingResult(completeExercise: UmlCompleteEx, learnserSolution: UmlClassDiagram, solSaved: Boolean)
  extends UmlCompleteResult(completeExercise, learnserSolution, solSaved: Boolean, compareClasses = true, compareClassesExtended = true, assocs = true, impls = true) {

  override def renderLearnerSolution: Html = new Html(displayClasses + displayAssocsAndImpls)

  override val nextPart: Option[UmlExPart] = None

}

class AllocationResult(completeExercise: UmlCompleteEx, learnserSolution: UmlClassDiagram, solSaved: Boolean)
  extends UmlCompleteResult(completeExercise, learnserSolution, solSaved: Boolean, compareClasses = true, compareClassesExtended = true, assocs = false, impls = false) {

  override def renderLearnerSolution: Html = new Html(displayClasses)

  override val nextPart: Option[UmlExPart] = None

}