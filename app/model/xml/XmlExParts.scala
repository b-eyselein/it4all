package model.xml

import enumeratum.{Enum, EnumEntry}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object XmlExParts extends Enum[XmlExPart] {

  val values: IndexedSeq[XmlExPart] = findValues

  case object GrammarCreationXmlPart extends XmlExPart("Grammatik erstellen", "grammar")

  case object DocumentCreationXmlPart extends XmlExPart("Dokument erstellen", "document")

}
