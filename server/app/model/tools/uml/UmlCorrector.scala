package model.tools.uml

import model.core.matching.MatchingResult
import model.points._
import model.tools.uml.matcher.{UmlAssociationMatcher, UmlClassMatch, UmlClassMatcher, UmlImplementationMatcher}

object UmlCorrector {

  def correct(userClassDiagram: UmlClassDiagram, sampleClassDiagram: UmlClassDiagram, part: UmlExPart): UmlCompleteResult = {

    val classResult: Option[MatchingResult[UmlClassMatch]] = part match {
      case UmlExParts.DiagramDrawingHelp                           => None
      case UmlExParts.ClassSelection                               => Some(UmlClassMatcher(false).doMatch(userClassDiagram.classes, sampleClassDiagram.classes))
      case UmlExParts.DiagramDrawing | UmlExParts.MemberAllocation => Some(UmlClassMatcher(true).doMatch(userClassDiagram.classes, sampleClassDiagram.classes))
    }

    val assocResult = part match {
      case UmlExParts.DiagramDrawingHelp | UmlExParts.DiagramDrawing => Some(UmlAssociationMatcher.doMatch(userClassDiagram.associations, sampleClassDiagram.associations))
      case _                                                         => None
    }

    val implResult = part match {
      case UmlExParts.DiagramDrawingHelp | UmlExParts.DiagramDrawing => Some(UmlImplementationMatcher.doMatch(userClassDiagram.implementations, sampleClassDiagram.implementations))
      case _                                                         => None
    }


    val points: Points = (-1).points

    val maxPoints: Points = (-1).points

    UmlCompleteResult(classResult, assocResult, implResult, points, maxPoints)
  }

}