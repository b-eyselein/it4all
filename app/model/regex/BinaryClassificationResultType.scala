package model.regex

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable.IndexedSeq

sealed trait BinaryClassificationResultType extends EnumEntry

object BinaryClassificationResultTypes extends PlayEnum[BinaryClassificationResultType] {

  val values: IndexedSeq[BinaryClassificationResultType] = findValues

  case object TruePositive extends BinaryClassificationResultType

  case object FalsePositive extends BinaryClassificationResultType

  case object FalseNegative extends BinaryClassificationResultType

  case object TrueNegative extends BinaryClassificationResultType

}