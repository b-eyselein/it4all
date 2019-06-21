package model.tools.uml

import model._
import model.points.Points
import play.twirl.api.Html

final case class UmlCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class UmlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String,
                             state: ExerciseState, markedText: String, toIgnore: Seq[String], mappings: Map[String, String], sampleSolutions: Seq[UmlSampleSolution])
  extends Exercise {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.uml.umlPreview(this)

  def titleForPart(part: UmlExPart): String = part match {
    case UmlExParts.ClassSelection     => "Auswahl der Klassen"
    case UmlExParts.DiagramDrawing     => "Freies Zeichnen"
    case UmlExParts.DiagramDrawingHelp => "Modellierung der Beziehungen"
    case UmlExParts.MemberAllocation   => "Zuordnung der Member"
  }

  def textForPart(part: UmlExPart): Html = Html(part match {
    case UmlExParts.ClassSelection | UmlExParts.DiagramDrawing => markedText
    case _                                                     => text
  })

  def getDefaultClassDiagForPart(part: UmlExPart): UmlClassDiagram = {
    val assocs: Seq[UmlAssociation] = Seq[UmlAssociation]()
    val impls: Seq[UmlImplementation] = Seq[UmlImplementation]()

    val classes: Seq[UmlClass] = part match {
      case UmlExParts.DiagramDrawingHelp => sampleSolutions.head.sample.classes.map {
        oldClass => UmlClass(oldClass.classType, oldClass.className, attributes = Seq[UmlAttribute](), methods = Seq[UmlMethod](), position = oldClass.position)
      }
      case _                             => Seq[UmlClass]()
    }

    UmlClassDiagram(classes, assocs, impls)
  }

  val allAttributes: Seq[UmlAttribute] = allDistinctMembers(_.attributes)

  val allMethods: Seq[UmlMethod] = allDistinctMembers(_.methods)

  private def allDistinctMembers[M <: UmlClassMember](members: UmlClass => Seq[M]): Seq[M] = sampleSolutions.headOption match {
    case None         => Seq.empty
    case Some(sample) => sample.sample.classes flatMap members distinct
  }

}


// Table classes

final case class UmlSampleSolution(id: Int, sample: UmlClassDiagram)
  extends SampleSolution[UmlClassDiagram]

final case class UmlUserSolution(id: Int, part: UmlExPart, solution: UmlClassDiagram, points: Points, maxPoints: Points)
  extends UserSolution[UmlExPart, UmlClassDiagram]

final case class UmlExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
