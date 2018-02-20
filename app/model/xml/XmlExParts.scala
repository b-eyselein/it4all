package model.xml

import controllers.exes.idPartExes.{ExPart, ExParts}


sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart

case object DocumentCreationXmlPart extends XmlExPart("Bearbeitung", "xml")

case object GrammarCreationXmlPart extends XmlExPart("Grammatik", "grammar")


object XmlExParts extends ExParts[XmlExPart] {

  val values = Seq(GrammarCreationXmlPart, DocumentCreationXmlPart)

}
