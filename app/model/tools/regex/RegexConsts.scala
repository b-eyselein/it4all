package model.tools.regex

import enumeratum.{EnumEntry, PlayEnum}
import model.{Consts, ExPart}

import scala.collection.immutable.IndexedSeq

object RegexConsts extends Consts {

  val correctionTypeName: String = "correctionType"

  val dataName: String = "data"

  val extractionTestDataName: String = "extractionTestData"

  val includedName: String = "included"

  val resultTypeName: String = "resultType"

  val matchTestDataName: String = "matchTestData"

}


sealed abstract class RegexExPart(val partName: String, val urlName: String) extends EnumEntry with ExPart

object RegexExParts extends PlayEnum[RegexExPart] {

  val values: IndexedSeq[RegexExPart] = findValues


  case object RegexSingleExPart extends RegexExPart(partName = "Ausdruck erstellen", urlName = "regex")

}
