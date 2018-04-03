package model.uml

import model.core.ExPart


sealed abstract class UmlExPart(val partName: String, val urlName: String) extends ExPart


case object ClassSelection extends UmlExPart(partName = "Klassenwahl", urlName = "class_selection")

case object DiagramDrawingHelp extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagram_drawing_help")

case object DiagramDrawing extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagram_drawing")

case object MemberAllocation extends UmlExPart(partName = "Zuordnung der Member", urlName = "member_allocation")


object UmlExParts {

  val values = Seq(ClassSelection, DiagramDrawingHelp, DiagramDrawing, MemberAllocation)

}
