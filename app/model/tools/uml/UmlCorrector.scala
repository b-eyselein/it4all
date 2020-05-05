package model.tools.uml

import model.Exercise
import model.points._
import model.tools.uml.matcher.{UmlAssociationMatcher, UmlClassMatcher, UmlImplementationMatcher}
import model.tools.AbstractCorrector
import play.api.Logger

object UmlCorrector extends AbstractCorrector {

  override type AbstractResult = UmlAbstractResult

  override protected val logger: Logger = Logger(UmlCorrector.getClass)

  override protected def buildInternalError(
    msg: String,
    solutionSaved: Boolean,
    maxPoints: Points
  ): UmlInternalErrorResult = UmlInternalErrorResult(msg, solutionSaved, maxPoints)

  /*
   * FIXME: compare against every sample solution, take best?
   */
  def correct(
    userClassDiagram: UmlClassDiagram,
    exercise: Exercise[UmlClassDiagram, UmlExerciseContent],
    part: UmlExPart,
    solutionSaved: Boolean
  ): UmlAbstractResult = exercise.content.sampleSolutions.headOption match {
    case None => onError("There is no sample solution!", solutionSaved)
    case Some(sampleSolution) =>
      val sampleClassDiagram = sampleSolution.sample

      val classResult = part match {
        case UmlExPart.DiagramDrawingHelp => None

        case UmlExPart.ClassSelection =>
          Some(UmlClassMatcher(false).doMatch(userClassDiagram.classes, sampleClassDiagram.classes))

        case UmlExPart.DiagramDrawing | UmlExPart.MemberAllocation =>
          Some(UmlClassMatcher(true).doMatch(userClassDiagram.classes, sampleClassDiagram.classes))
      }

      val assocResult = part match {
        case UmlExPart.DiagramDrawingHelp | UmlExPart.DiagramDrawing =>
          Some(UmlAssociationMatcher.doMatch(userClassDiagram.associations, sampleClassDiagram.associations))

        case _ => None
      }

      val implResult = part match {
        case UmlExPart.DiagramDrawingHelp | UmlExPart.DiagramDrawing =>
          Some(UmlImplementationMatcher.doMatch(userClassDiagram.implementations, sampleClassDiagram.implementations))

        case _ => None
      }

      val points: Points = (-1).points

      val maxPoints: Points = (-1).points

      UmlResult(classResult, assocResult, implResult, points, maxPoints, solutionSaved)
  }

}
