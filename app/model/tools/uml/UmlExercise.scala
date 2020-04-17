package model.tools.uml

import enumeratum.{EnumEntry, PlayEnum}
import model.tools._

sealed abstract class UmlExPart(val partName: String, val urlName: String) extends ExPart

object UmlExParts extends ExParts[UmlExPart] {

  val values: IndexedSeq[UmlExPart] = findValues

  case object ClassSelection extends UmlExPart(partName = "Klassenwahl", urlName = "classSelection")

  case object DiagramDrawingHelp extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagramDrawingHelp")

  case object DiagramDrawing extends UmlExPart(partName = "Zeichnen des Diagramms", urlName = "diagramDrawing")

  case object MemberAllocation extends UmlExPart(partName = "Zuordnung der Member", urlName = "memberAllocation")

}

sealed trait UmlExTag extends EnumEntry

case object UmlExTag extends PlayEnum[UmlExTag] {

  override val values: IndexedSeq[UmlExTag] = findValues

  case object UmlExTagTodo extends UmlExTag

}

final case class UmlExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topics: Seq[Topic],
  difficulty: Int,
  sampleSolutions: Seq[SampleSolution[UmlClassDiagram]],
  content: UmlExerciseContent
) extends Exercise[UmlClassDiagram, UmlExerciseContent]

final case class UmlExerciseContent(
  toIgnore: Seq[String],
  mappings: Map[String, String]
)
