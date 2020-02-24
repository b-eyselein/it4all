package model.tools.collectionTools.regex

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable.IndexedSeq

sealed abstract class BinaryClassificationResultType(val correct: Boolean) extends EnumEntry

object BinaryClassificationResultTypes extends PlayEnum[BinaryClassificationResultType] {

  val values: IndexedSeq[BinaryClassificationResultType] = findValues

  case object TruePositive extends BinaryClassificationResultType(correct = true)

  case object FalsePositive extends BinaryClassificationResultType(correct = false)

  case object FalseNegative extends BinaryClassificationResultType(correct = false)

  case object TrueNegative extends BinaryClassificationResultType(correct = true)

}
