package model.blanks

import controllers.exes.idPartExes.ExPart

object BlanksExParts {

  sealed abstract class BlankExPart(val partName: String, val urlName: String) extends ExPart

  case object BlankExSinglePart extends BlankExPart("Lückentext", "blanks")

  val values = Seq(BlankExSinglePart)

}
