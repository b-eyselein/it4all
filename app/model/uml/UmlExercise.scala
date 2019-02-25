package model.uml

import model._
import play.twirl.api.Html

import scala.language.postfixOps

// Classes for use

final case class UmlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String,
                             state: ExerciseState, markedText: String, toIgnore: Seq[String], mappings: Map[String, String], sampleSolutions: Seq[UmlSampleSolution])
  extends SingleExercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.uml.umlPreview(this)

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

final case class UmlSampleSolution(id: Int, exerciseId: Int, exSemVer: SemanticVersion, sample: UmlClassDiagram)
  extends SampleSolution[UmlClassDiagram]

final case class UmlSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion, part: UmlExPart,
                             solution: UmlClassDiagram, points: Points, maxPoints: Points) extends DBPartSolution[UmlExPart, UmlClassDiagram]

final case class UmlExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: UmlExPart,
                                   difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[UmlExPart]
