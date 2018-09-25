package model.uml

import enumeratum.{EnumEntry, PlayEnum}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class UmlExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object UmlExParts extends PlayEnum[UmlExPart] {

  val values: IndexedSeq[UmlExPart] = findValues

  case object ClassSelection extends UmlExPart(partName = "Klassenwahl", urlName = "class_selection")

  case object DiagramDrawingHelp extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagram_drawing_help")

  case object DiagramDrawing extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagram_drawing")

  case object MemberAllocation extends UmlExPart(partName = "Zuordnung der Member", urlName = "member_allocation")

}
