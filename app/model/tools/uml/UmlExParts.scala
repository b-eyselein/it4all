package model.tools.uml

import model.tools.{ExPart, ExParts}

sealed abstract class UmlExPart(val partName: String, val urlName: String) extends ExPart

object UmlExParts extends ExParts[UmlExPart] {

  val values: IndexedSeq[UmlExPart] = findValues

  case object ClassSelection extends UmlExPart(partName = "Klassenwahl", urlName = "classSelection")

  case object DiagramDrawingHelp extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagramDrawingHelp")

  case object DiagramDrawing extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagramDrawing")

  case object MemberAllocation extends UmlExPart(partName = "Zuordnung der Member", urlName = "memberAllocation")

}
