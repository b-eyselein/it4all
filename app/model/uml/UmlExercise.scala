package model.uml

import model._
import model.uml.UmlConsts._
import play.twirl.api.Html

import scala.language.postfixOps

// Classes for use

final case class UmlCompleteEx(ex: UmlExercise, mappings: Seq[UmlMapping]) extends SingleCompleteEx[UmlExercise, UmlExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.uml.umlPreview(this)

  def titleForPart(part: UmlExPart): String = part match {
    case UmlExParts.ClassSelection     => "Auswahl der Klassen"
    case UmlExParts.DiagramDrawing     => "Freies Zeichnen"
    case UmlExParts.DiagramDrawingHelp => "Modellierung der Beziehungen"
    case UmlExParts.MemberAllocation   => "Zuordnung der Member"
  }

  def textForPart(part: UmlExPart): Html = Html(part match {
    case UmlExParts.ClassSelection | UmlExParts.DiagramDrawing => ex.markedText
    case _                                                     => ex.text
  })

  override def hasPart(partType: UmlExPart): Boolean = partType match {
    case UmlExParts.ClassSelection | UmlExParts.DiagramDrawing => true // TODO: Currently deactivated...
    case _                                                     => false
  }

  def getDefaultClassDiagForPart(part: UmlExPart): UmlClassDiagram = {
    val assocs: Seq[UmlAssociation] = Seq[UmlAssociation]()
    val impls: Seq[UmlImplementation] = Seq[UmlImplementation]()

    val classes: Seq[UmlClass] = part match {
      case UmlExParts.DiagramDrawingHelp => ex.solution.classes.map {
        oldClass => UmlClass(oldClass.classType, oldClass.className, attributes = Seq[UmlAttribute](), methods = Seq[UmlMethod](), position = oldClass.position)
      }
      case _                             => Seq[UmlClass]()
    }

    UmlClassDiagram(classes, assocs, impls)
  }

  val allAttributes: Seq[UmlAttribute] = allDistinctMembers(_.attributes)

  val allMethods: Seq[UmlMethod] = allDistinctMembers(_.methods)

  private def allDistinctMembers[M <: UmlClassMember](members: UmlClass => Seq[M]): Seq[M] = ex.solution.classes flatMap members distinct

}


// Table classes

final case class UmlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                             solution: UmlClassDiagram, markedText: String, toIgnore: String) extends Exercise {

  def splitToIgnore: Seq[String] = toIgnore split tagJoinChar

}

// FIXME: save ignore words and mappings as json!?!
final case class UmlMapping(exerciseId: Int, exSemVer: SemanticVersion, key: String, value: String)

final case class UmlSolution(username: String, exerciseId: Int, exSemVer: SemanticVersion, part: UmlExPart, solution: UmlClassDiagram,
                             points: Points, maxPoints: Points) extends DBPartSolution[UmlExPart, UmlClassDiagram]

final case class UmlExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: UmlExPart,
                                   difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[UmlExPart]