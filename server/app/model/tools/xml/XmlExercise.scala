package model.tools.xml

import model._
import model.points.Points

import scala.collection.immutable.IndexedSeq


sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart

object XmlExParts extends ExParts[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues


  case object GrammarCreationXmlPart extends XmlExPart("Grammatik", "grammar")

  case object DocumentCreationXmlPart extends XmlExPart("Dokument", "document")

}


final case class XmlExercise(
  id: Int, collId: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
  grammarDescription: String, rootNode: String, samples: Seq[XmlSampleSolution]
) extends Exercise {

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
