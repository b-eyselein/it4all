package model.feedback

import enumeratum.{EnumEntry, PlayEnum}

sealed abstract class Mark(val value: Int, val display: EvaluatedAspect => String) extends EnumEntry

object Mark extends PlayEnum[Mark] {

  override val values: IndexedSeq[Mark] = findValues

  case object VeryGood extends Mark(1, ea => s"Sehr ${ea.positive.toLowerCase}")

  case object Good extends Mark(2, _.positive)

  case object Neutral extends Mark(3, _.neutral)

  case object Bad extends Mark(4, _.negative)

  case object VeryBad extends Mark(5, ea => s"Sehr ${ea.negative.toLowerCase}")

  case object NoMark extends Mark(-1, _ => "Keine Angabe")

}
