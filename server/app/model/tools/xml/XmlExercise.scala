package model.tools.xml

import enumeratum.{EnumEntry, PlayEnum}
import model._
import model.points.Points
import play.twirl.api.Html

import scala.collection.immutable.IndexedSeq


sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object XmlExParts extends PlayEnum[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues

  case object GrammarCreationXmlPart extends XmlExPart("Grammatik", "grammar")

  case object DocumentCreationXmlPart extends XmlExPart("Dokument", "document")

}


final case class XmlExercise(
  id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
  grammarDescription: String, rootNode: String, samples: Seq[XmlSampleSolution]
) extends Exercise {

  override def preview: Html = //FIXME: move to toolMain!
    views.html.toolViews.xml.xmlPreview(this)

  def getTemplate(part: XmlExPart): XmlSolution = XmlSolution(
    document =
      s"""<?xml version="1.0" encoding="UTF-8"?>
         |<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">""".stripMargin,
    grammar = s"<!ELEMENT $rootNode (EMPTY)>"
  )

}

final case class XmlSolution(document: String, grammar: String)

final case class XmlSampleSolution(id: Int, sample: XmlSolution)
  extends SampleSolution[XmlSolution]

final case class XmlUserSolution(id: Int, part: XmlExPart, solution: XmlSolution, points: Points, maxPoints: Points)
  extends UserSolution[XmlExPart, XmlSolution]

final case class XmlExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
