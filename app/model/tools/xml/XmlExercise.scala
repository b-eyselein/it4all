package model.tools.xml

import enumeratum.{EnumEntry, PlayEnum}
import model.tools._

sealed abstract class XmlExPart(val partName: String, val id: String) extends ExPart

object XmlExPart extends ExParts[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues

  case object GrammarCreationXmlPart extends XmlExPart(partName = "Grammatik", id = "grammar")

  case object DocumentCreationXmlPart extends XmlExPart(partName = "Dokument", id = "document")

}

sealed trait XmlExTag extends EnumEntry

case object XmlExTag extends PlayEnum[XmlExTag] {

  override val values: IndexedSeq[XmlExTag] = findValues

  case object XmlExTagTodo extends XmlExTag

}

final case class XmlSolution(document: String, grammar: String)

final case class XmlExerciseContent(
  grammarDescription: String,
  rootNode: String,
  sampleSolutions: Seq[SampleSolution[XmlSolution]]
) extends ExerciseContent[XmlSolution] {

  override def parts: Seq[ExPart] = XmlExPart.values

}
