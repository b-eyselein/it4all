package model.tools

import enumeratum.{EnumEntry, PlayEnum}

sealed trait ToolState extends EnumEntry

object ToolState extends PlayEnum[ToolState] {

  override val values: IndexedSeq[ToolState] = findValues

  case object PRE_ALPHA extends ToolState

  case object ALPHA extends ToolState

  case object BETA extends ToolState

  case object LIVE extends ToolState

}
