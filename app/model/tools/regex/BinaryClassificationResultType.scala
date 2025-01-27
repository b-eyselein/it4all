package model.tools.regex

import enumeratum.{Enum, EnumEntry}

sealed abstract class BinaryClassificationResultType(val correct: Boolean) extends EnumEntry

object BinaryClassificationResultType extends Enum[BinaryClassificationResultType] {

  case object TruePositive  extends BinaryClassificationResultType(correct = true)
  case object FalsePositive extends BinaryClassificationResultType(correct = false)
  case object FalseNegative extends BinaryClassificationResultType(correct = false)
  case object TrueNegative  extends BinaryClassificationResultType(correct = true)

  val values: IndexedSeq[BinaryClassificationResultType] = findValues

}
