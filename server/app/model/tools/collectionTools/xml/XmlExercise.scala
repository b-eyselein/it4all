package model.tools.collectionTools.xml

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
  id: Int, collectionId: Int, toolId: String = XmlConsts.toolId, semanticVersion: SemanticVersion,
  title: String, author: String, text: LongText, state: ExerciseState,
  grammarDescription: LongText,
  rootNode: String,
  sampleSolutions: Seq[XmlSampleSolution]
) extends Exercise {

  override protected type SolutionType = XmlSolution
  override protected type SampleSolutionType = XmlSampleSolution

}

final case class XmlSolution(document: String, grammar: String)

final case class XmlSampleSolution(id: Int, sample: XmlSolution)
  extends SampleSolution[XmlSolution]

final case class XmlUserSolution(id: Int, part: XmlExPart, solution: XmlSolution, points: Points, maxPoints: Points)
  extends UserSolution[XmlExPart, XmlSolution]

final case class XmlExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
