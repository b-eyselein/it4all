package model.web

import model.core.{ExPart, ExParts}


sealed abstract class WebExPart(val partName: String, val urlName: String) extends ExPart


case object HtmlPart extends WebExPart("Html-Teil", "html")

case object JsPart extends WebExPart("Js-Teil", "js")

case object PHPPart extends WebExPart("PHP-Teil", "php")


object WebExParts extends ExParts[WebExPart] {

  val values = Seq(HtmlPart, JsPart)

}
