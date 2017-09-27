package model

import model._
import model.exercise.Success
import model.matching.ScalaMatchingResult
import model.umlmatcher._
import model.result.EvaluationResult
import scala.collection.JavaConverters._

abstract sealed class UmlResult(val exercise: UmlExercise, val learnerSolution: UmlSolution, extendedComp: Boolean)
  extends EvaluationResult(Success.NONE) {
  val musterSolution = exercise.getSolution

  val classResult = new UmlClassMatcher(extendedComp).doMatch(learnerSolution.classes, musterSolution.classes)
}

case class ClassSelectionResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l, false)

case class DiagramDrawingResult(e: UmlExercise, l: UmlSolution) extends UmlResult(e, l, true) {

  val associationResult = UmlAssociationMatcher.doMatch(learnerSolution.associations, musterSolution.associations)

  val implementationResult = UmlImplementationMatcher.doMatch(learnerSolution.implementations, musterSolution.implementations)

}
