package model.core.matching

import enumeratum.{EnumEntry, PlayEnum}

import scala.collection.immutable.IndexedSeq


sealed abstract class MatchType(val glyphicon: String, val bsClass: String) extends EnumEntry


object MatchType extends PlayEnum[MatchType] {

  override val values: IndexedSeq[MatchType] = findValues

  case object SUCCESSFUL_MATCH extends MatchType("ok", "success")

  case object PARTIAL_MATCH extends MatchType("question-sign", "warning")

  case object UNSUCCESSFUL_MATCH extends MatchType("exclamation-sign", "danger")

  case object ONLY_USER extends MatchType("remove", "danger")

  case object ONLY_SAMPLE extends MatchType("minus", "danger")

}
