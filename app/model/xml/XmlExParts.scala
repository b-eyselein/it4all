package model.xml

import controllers.exes.idPartExes.{ExPart, ExParts}


sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart

case object XmlSingleExPart extends XmlExPart("Bearbeitung", "xml")


object XmlExParts extends ExParts[XmlExPart] {

  val values = Seq(XmlSingleExPart)

}
