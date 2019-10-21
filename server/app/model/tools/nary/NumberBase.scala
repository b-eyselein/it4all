package model.tools.nary

import enumeratum.{EnumEntry, PlayEnum}
import play.twirl.api.Html

import scala.collection.immutable

sealed abstract class NumberBase(val base: Int, val baseName: String, val mark: String, val regexStart: String, val regexRest: String) extends EnumEntry {

  val htmlPattern: String = "[\\s" + regexRest + "]+"

  val pluralName: String = baseName + "zahlen"

  val regex: String = "-?" + mark + "[" + regexStart + "][" + regexRest + "]*"

  val singularName: String = baseName + "zahl"

  val systemName: String = baseName + "system"

}

object NumberBase extends PlayEnum[NumberBase] {

  override val values: immutable.IndexedSeq[NumberBase] = findValues

  case object Binary extends NumberBase(2, "Binär", "0b", "0-1", "0-1")

  case object Octal extends NumberBase(8, "Oktal", "0o", "1-7", "0-7")

  case object HexaDecimal extends NumberBase(16, "Hexadezimal", "0x", "1-9a-fA-F", "0-9a-fA-F")

  case object Decimal extends NumberBase(10, "Dezimal", "", "1-9", "0-9")

}
