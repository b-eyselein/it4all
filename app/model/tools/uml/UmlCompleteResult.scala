package model.tools.uml

import model.core.matching.{Match, MatchingResult}
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import model.tools.uml.matcher._

object UmlCompleteResult {

  def describeImplementation(impl: UmlImplementation): String = s"${impl.subClass}  &rarr;  ${impl.superClass}"

  def describeAssociation(assoc: UmlAssociation): String =
    s"${assoc.assocType.german}: ${assoc.firstEnd} &harr; ${assoc.secondEnd} (${assoc.displayMult(turn = false)})"

}

final case class UmlCompleteResult(learnerSolution: UmlClassDiagram, sampleSolution: UmlClassDiagram, part: UmlExPart, solutionSaved: Boolean = false)
  extends CompleteResult[EvaluationResult] {

  override val success: SuccessType = SuccessType.NONE

  override def results: Seq[MatchingResult[_ <: Match]] = Seq[MatchingResult[_ <: Match]]() ++ classResult ++ assocAndImplResult.map(_._1) ++ assocAndImplResult.map(_._2)

  //  private val sampleSolution: UmlClassDiagram = exercise.sampleSolutions.head.sample // FIXME!

  val classResult: Option[MatchingResult[UmlClassMatch]] = part match {
    case UmlExParts.DiagramDrawingHelp                           => None
    case UmlExParts.ClassSelection                               => Some(UmlClassMatcher(false).doMatch(learnerSolution.classes, sampleSolution.classes))
    case UmlExParts.DiagramDrawing | UmlExParts.MemberAllocation => Some(UmlClassMatcher(true).doMatch(learnerSolution.classes, sampleSolution.classes))
  }

  val assocAndImplResult: Option[(MatchingResult[UmlAssociationMatch], MatchingResult[UmlImplementationMatch])] =
    part match {
      case UmlExParts.DiagramDrawingHelp | UmlExParts.DiagramDrawing =>
        val assocRes = UmlAssociationMatcher.doMatch(learnerSolution.associations, sampleSolution.associations)
        val implRes = UmlImplementationMatcher.doMatch(learnerSolution.implementations, sampleSolution.implementations)
        Some((assocRes, implRes))
      case _                                                         => None
    }

  def nextPart: Option[UmlExPart] = part match {
    case UmlExParts.DiagramDrawing => None

    case UmlExParts.ClassSelection     => Some(UmlExParts.DiagramDrawingHelp)
    case UmlExParts.DiagramDrawingHelp => Some(UmlExParts.MemberAllocation)
    case UmlExParts.MemberAllocation   => None
  }

  override val points = -1 points

  override def maxPoints: Points = -1 points

}
