package model.regex

import enumeratum.{EnumEntry, PlayEnum}
import model.{Consts, ExPart}

import scala.collection.immutable.IndexedSeq

object RegexConsts extends Consts {

  val dataName: String = "data"

  val includedName: String = "included"

  val resultTypeName: String = "resultType"

  val testDataName: String = "testData"

}


sealed abstract class RegexExPart(val partName: String, val urlName: String) extends EnumEntry with ExPart

object RegexExParts extends PlayEnum[RegexExPart] {

  val values: IndexedSeq[RegexExPart] = findValues


  case object RegexSingleExPart extends RegexExPart(partName = "Ausdruck erstellen", urlName = "regex")

}