package model.blanks

import controllers.exes.idPartExes.ExPart

object BlanksExParts {

  sealed abstract class BlanksExPart(val partName: String, val urlName: String) extends ExPart

  case object BlankExSinglePart extends BlanksExPart("Lückentext", "blanks")

  val values = Seq(BlankExSinglePart)

}
