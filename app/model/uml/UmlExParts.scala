package model.uml

import controllers.exes.idPartExes.ExPart


sealed abstract class UmlExPart(val partName: String, val urlName: String) extends ExPart

case object ClassSelection extends UmlExPart("Klassenwahl", "class_selection")

case object DiagramDrawingHelp extends UmlExPart("Zeichnen des Diagramms", "diagram_drawing_help")

case object DiagramDrawing extends UmlExPart("Zeichnen des Diagramms", "diagram_drawing")

case object MemberAllocation extends UmlExPart("Zuordnung der Member", "member_allocation")


object UmlExParts {

  val values = Seq(ClassSelection, DiagramDrawingHelp, DiagramDrawing, MemberAllocation)

}
