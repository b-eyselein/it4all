package model.result

import model._
import model.uml._
import model.umlmatcher._
import model.matching.MatchingResult
import model.exercise.Success

abstract sealed class ScalaUmlResult(exercise: UmlExercise, learnerSolution: UmlSolution) extends EvaluationResult(Success.NONE) {
  def classResult: MatchingResult[UmlClass, UmlClassMatch]
}

case class ScalaClassSelectionResult(exercise: UmlExercise, learnerSolution: UmlSolution) extends ScalaUmlResult(exercise, learnerSolution) {

  override val classResult = new UmlClassMatcher(false).doMatch(null, null)
}

case class ScalaDiagramDrawingResult(exercise: UmlExercise, learnerSolution: UmlSolution) extends ScalaUmlResult(exercise, learnerSolution) {
  val musterSolution = exercise.getSolution()

  val associationResult = new UmlAssociationMatcher().doMatch(learnerSolution.getAssociations(), musterSolution.getAssociations())
  val implementationResult = new UmlImplementationMatcher().doMatch(learnerSolution.getImplementations(), musterSolution.getImplementations())

  override val classResult = new UmlClassMatcher(true).doMatch(learnerSolution.getClasses(), musterSolution.getClasses())
}
