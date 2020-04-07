package model.tools.uml

import model.tools.{ExerciseContent, SampleSolution}

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
