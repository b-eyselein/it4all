package model.tools.xml

import enumeratum.{EnumEntry, PlayEnum}
import model.tools._

sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart

object XmlExParts extends ExParts[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues

  case object GrammarCreationXmlPart extends XmlExPart("Grammatik", "grammar")

  case object DocumentCreationXmlPart extends XmlExPart("Dokument", "document")

}

sealed trait XmlExTag extends EnumEntry

case object XmlExTag extends PlayEnum[XmlExTag] {

  override val values: IndexedSeq[XmlExTag] = findValues

  case object XmlExTagTodo extends XmlExTag

}

final case class XmlSolution(document: String, grammar: String)

final case class XmlExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topics: Seq[Topic],
  difficulty: Int,
  sampleSolutions: Seq[SampleSolution[XmlSolution]],
  content: XmlExerciseContent
) extends Exercise[XmlSolution, XmlExerciseContent]

final case class XmlExerciseContent(
  grammarDescription: String,
  rootNode: String
)
