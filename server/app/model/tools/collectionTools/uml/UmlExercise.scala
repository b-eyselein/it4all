package model.tools.collectionTools.uml

import model._
import model.points.Points


final case class UmlExercise(
  id: Int, collectionId: Int, toolId: String = UmlConsts.toolId, semanticVersion: SemanticVersion,
  title: String, author: String, text: LongText, state: ExerciseState,
  toIgnore: Seq[String],
  mappings: Map[String, String],
  sampleSolutions: Seq[UmlSampleSolution]
) extends Exercise {

  override protected type SolutionType = UmlClassDiagram
  override protected type SampleSolutionType = UmlSampleSolution


  def titleForPart(part: UmlExPart): String = part match {
    case UmlExParts.ClassSelection     => "Auswahl der Klassen"
    case UmlExParts.DiagramDrawing     => "Freies Zeichnen"
    case UmlExParts.DiagramDrawingHelp => "Modellierung der Beziehungen"
    case UmlExParts.MemberAllocation   => "Zuordnung der Member"
  }

  def markedText: String = UmlExTextProcessor.parseText(text.wrapped, mappings, toIgnore)

  def getDefaultClassDiagForPart(part: UmlExPart): UmlClassDiagram = {
    val assocs: Seq[UmlAssociation]    = Seq[UmlAssociation]()
    val impls : Seq[UmlImplementation] = Seq[UmlImplementation]()

    val classes: Seq[UmlClass] = part match {
      case UmlExParts.DiagramDrawingHelp => sampleSolutions.head.sample.classes.map {
        oldClass => UmlClass(oldClass.classType, oldClass.name, attributes = Seq[UmlAttribute](), methods = Seq[UmlMethod]())
      }
      case _                             => Seq[UmlClass]()
    }

    UmlClassDiagram(classes, assocs, impls)
  }

  def allAttributes: Seq[UmlAttribute] = allDistinctMembers(_.attributes)

  def allMethods: Seq[UmlMethod] = allDistinctMembers(_.methods)

  private def allDistinctMembers[M <: UmlClassMember](members: UmlClass => Seq[M]): Seq[M] = sampleSolutions.headOption match {
    case None         => Seq.empty
    case Some(sample) => sample.sample.classes.flatMap(members).distinct
  }

}


// Table classes

final case class UmlSampleSolution(id: Int, sample: UmlClassDiagram)
  extends SampleSolution[UmlClassDiagram]

final case class UmlUserSolution(id: Int, part: UmlExPart, solution: UmlClassDiagram, points: Points, maxPoints: Points)
  extends UserSolution[UmlExPart, UmlClassDiagram]

final case class UmlExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
