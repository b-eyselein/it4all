package model.tools.collectionTools.xml

import model.core.LongText
import model.tools.collectionTools.{ExPart, ExParts, ExerciseContent, SampleSolution}

import scala.collection.immutable.IndexedSeq


sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart

object XmlExParts extends ExParts[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues


  case object GrammarCreationXmlPart extends XmlExPart("Grammatik", "grammar")

  case object DocumentCreationXmlPart extends XmlExPart("Dokument", "document")

}


final case class XmlSolution(document: String, grammar: String)


final case class XmlExerciseContent(
  grammarDescription: LongText,
  rootNode: String,
  sampleSolutions: Seq[SampleSolution[XmlSolution]]
) extends ExerciseContent {

  override type SolType = XmlSolution

}



//final case class XmlSampleSolution(id: Int, sample: XmlSolution)
//  extends SampleSolution[XmlSolution]

//final case class XmlUserSolution(id: Int, part: XmlExPart, solution: XmlSolution, points: Points, maxPoints: Points)
//  extends UserSolution[XmlSolution]

