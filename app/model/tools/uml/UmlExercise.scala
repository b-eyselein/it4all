package model.tools.uml

import enumeratum.PlayEnum
import model.{ExPart, ExerciseContentWithParts}

sealed abstract class UmlExPart(
  val partName: String,
  val id: String,
  override val isEntryPart: Boolean = true
) extends ExPart

object UmlExPart extends PlayEnum[UmlExPart] {

  val values: IndexedSeq[UmlExPart] = findValues

  case object ClassSelection extends UmlExPart(partName = "Klassenwahl", id = "classSelection")

  case object DiagramDrawingHelp extends UmlExPart(partName = "Zeichnen des Diagramms", id = "diagramDrawingHelp", isEntryPart = false)

  case object DiagramDrawing extends UmlExPart(partName = "Zeichnen des Diagramms", id = "diagramDrawing")

  case object MemberAllocation extends UmlExPart(partName = "Zuordnung der Member", id = "memberAllocation", isEntryPart = false)

}

final case class UmlExerciseContent(
  toIgnore: Seq[String],
  mappings: Map[String, String],
  sampleSolutions: Seq[UmlClassDiagram]
) extends ExerciseContentWithParts {

  override protected type S = UmlClassDiagram

  override def parts: Seq[ExPart] = UmlExPart.values

}
