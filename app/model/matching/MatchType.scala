package model.matching

import enumeratum.{Enum, EnumEntry}

sealed trait MatchType extends EnumEntry

object MatchType extends Enum[MatchType] {

  case object SUCCESSFUL_MATCH   extends MatchType
  case object PARTIAL_MATCH      extends MatchType
  case object UNSUCCESSFUL_MATCH extends MatchType

  override val values: IndexedSeq[MatchType] = findValues

}
