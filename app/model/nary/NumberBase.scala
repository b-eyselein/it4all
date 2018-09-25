package model.nary

import enumeratum.{EnumEntry, PlayEnum}
import play.twirl.api.Html

import scala.collection.immutable

sealed abstract class NumberBase(val base: Int, val baseName: String, val mark: String, val regexStart: String, val regexRest: String) extends EnumEntry {

  // FIXME: use enumeratum!

  val htmlPattern: String = "[\\s" + regexRest + "]+"

  val pluralName: String = baseName + "zahlen"

  val regex: String = "-?" + mark + "[" + regexStart + "][" + regexRest + "]*"

  val singularName: String = baseName + "zahl"

  val systemName: String = baseName + "system"

  val dispBase: Html = new Html("<sub>" + (if (base < 10) "&nbsp; " else "") + base + "</sub>")

}

object NumberBase extends PlayEnum[NumberBase] {

  override val values: immutable.IndexedSeq[NumberBase] = findValues

  case object BINARY extends NumberBase(2, "Binär", "0b", "0-1", "0-1")

  case object OCTAL extends NumberBase(8, "Oktal", "0o", "1-7", "0-7")

  case object HEXADECIMAL extends NumberBase(16, "Hexadezimal", "0x", "1-9a-fA-F", "0-9a-fA-F")

  case object DECIMAL extends NumberBase(10, "Dezimal", "", "1-9", "0-9")


}
