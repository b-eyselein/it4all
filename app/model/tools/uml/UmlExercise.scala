package model.tools.uml

import enumeratum.{EnumEntry, PlayEnum}
import model.tools._

sealed abstract class UmlExPart(
  val partName: String,
  val id: String,
  override val isEntryPart: Boolean = true
) extends ExPart

object UmlExPart extends ExParts[UmlExPart] {

  val values: IndexedSeq[UmlExPart] = findValues

  case object ClassSelection extends UmlExPart(partName = "Klassenwahl", id = "classSelection")

  case object DiagramDrawingHelp
      extends UmlExPart(partName = "Zeichnen des Diagramms", id = "diagramDrawingHelp", isEntryPart = false)

  case object DiagramDrawing extends UmlExPart(partName = "Zeichnen des Diagramms", id = "diagramDrawing")

  case object MemberAllocation
      extends UmlExPart(partName = "Zuordnung der Member", id = "memberAllocation", isEntryPart = false)

}

sealed trait UmlExTag extends EnumEntry

case object UmlExTag extends PlayEnum[UmlExTag] {

  override val values: IndexedSeq[UmlExTag] = findValues

  case object UmlExTagTodo extends UmlExTag

}

final case class UmlExerciseContent(
  toIgnore: Seq[String],
  mappings: Map[String, String],
  sampleSolutions: Seq[SampleSolution[UmlClassDiagram]]
) extends ExerciseContent[UmlClassDiagram] {

  override def parts: Seq[ExPart] = UmlExPart.values

}
