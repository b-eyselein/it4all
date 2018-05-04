package model.core.matching

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq


sealed abstract class MatchType(val glyphicon: String, val bsClass: String) extends EnumEntry


object MatchType extends Enum[MatchType] {

  override val values: IndexedSeq[MatchType] = findValues

  case object FAILURE extends MatchType("warning-sign", "danger")

  case object SUCCESSFUL_MATCH extends MatchType("ok", "success")

  case object PARTIAL_MATCH extends MatchType("question-sign", "warning")

  case object UNSUCCESSFUL_MATCH extends MatchType("exclamation-sign", "danger")

  case object ONLY_USER extends MatchType("remove", "danger")

  case object ONLY_SAMPLE extends MatchType("minus", "danger")

}
