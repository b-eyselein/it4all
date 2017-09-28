package model

import model.exercise.Success
import model.result.EvaluationResult
import model.umlmatcher.UmlAssociationMatcher
import model.umlmatcher.UmlClassMatcher
import model.umlmatcher.UmlImplementationMatcher
import model.matching.MatchingResult
import model.matching.Match

abstract sealed class UmlResult(val exercise: UmlExercise, val learnerSolution: UmlSolution)
  extends EvaluationResult(Success.NONE) {

  val musterSolution = exercise.getSolution

  def notEmptyMatchingResults: List[MatchingResult[_, _ <: Match[_]]]

}

case class ClassSelectionResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l) {

  val classResult = new UmlClassMatcher(false).doMatch(learnerSolution.classes, musterSolution.classes)

  override def notEmptyMatchingResults = List(classResult)

}

case class DiagramDrawingHelpResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l) {

  val associationResult = UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations)

  val implementationResult = UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations)

  override def notEmptyMatchingResults = List(associationResult, implementationResult)

}

case class DiagramDrawingResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l) {

  val classResult = new UmlClassMatcher(true).doMatch(learnerSolution.classes, musterSolution.classes)

  val associationResult = UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations)

  val implementationResult = UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations)

  override def notEmptyMatchingResults = List(classResult, associationResult, implementationResult)

}
