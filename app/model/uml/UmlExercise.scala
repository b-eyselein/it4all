package model.uml

import model.uml.UmlConsts._
import model.{Exercise, ExerciseState, PartSolution, PartsCompleteEx}
import play.twirl.api.Html

import scala.language.postfixOps

// Classes for use

case class UmlCompleteEx(ex: UmlExercise, mappings: Seq[UmlMapping])
  extends PartsCompleteEx[UmlExercise, UmlExPart] {

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
    val assocs: Seq[UmlAssociation] = Seq.empty
    val impls: Seq[UmlImplementation] = Seq.empty

    val classes: Seq[UmlClass] = part match {
      case UmlExParts.DiagramDrawingHelp => ex.solution.classes.map {
        oldClass => UmlClass(oldClass.classType, oldClass.className, attributes = Seq.empty, methods = Seq.empty, position = oldClass.position)
      }
      case _                             => Seq.empty
    }

    UmlClassDiagram(classes, assocs, impls)
  }

  val allAttributes: Seq[UmlAttribute] = allDistinctMembers(_.attributes)

  val allMethods: Seq[UmlMethod] = allDistinctMembers(_.methods)

  private def allDistinctMembers[M <: UmlClassMember](members: UmlClass => Seq[M]): Seq[M] = ex.solution.classes flatMap members distinct

}


// Table classes

case class UmlExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, solution: UmlClassDiagram, markedText: String, toIgnore: String)
  extends Exercise {

  def splitToIgnore: Seq[String] = toIgnore split tagJoinChar

}

// FIXME: save ignore words and mappings as json!?!
case class UmlMapping(exerciseId: Int, key: String, value: String)

case class UmlSolution(username: String, exerciseId: Int, part: UmlExPart, solution: UmlClassDiagram, points: Double, maxPoints: Double)
  extends PartSolution[UmlExPart, UmlClassDiagram]
