package model.uml

import scala.util.Try

object UmlCorrector {

  def correct(exercise: UmlCompleteEx, sol: UmlSolution, solutionSaved: Boolean): Try[UmlCompleteResult] = Try {
    sol.part match {
      case ClassSelection     => new ClassSelectionResult(exercise, sol.classDiagram, solutionSaved)
      case DiagramDrawing     => new DiagramDrawingResult(exercise, sol.classDiagram, solutionSaved)
      case DiagramDrawingHelp => new DiagramDrawingHelpResult(exercise, sol.classDiagram, solutionSaved)
      case MemberAllocation   => new AllocationResult(exercise, sol.classDiagram, solutionSaved)
    }
  }

}
