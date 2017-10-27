package model.nary

import scala.util.Random

sealed abstract class NumberBase(val base: Int, baseName: String, mark: String, regexStart: String, regexRest: String) {

  def htmlPattern: String = s"""[\s$regexStart][\s$regexRest]*"""

  def pluralName: String = baseName + "zahlen"

  def regex: String = s"-?$mark[$regexStart][$regexRest]*"

  def singularName: String = baseName + "zahl"

  def systemName: String = baseName + "system"

}

object NumberBase {
  def takeRandom(): NumberBase = Random.nextInt(3) match {
    case 0 => BINARY
    case 1 => OCTAL
    case 2 => HEXADECIMAL
    case 3 => DECIMAL
  }

  def valueOf(str: String): NumberBase = str match {
    case "BINARY"      => BINARY
    case "OCTAL"       => OCTAL
    case "HEXADECIMAL" => HEXADECIMAL
    case "DECIMAL"     => DECIMAL
    case _             => throw new IllegalArgumentException(s"No value for $str!")
  }
}

case object BINARY extends NumberBase(2, "Binaer", "0b", "0-1", "0-1")

case object OCTAL extends NumberBase(8, "Oktal", "0o", "1-7", "0-7")

case object HEXADECIMAL extends NumberBase(16, "Hexadezimal", "0x", "1-9a-fA-F", "0-9a-fA-F")

case object DECIMAL extends NumberBase(10, "Dezimal", "", "1-9", "0-9")
