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
  semanticVersion: SemanticVersion,
  title: String,
  authors: Seq[String],
  text: String,
  tags: Seq[UmlExTag],
  difficulty: Option[Int],
  sampleSolutions: Seq[SampleSolution[UmlClassDiagram]],
  toIgnore: Seq[String],
  mappings: Map[String, String]
) extends Exercise {

  override type ET      = UmlExTag
  override type SolType = UmlClassDiagram

}

final case class UmlExerciseContent(
  toIgnore: Seq[String],
  mappings: Map[String, String],
  sampleSolutions: Seq[SampleSolution[UmlClassDiagram]]
) extends ExerciseContent {

  override type SolType = UmlClassDiagram

  def titleForPart(part: UmlExPart): String = part match {
    case UmlExParts.ClassSelection     => "Auswahl der Klassen"
    case UmlExParts.DiagramDrawing     => "Freies Zeichnen"
    case UmlExParts.DiagramDrawingHelp => "Modellierung der Beziehungen"
    case UmlExParts.MemberAllocation   => "Zuordnung der Member"
  }

  //  def markedText: String = UmlExTextProcessor.parseText(text.wrapped, mappings, toIgnore)

  def getDefaultClassDiagForPart(part: UmlExPart): UmlClassDiagram = {
    val assocs: Seq[UmlAssociation]   = Seq[UmlAssociation]()
    val impls: Seq[UmlImplementation] = Seq[UmlImplementation]()

    val classes: Seq[UmlClass] = part match {
      case UmlExParts.DiagramDrawingHelp =>
        sampleSolutions.head.sample.classes.map { oldClass =>
          UmlClass(oldClass.classType, oldClass.name, attributes = Seq[UmlAttribute](), methods = Seq[UmlMethod]())
        }
      case _ => Seq[UmlClass]()
    }

    UmlClassDiagram(classes, assocs, impls)
  }

  def allAttributes: Seq[UmlAttribute] = allDistinctMembers(_.attributes)

  def allMethods: Seq[UmlMethod] = allDistinctMembers(_.methods)

  private def allDistinctMembers[M <: UmlClassMember](members: UmlClass => Seq[M]): Seq[M] =
    sampleSolutions.headOption match {
      case None         => Seq.empty
      case Some(sample) => sample.sample.classes.flatMap(members).distinct
    }

}
