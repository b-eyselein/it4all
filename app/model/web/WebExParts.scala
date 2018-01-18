package model.web

import controllers.exes.idPartExes.ExPart

object WebExParts {

  sealed abstract class WebExPart(val partName: String, val urlName: String) extends ExPart

  case object HtmlPart extends WebExPart("Html-Teil", "html")

  case object JsPart extends WebExPart("Js-Teil", "js")

  val values = Seq(HtmlPart, JsPart)

}
