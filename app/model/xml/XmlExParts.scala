package model.xml

import controllers.exes.idPartExes.{ExPart, ExParts}


sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart

case object DocumentCreationXmlPart extends XmlExPart("Dokument erstellen", "document")

case object GrammarCreationXmlPart extends XmlExPart("Grammatik erstellen", "grammar")


object XmlExParts extends ExParts[XmlExPart] {

  val values = Seq(GrammarCreationXmlPart, DocumentCreationXmlPart)

}
