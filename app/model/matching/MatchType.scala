package model.matching

import enumeratum.{EnumEntry, PlayEnum}

sealed trait MatchType extends EnumEntry

object MatchType extends PlayEnum[MatchType] {

  override val values: IndexedSeq[MatchType] = findValues

  case object SUCCESSFUL_MATCH extends MatchType

  case object PARTIAL_MATCH extends MatchType

  case object UNSUCCESSFUL_MATCH extends MatchType

}
