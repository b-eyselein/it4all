package model.tools.collectionTools.regex

import model.tools.collectionTools.{ExPart, ExParts}
import model.tools.{ToolConsts, ToolState}

import scala.collection.immutable.IndexedSeq

object RegexConsts extends ToolConsts {

  override val toolName : String    = "Reguläre Ausdrücke"
  override val toolId   : String    = "regex"
  override val toolState: ToolState = ToolState.LIVE

}


sealed abstract class RegexExPart(val partName: String, val urlName: String) extends ExPart

object RegexExParts extends ExParts[RegexExPart] {

  val values: IndexedSeq[RegexExPart] = findValues


  case object RegexSingleExPart extends RegexExPart(partName = "Ausdruck erstellen", urlName = "regex")

}
