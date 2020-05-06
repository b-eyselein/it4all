package model.tools.uml

import model.{ExPart, ExParts, ExerciseContent, SampleSolution}

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

final case class UmlExerciseContent(
  toIgnore: Seq[String],
  mappings: Map[String, String],
  sampleSolutions: Seq[SampleSolution[UmlClassDiagram]]
) extends ExerciseContent[UmlClassDiagram] {

  override def parts: Seq[ExPart] = UmlExPart.values

}
