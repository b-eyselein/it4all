package model.uml

import model.Enums.SuccessType.NONE
import model.core.EvaluationResult
import model.core.matching.{Match, MatchingResult}

abstract sealed class UmlResult(val exercise: UmlCompleteEx, val learnerSolution: UmlSolution, assocs: Boolean, impls: Boolean)
  extends EvaluationResult(NONE) {

  val musterSolution: UmlSolution = exercise.solution

  protected val classResult: Option[MatchingResult[UmlCompleteClass, UmlClassMatch]]

  protected val associationResult: Option[MatchingResult[UmlAssociation, UmlAssociationMatch]] =
    if (assocs) Some(UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations))
    else None

  protected val implementationResult: Option[MatchingResult[UmlImplementation, UmlImplementationMatch]] =
    if (impls) Some(UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations))
    else None

  def notEmptyMatchingResults: List[MatchingResult[_, _ <: Match[_]]] =
    List(classResult, associationResult, implementationResult).flatten

}

case class ClassSelectionResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, assocs = false, impls = false) {

  println(learnerSolution)

  println(musterSolution)

  override protected val classResult = Some(UmlClassMatcher(false).doMatch(learnerSolution.classes, musterSolution.classes))


}

case class DiagramDrawingHelpResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, assocs = true, impls = true) {

  override protected val classResult: Option[MatchingResult[UmlCompleteClass, UmlClassMatch]] = None

}

case class DiagramDrawingResult(e: UmlCompleteEx, l: UmlSolution) extends UmlResult(e, l, assocs = true, impls = true) {

  override protected val classResult = Some(UmlClassMatcher(true).doMatch(learnerSolution.classes, musterSolution.classes))

}
