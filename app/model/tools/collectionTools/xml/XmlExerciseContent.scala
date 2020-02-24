package model.tools.collectionTools.xml

import model.tools.collectionTools.{ExPart, ExParts, ExerciseContent, SampleSolution}

sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart

object XmlExParts extends ExParts[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues

  case object GrammarCreationXmlPart extends XmlExPart("Grammatik", "grammar")

  case object DocumentCreationXmlPart extends XmlExPart("Dokument", "document")

}

final case class XmlSolution(document: String, grammar: String)

final case class XmlExerciseContent(
  grammarDescription: String,
  rootNode: String,
  sampleSolutions: Seq[SampleSolution[XmlSolution]]
) extends ExerciseContent {

  override type SolType = XmlSolution

}
