package model.xml

import controllers.exes.idPartExes.ExPart

object XmlExParts {

  sealed abstract class XmlExPart(val partName: String, val urlName: String) extends ExPart

  case object XmlSingleExPart extends XmlExPart("Bearbeitung", "xml")

  val values = Seq(XmlSingleExPart)

}
