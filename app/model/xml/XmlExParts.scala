package model.xml

import enumeratum.{EnumEntry, PlayEnum}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object XmlExParts extends PlayEnum[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues

  case object GrammarCreationXmlPart extends XmlExPart("Grammatik", "grammar")

  case object DocumentCreationXmlPart extends XmlExPart("Dokument", "document")

}
