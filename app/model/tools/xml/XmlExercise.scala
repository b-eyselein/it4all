package model.tools.xml

import enumeratum.PlayEnum
import model.{ExPart, ExerciseContent}

sealed abstract class XmlExPart(val partName: String, val id: String) extends ExPart

object XmlExPart extends PlayEnum[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues

  case object GrammarCreationXmlPart extends XmlExPart(partName = "Grammatik", id = "grammar")

  case object DocumentCreationXmlPart extends XmlExPart(partName = "Dokument", id = "document")

}

final case class XmlSolution(document: String, grammar: String)

final case class XmlExerciseContent(
  grammarDescription: String,
  rootNode: String,
  sampleSolutions: Seq[XmlSolution]
) extends ExerciseContent {

  override protected type S = XmlSolution

  override def parts: Seq[ExPart] = XmlExPart.values

}
