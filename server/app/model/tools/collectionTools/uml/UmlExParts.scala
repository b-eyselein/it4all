package model.tools.collectionTools.uml

import model.{ExPart, ExParts}

import scala.collection.immutable.IndexedSeq


sealed abstract class UmlExPart(val partName: String, val urlName: String) extends ExPart


object UmlExParts extends ExParts[UmlExPart] {

  val values: IndexedSeq[UmlExPart] = findValues


  case object ClassSelection extends UmlExPart(partName = "Klassenwahl", urlName = "class_selection")

  case object DiagramDrawingHelp extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagram_drawing_help")

  case object DiagramDrawing extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagram_drawing")

  case object MemberAllocation extends UmlExPart(partName = "Zuordnung der Member", urlName = "member_allocation")

}
