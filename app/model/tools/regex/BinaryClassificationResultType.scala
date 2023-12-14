package model.tools.regex

import enumeratum.{EnumEntry, Enum}

sealed abstract class BinaryClassificationResultType(val correct: Boolean) extends EnumEntry

object BinaryClassificationResultTypes extends Enum[BinaryClassificationResultType] {

  case object TruePositive  extends BinaryClassificationResultType(correct = true)
  case object FalsePositive extends BinaryClassificationResultType(correct = false)
  case object FalseNegative extends BinaryClassificationResultType(correct = false)
  case object TrueNegative  extends BinaryClassificationResultType(correct = true)

  val values: IndexedSeq[BinaryClassificationResultType] = findValues

}
