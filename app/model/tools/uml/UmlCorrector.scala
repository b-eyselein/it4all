package model.tools.uml

import model.points._
import model.tools.uml.UmlTool.UmlExercise
import model.tools.uml.matcher.{UmlAssociationMatcher, UmlClassMatcher, UmlImplementationMatcher}

import scala.util.{Failure, Success, Try}

object UmlCorrector {

  /*
   * FIXME: compare against every sample solution, take best?
   */
  def correct(
    userClassDiagram: UmlClassDiagram,
    exercise: UmlExercise,
    part: UmlExPart
  ): Try[UmlResult] = exercise.content.sampleSolutions.headOption match {
    case None => Failure(new Exception("There is no sample solution!"))
    case Some(sampleClassDiagram) =>
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

      // TODO: points and maxPoints!
      Success(UmlResult(classResult, assocResult, implResult, points = (-1).points, maxPoints = (-1).points))
  }

}
