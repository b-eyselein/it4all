package model

import model.matching.{Match, MatchingResult}
import model.result.{EvaluationResult, SuccessType}
import model.umlmatcher._

abstract sealed class UmlResult(val exercise: UmlExercise, val learnerSolution: UmlSolution, assocs: Boolean, impls: Boolean)
  extends EvaluationResult(SuccessType.NONE) {

  val musterSolution: UmlSolution = exercise.getSolution

  protected val classResult: Option[MatchingResult[UmlClass, UmlClassMatch]]

  protected val associationResult: Option[MatchingResult[UmlAssociation, UmlAssociationMatch]] =
    if (assocs) Some(UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations))
    else None

  protected val implementationResult: Option[MatchingResult[UmlImplementation, UmlImplementationMatch]] =
    if (impls) Some(UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations))
    else None

  def notEmptyMatchingResults: List[MatchingResult[_, _ <: Match[_]]] =
    List(classResult, associationResult, implementationResult).flatten

}

case class ClassSelectionResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l, assocs = false, impls = false) {

  override protected val classResult = Some(UmlClassMatcher(false).doMatch(learnerSolution.classes, musterSolution.classes))


}

case class DiagramDrawingHelpResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l, assocs = true, impls = true) {

  override val classResult: Option[MatchingResult[UmlClass, UmlClassMatch]] = None

}

case class DiagramDrawingResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l, assocs = true, impls = true) {

  override val classResult = Some(UmlClassMatcher(true).doMatch(learnerSolution.classes, musterSolution.classes))

}
