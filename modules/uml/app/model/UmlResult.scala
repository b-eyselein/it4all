package model

import model.matching.{Match, MatchingResult}
import model.result.{EvaluationResult, SuccessType}
import model.umlmatcher._

abstract sealed class UmlResult(val exercise: UmlExercise, val learnerSolution: UmlSolution)
  extends EvaluationResult(SuccessType.NONE) {

  val musterSolution: UmlSolution = exercise.getSolution

  protected val classResult: Option[MatchingResult[UmlClass, UmlClassMatch]]

  protected val associationResult: Option[MatchingResult[UmlAssociation, UmlAssociationMatch]]

  protected val implementationResult: Option[MatchingResult[UmlImplementation, UmlImplementationMatch]]

  def notEmptyMatchingResults: List[MatchingResult[_, _ <: Match[_]]] =
    List(classResult, associationResult, implementationResult).flatten

}

case class ClassSelectionResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l) {

  override protected val classResult = Some(UmlClassMatcher(false).doMatch(learnerSolution.classes, musterSolution.classes))

  override protected val associationResult: Option[MatchingResult[UmlAssociation, UmlAssociationMatch]] = None

  override protected val implementationResult: Option[MatchingResult[UmlImplementation, UmlImplementationMatch]] = None


}

case class DiagramDrawingHelpResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l) {

  override val classResult: Option[MatchingResult[UmlClass, UmlClassMatch]] = None

  override val associationResult = Some(UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations))

  override val implementationResult = Some(UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations))


}

case class DiagramDrawingResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l) {

  override val classResult = Some(UmlClassMatcher(true).doMatch(learnerSolution.classes, musterSolution.classes))

  override val associationResult = Some(UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations))

  override val implementationResult = Some(UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations))

}
