package model.core.matching

import enumeratum.{EnumEntry, PlayEnum}


sealed abstract class MatchType(val bsClass: String) extends EnumEntry


object MatchType extends PlayEnum[MatchType] {

  override val values: IndexedSeq[MatchType] = findValues


  case object SUCCESSFUL_MATCH extends MatchType("success")

  case object PARTIAL_MATCH extends MatchType("warning")

  case object UNSUCCESSFUL_MATCH extends MatchType("danger")

  case object ONLY_USER extends MatchType("danger")

  case object ONLY_SAMPLE extends MatchType("danger")

}
