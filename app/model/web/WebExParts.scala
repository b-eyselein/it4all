package model.web

import enumeratum.{Enum, EnumEntry}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class WebExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object WebExParts extends Enum[WebExPart] {

  val values: IndexedSeq[WebExPart] = findValues

  case object HtmlPart extends WebExPart("Html-Teil", "html")

  case object JsPart extends WebExPart("Js-Teil", "js")

  case object PHPPart extends WebExPart("PHP-Teil", "php")

}
