package model.result

import enumeratum.{EnumEntry, PlayEnum}

sealed abstract class SuccessType extends EnumEntry

object SuccessType extends PlayEnum[SuccessType] {

  override val values: IndexedSeq[SuccessType] = findValues

  case object ERROR extends SuccessType

  case object NONE extends SuccessType

  case object PARTIALLY extends SuccessType

  case object COMPLETE extends SuccessType

}
